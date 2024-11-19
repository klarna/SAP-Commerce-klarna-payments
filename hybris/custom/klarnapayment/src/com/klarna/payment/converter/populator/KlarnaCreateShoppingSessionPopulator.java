/*
 * [y] hybris Platform
 * 
 * Copyright (c) 2000-2024 SAP SE
 * All rights reserved.
 * 
 * This software is the confidential and proprietary information of SAP 
 * Hybris ("Confidential Information"). You shall not disclose such 
 * Confidential Information and shall use it only in accordance with the 
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.klarna.payment.converter.populator;

import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.consignmenttrackingservices.model.CarrierModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.core.model.order.delivery.DeliveryModeModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.ordersplitting.model.ConsignmentEntryModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.klarna.api.enums.PaymentAcquiringChannel;
import com.klarna.api.payments.model.PaymentsAddress;
import com.klarna.api.shoppingdata.model.ShippingData;
import com.klarna.api.shoppingdata.model.ShippingOption;
import com.klarna.api.shoppingdata.model.ShoppingCreateSessionRequest;
import com.klarna.api.shoppingdata.model.ShoppingDataContent;
import com.klarna.api.shoppingdata.model.ShoppingDataLine;
import com.klarna.api.shoppingdata.model.SupplementaryPurchaseData;
import com.klarna.payment.util.KlarnaConversionUtils;
import com.klarna.payment.util.LogHelper;

/**
 *
 */
public class KlarnaCreateShoppingSessionPopulator implements Populator<CartModel,ShoppingCreateSessionRequest>
{
	protected static final Logger LOG = Logger.getLogger(KlarnaCreateShoppingSessionPopulator.class);

	
	public static final String KLARNA_SHOPPING_SESSION_CONTENT_TYPE = "vnd.klarna.supplementary-data.v1";
	

	@Resource(name = "pageTitleResolver")
	private PageTitleResolver pageTitleResolver;
	
	@Resource(name = "productPrimaryImagePopulator")
	private Populator<ProductModel, ProductData> productPrimaryImagePopulator;
	
	@Resource(name = "siteBaseUrlResolutionService")
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
	
	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;
	
   @Override
	public void populate(CartModel cart, ShoppingCreateSessionRequest shoppingCreateSessionRequest) throws ConversionException
   {
		shoppingCreateSessionRequest.setSupplementaryPurchaseData(populateSupplementaryData(cart));
   }
   
   private SupplementaryPurchaseData populateSupplementaryData(CartModel cart){
   	SupplementaryPurchaseData purchaseData = new SupplementaryPurchaseData();
   	purchaseData.setMerchantReferences(Collections.singletonList(cart.getGuid()));
   	purchaseData.setContentType(KLARNA_SHOPPING_SESSION_CONTENT_TYPE);
   	purchaseData.setContent(getShoppingDataContent(cart));
   	return purchaseData;
   }
   
   /**
	 * 
	 */
	private ShoppingDataContent getShoppingDataContent(CartModel cart)
	{
	
   	ShoppingDataContent shoppingDataContent = new ShoppingDataContent();
   	
   	// Acquiring channel 
   	shoppingDataContent.setAcquiringChannel(PaymentAcquiringChannel.ECOMMERCE.toString());
   	
   	// Merchant Reference Cart Unique identification
   	shoppingDataContent.setMerchantReference(cart.getGuid());
   	
   	// line items 
   	List<ShoppingDataLine> lines = new ArrayList<ShoppingDataLine>();
   	for(AbstractOrderEntryModel  entry: cart.getEntries())
   	{
   		ShoppingDataLine shoppingDataLine = new ShoppingDataLine();
   		populateShoppingDataLine(entry,shoppingDataLine);
   		shoppingDataLine.setTotalTaxAmount(calculateOrderEntryTaxAmount(entry, cart.getCurrency().getIsocode()));
   		shoppingDataLine.setShippingReference(getShippingReference(cart.getConsignments(),entry)); 
   		lines.add(shoppingDataLine);
   	}
   	shoppingDataContent.setLineItems(lines);
   	
   	// Shipping 
   	ShippingData shippingData = new ShippingData();
   	shoppingDataContent.setShipping(populateShipping(cart,shippingData));
   	if(CollectionUtils.isNotEmpty(shoppingDataContent.getLineItems())) {
   		shippingData.setShippingReference( shoppingDataContent.getLineItems().get(0).getShippingReference());
   	}
   	
   	
   	// Buyer 
   	UserModel buyer = cart.getUser();
   	if(buyer != null) {
   	shoppingDataContent.setBuyer(StringUtils.isNotBlank(buyer.getName()) ? buyer.getName() : buyer.getUid());
   	}
   	
   	
   	// Subscriptions 
   	
   	// On-demand
   	
   	return shoppingDataContent;
   }
	/**
	 * @param entry 
	 * 
	 */
	private String getShippingReference(Set<ConsignmentModel> consignments, AbstractOrderEntryModel entry)
	{
		for(ConsignmentModel consignment: consignments)
		{
			for(ConsignmentEntryModel consignEntry : consignment.getConsignmentEntries()) {
				if(consignEntry.getOrderEntry() == entry) {
					return consignment.getTrackingID();
   			}
			}
		}
		return null;
	}
	
	/**
	 
	 * @param CartModel 
	 * @param shippingData 
	 * 
	 */
	private ShippingData populateShipping(CartModel cart, ShippingData shippingData)
	{
		
		AddressModel addressModel = cart.getDeliveryAddress();
		// Recipient 
		PaymentsAddress recipent = new PaymentsAddress();
		recipent.setGivenName(addressModel.getFullname());
		recipent.setFamilyName(addressModel.getLastname());
		recipent.setEmail(addressModel.getEmail());
		recipent.setPhone(addressModel.getPhone1()  != null ? addressModel.getPhone1() : addressModel.getPhone2());
		recipent.setAttention(addressModel.getBuilding());
		
		// Shipping Address
		PaymentsAddress address = new PaymentsAddress();
		address.setStreetAddress(addressModel.getStreetnumber());
		address.setStreetAddress2(addressModel.getStreetname());
		address.setPostalCode(addressModel.getPostalcode());
		address.setCity(addressModel.getCity()!= null ? addressModel.getCity().getName() : (addressModel.getCityDistrict() != null ? addressModel.getCityDistrict().getName() : ""));
		address.setRegion(addressModel.getRegion() != null ? addressModel.getRegion().getIsocode() : "" );
		address.setCountry(addressModel.getCountry() != null ? addressModel.getCountry().getIsocode() : "");
		
		// Shipping Options
		ShippingOption shippingOption = new ShippingOption();
		DeliveryModeModel deliveryModeModel = cart.getDeliveryMode();
		
		
		shippingOption.setShippingType(deliveryModeModel.getCode());
		shippingOption.setShippingTypeAttributes(Collections.singletonList(deliveryModeModel.getName()));
		shippingOption.setShippingCarrier(getShippingCarrier(cart.getConsignments()));
		
		shippingData.setRecipient(recipent);
		shippingData.setAddress(address);
		shippingData.setShippingOption(shippingOption);
		return shippingData;
	}

	/**
	 * 
	 */
	private String getShippingCarrier(Set<ConsignmentModel> consignments)
	{
		for(ConsignmentModel consignment: consignments)
		{
			CarrierModel carrierModel = consignment.getCarrierDetails();
			return carrierModel.getCode();
		}
		return null;
	}

	/**
	 * @param shoppingDataLine 
	 * 
	 */
	private void populateShoppingDataLine(AbstractOrderEntryModel entry, ShoppingDataLine shoppingDataLine)
	{
		ProductModel product = entry.getProduct();
		shoppingDataLine.setName(product.getDescription());
		shoppingDataLine.setQuantity(entry.getQuantity());
		shoppingDataLine.setTotalAmount(entry.getTotalPrice() != null ? Long.valueOf(entry.getTotalPrice().longValue()) : Long.valueOf(0));
		
		shoppingDataLine.setUnitPrice(entry.getBasePrice() != null ? Long.valueOf(entry.getBasePrice().longValue()) : Long.valueOf(0) );

		final String productPath = StringEscapeUtils.unescapeHtml(pageTitleResolver.resolveProductPageTitle(product.getCode()));
		shoppingDataLine.setProductUrl(StringUtils.replace(productPath, "|", ">"));
		
		final BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
		final String mediaUrl = siteBaseUrlResolutionService.getMediaUrlForSite(currentBaseSite, true);
		final String imgUrl = getImageURL(product);
		shoppingDataLine.setImageUrl(imgUrl != null ? mediaUrl != null ? mediaUrl + imgUrl : imgUrl : null);

		shoppingDataLine.setProductIdentifier(entry.getProduct().getCode());
		shoppingDataLine.setReference(product.getCode()+"_"+entry.getEntryNumber());
	}

	private Long calculateOrderEntryTaxAmount(final AbstractOrderEntryModel entry, final String currencyCode)
	{
		LogHelper.debugLog(LOG, "entering KlarnaCreateShoppingSessionPopulator.calculateOrderEntryTaxAmount ... ");
		final TaxValue tax = getTaxValue(entry.getTaxValues(), currencyCode);
		final Double appliedValue = Double.valueOf(tax.getAppliedValue());
		return KlarnaConversionUtils.getKlarnaLongValue(appliedValue);
	}
	
	private TaxValue getTaxValue(final Collection<TaxValue> taxes, final String currencyCode)
	{
		LogHelper.debugLog(LOG, "entering KlarnaCreateShoppingSessionPopulator.getTaxValue ... ");
		if (taxes != null)
		{
			if (taxes.size() == 1)
			{
				final TaxValue tax = taxes.iterator().next();
				if (tax.isAbsolute())
				{
					LogHelper.debugLog(LOG, "System does not accept absolute tax for the order");
					throw new IllegalArgumentException("System does not accept absolute tax for the order");
				}
				return tax;

			}
			if (taxes.size() > 1)
			{
				LogHelper.debugLog(LOG, "System does not accept multiple tax for the order");

				throw new IllegalArgumentException("System does not accept multiple tax for the order");
			}
		}

		return new TaxValue("Empty Tax", 0.0D, false, currencyCode);
	}
	
	private String getImageURL(final ProductModel product)
	{
		LogHelper.debugLog(LOG, "entering KlarnaCreateShoppingSessionPopulator.getImageURL ... ");
		String imageURL = null;
		final ProductData productData = new ProductData();
		productPrimaryImagePopulator.populate(product, productData);
		if (productData.getImages() != null)
		{
			final Iterator<ImageData> it = productData.getImages().iterator();
			while (it.hasNext())
			{
				final ImageData id = it.next();
				if ("product".equalsIgnoreCase(id.getFormat()))
				{
					return id.getUrl();
				}
				imageURL = id.getUrl();
			}
		}
		return imageURL;
	}
}
