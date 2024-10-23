package com.klarna.payment.converter.populator;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.klarna.api.payments.model.PaymentsAddress;
import com.klarna.api.payments.model.PaymentsMerchantUrls;
import com.klarna.api.payments.model.PaymentsOrderLine;
import com.klarna.api.payments.model.PaymentsProductIdentifiers;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.payment.enums.KlarnaOrderTypeEnum;
import com.klarna.payment.facades.KlarnaConfigFacade;
import com.klarna.payment.util.KlarnaConversionUtils;


public class KlarnaExpCheckoutAuthPayloadPopulator implements Populator<AbstractOrderModel, PaymentsSession>
{

	protected static final Logger LOG = Logger.getLogger(KlarnaExpCheckoutAuthPayloadPopulator.class);

	private static final String INTENT_BUY = "buy";
	private static final String KLARNA_EXPCHECKOUT_AUTHORIZE_CALLBACK_URL = "klarna.expcheckout.authorize.callback.url";

	private static final String SALES_TAX = "Sales Tax";
	private static final String GLOBAL_DISCOUNT = "Global Discount";
	private static final int TAX_FACTOR = 10000;

	@Resource(name = "klarnaConfigFacade")
	private KlarnaConfigFacade klarnaConfigFacade;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "productPrimaryImagePopulator")
	private Populator<ProductModel, ProductData> productPrimaryImagePopulator;

	@Resource(name = "productModelUrlResolver")
	private UrlResolver<ProductModel> productModelUrlResolver;

	@Resource(name = "siteBaseUrlResolutionService")
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	@Resource(name = "pageTitleResolver")
	private PageTitleResolver pageTitleResolver;

	@Resource(name = "commonI18NService")
	private CommonI18NService commonI18NService;

	@Resource(name = "siteConfigService")
	private SiteConfigService siteConfigService;


	@Resource(name = "addressConverter")
	private Converter<AddressModel, AddressData> addressConverter;

	@Resource(name = "klarnaPaymentsAddressConverter")
	private Converter<AddressData, PaymentsAddress> klarnaPaymentsAddressConverter;


	@Override
	public void populate(final AbstractOrderModel source, final PaymentsSession target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setIntent(INTENT_BUY);

		// Set default purchase country. This can change when the delivery country is selected by the customer in Klarna Exp Checkout screen
		final BaseStoreModel currentStore = baseStoreService.getCurrentBaseStore();
		final List<CountryModel> deliveryCountries = new ArrayList(currentStore.getDeliveryCountries());
		target.setPurchaseCountry(deliveryCountries.get(0).getIsocode());

		target.setPurchaseCurrency(source.getCurrency().getIsocode());
		target.setLocale(getLocale(target.getPurchaseCountry()));

		target.setMerchantReference1(source.getCode());

		addKlarnaOrder(source, target);
		addKlarnOrderTotal(source, target);

		addMerchantUrls(target);

		if (source.getDeliveryAddress() != null)
		{
			final AddressData shippingAddress = addressConverter.convert(source.getDeliveryAddress());
			if (StringUtils.isEmpty(shippingAddress.getEmail()))
			{
				// Email id is mandatory
				shippingAddress.setEmail(getEmailId(source));
			}
			target.setShippingAddress(klarnaPaymentsAddressConverter.convert(shippingAddress));

		}
		if (source.getPaymentAddress() != null)
		{
			final AddressData paymentAddress = addressConverter.convert(source.getPaymentAddress());
			if (StringUtils.isEmpty(paymentAddress.getEmail()))
			{
				// Email id is mandatory
				paymentAddress.setEmail(getEmailId(source));
			}
			target.setBillingAddress(klarnaPaymentsAddressConverter.convert(paymentAddress));
		}

	}

	private void addMerchantUrls(final PaymentsSession target)
	{
		final BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
		final String authorizationCallbackRelativeUrl = siteConfigService.getProperty(KLARNA_EXPCHECKOUT_AUTHORIZE_CALLBACK_URL);
		if (StringUtils.isNotEmpty(authorizationCallbackRelativeUrl))
		{
			final String authorizationCallbackAbsoluteUrl = siteBaseUrlResolutionService.getWebsiteUrlForSite(currentBaseSite, true,
					authorizationCallbackRelativeUrl);
			final PaymentsMerchantUrls urls = new PaymentsMerchantUrls();
			urls.setAuthorization(authorizationCallbackAbsoluteUrl);
			target.setMerchantUrls(urls);
		}
	}

	private void addKlarnaOrder(final AbstractOrderModel source, final PaymentsSession target)
	{
		final List<PaymentsOrderLine> orderLines = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(source.getEntries()))
		{
			for (final AbstractOrderEntryModel entry : source.getEntries())
			{
				orderLines.add(getKlarnaOrderLine(entry, source.getCurrency().getIsocode()));
			}

			if (source.getDeliveryCost() != null && source.getDeliveryMode() != null)
			{
				orderLines.add(getKlarnaShipping(source));
			}

			if (CollectionUtils.isNotEmpty(source.getGlobalDiscountValues()))
			{
				orderLines.add(getGlobalDiscount(source));
			}
			if (klarnaConfigFacade.isNorthAmerianKlarnaPayment())
			{
				orderLines.add(getKlarnaSalesTaxLine(source));
			}
		}
		target.setOrderLines(orderLines);
	}

	private PaymentsOrderLine getGlobalDiscount(final AbstractOrderModel source)
	{
		final Double discountValue = getGlobalDiscountValue(source);
		final PaymentsOrderLine orderLine = new PaymentsOrderLine();
		orderLine.setType(KlarnaOrderTypeEnum.DISCOUNT.getValue());
		orderLine.setName(GLOBAL_DISCOUNT);
		orderLine.setQuantity(Long.valueOf(1));
		orderLine.setUnitPrice(KlarnaConversionUtils.getKlarnaLongValue(discountValue));
		orderLine.setTotalAmount(KlarnaConversionUtils.getKlarnaLongValue(discountValue));

		if (StringUtils.isNotEmpty(getCouponCodes(source)))
		{
			orderLine.setReference(getCouponCodes(source));
		}

		if (klarnaConfigFacade.isNorthAmerianKlarnaPayment())
		{
			orderLine.setTaxRate(Long.valueOf(0));
			orderLine.setTotalTaxAmount(Long.valueOf(0));
		}
		else
		{
			final Long taxRate = getTaxRate(getTaxValue(source.getTotalTaxValues(), source.getCurrency().getIsocode()));
			orderLine.setTaxRate(taxRate);
			orderLine.setTotalTaxAmount(calculateGlobalTotalTaxAmount(source));
		}

		return orderLine;
	}

	protected String getCouponCodes(final AbstractOrderModel source)
	{
		final Collection<String> coupons = source.getAppliedCouponCodes();
		String couponCodes = "";
		if (coupons != null && !coupons.isEmpty())
		{
			for (final String coupon : coupons)
			{

				couponCodes = (couponCodes.equals("")) ? coupon : couponCodes + "|" + coupon;
			}
		}

		return couponCodes;
	}

	protected Double getGlobalDiscountValue(final AbstractOrderModel source)
	{
		final List<DiscountValue> discounts = source.getGlobalDiscountValues();
		double discountValue = 0.0;
		for (final DiscountValue discount : discounts)
		{
			discountValue += discount.getAppliedValue();
		}
		discountValue = -discountValue;

		return Double.valueOf(discountValue);
	}

	private Long calculateGlobalTotalTaxAmount(final AbstractOrderModel source)
	{
		final Long totalDiscount = KlarnaConversionUtils.getKlarnaLongValue(getGlobalDiscountValue(source));
		final Long taxRate = getTaxRate(getTaxValue(source.getTotalTaxValues(), source.getCurrency().getIsocode()));
		if (klarnaConfigFacade.isNorthAmerianKlarnaPayment())
		{
			return Long.valueOf((totalDiscount.longValue() * taxRate.intValue()) / TAX_FACTOR);
		}
		else
		{
			return Long.valueOf(
					totalDiscount.longValue() - ((totalDiscount.longValue() * TAX_FACTOR) / (TAX_FACTOR + taxRate.intValue())));
		}
	}

	private PaymentsOrderLine getKlarnaOrderLine(final AbstractOrderEntryModel entry, final String currencyCode)
	{
		final ProductModel product = entry.getProduct();
		final Double promotionEntryValue = getPromotionEntryValue(entry);
		final PaymentsOrderLine orderLine = new PaymentsOrderLine();

		orderLine.setType(KlarnaOrderTypeEnum.PHYSICAL.getValue());
		orderLine.setReference(product.getCode());
		orderLine.setName(product.getName());
		orderLine.setQuantity(entry.getQuantity());
		orderLine.setQuantityUnit(entry.getUnit().getName());
		orderLine.setUnitPrice(KlarnaConversionUtils.getKlarnaLongValue(entry.getBasePrice()));
		orderLine.setTotalAmount(KlarnaConversionUtils.getKlarnaLongValue(entry.getTotalPrice()));

		final BaseSiteModel currentBaseSite = baseSiteService.getCurrentBaseSite();
		final String relUrl = productModelUrlResolver.resolve(entry.getProduct());
		final String prodUrl = siteBaseUrlResolutionService.getWebsiteUrlForSite(currentBaseSite, true, relUrl);
		final String mediaUrl = siteBaseUrlResolutionService.getMediaUrlForSite(currentBaseSite, true);
		orderLine.setProductUrl(prodUrl);
		final String imgUrl = getImageURL(entry.getProduct());
		orderLine.setImageUrl(imgUrl != null ? mediaUrl != null ? mediaUrl + imgUrl : imgUrl : null);

		final PaymentsProductIdentifiers productIdentifiers = getProductIdentifiers(entry.getProduct());
		orderLine.setProductIdentifiers(productIdentifiers);

		if (promotionEntryValue.doubleValue() > 0)
		{
			orderLine.setTotalDiscountAmount(KlarnaConversionUtils.getKlarnaLongValue(promotionEntryValue));
		}
		else
		{
			orderLine.setTotalDiscountAmount(Long.valueOf(0));
		}

		if (klarnaConfigFacade.isNorthAmerianKlarnaPayment())
		{
			orderLine.setTaxRate(Long.valueOf(0));
			orderLine.setTotalTaxAmount(Long.valueOf(0));
		}
		else
		{
			orderLine.setTaxRate(getTaxRate(getTaxValue(entry.getTaxValues(), currencyCode)));
			orderLine.setTotalTaxAmount(calculateOrderEntryTaxAmount(entry, currencyCode));
		}

		return orderLine;
	}

	private String getImageURL(final ProductModel product)
	{
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

	/**
	 * @param product
	 * @return productIdentifiers
	 */
	private PaymentsProductIdentifiers getProductIdentifiers(final ProductModel product)
	{
		final PaymentsProductIdentifiers productIdentifiers = new PaymentsProductIdentifiers();
		productIdentifiers.setGlobalTradeItemNumber(product.getEan());
		productIdentifiers.setBrand(product.getManufacturerName());
		productIdentifiers.setManufacturerPartNumber(product.getManufacturerAID());
		final String productPath = StringEscapeUtils.unescapeHtml(pageTitleResolver.resolveProductPageTitle(product.getCode()));
		productIdentifiers.setCategoryPath(StringUtils.replace(productPath, "|", ">"));
		return productIdentifiers;
	}

	protected Double getPromotionEntryValue(final AbstractOrderEntryModel entry)
	{
		final double acctualPrice = entry.getBasePrice().doubleValue() * entry.getQuantity().intValue();
		final double promotionValue = acctualPrice - entry.getTotalPrice().doubleValue();
		return Double.valueOf(promotionValue);
	}

	protected PaymentsOrderLine getKlarnaSalesTaxLine(final AbstractOrderModel order)
	{
		final PaymentsOrderLine orderLine = new PaymentsOrderLine();
		orderLine.setType(KlarnaOrderTypeEnum.SALES_TAX.getValue());
		orderLine.setReference(KlarnaOrderTypeEnum.SALES_TAX.getValue());
		orderLine.setName(SALES_TAX);
		orderLine.setQuantity(Long.valueOf(1));
		orderLine.setTaxRate(Long.valueOf(0));
		orderLine.setTotalTaxAmount(Long.valueOf(0));
		orderLine.setUnitPrice(KlarnaConversionUtils.getKlarnaLongValue(order.getTotalTax()));
		orderLine.setTotalAmount(KlarnaConversionUtils.getKlarnaLongValue(order.getTotalTax()));

		return orderLine;
	}

	private void addKlarnOrderTotal(final AbstractOrderModel source, final PaymentsSession target)
	{
		if (CollectionUtils.isNotEmpty(source.getEntries()))
		{
			target.setOrderAmount(calculateTotalAmount(source));
			target.setOrderTaxAmount(calculateTotalTaxAmount(source));
		}
		else
		{
			target.setOrderAmount(Long.valueOf(0));
			target.setOrderTaxAmount(Long.valueOf(0));
		}
	}

	private String getLocale(final String purchaseCountry)
	{
		final Locale currentLocale = commonI18NService.getLocaleForLanguage(commonI18NService.getCurrentLanguage());
		return currentLocale.getLanguage() + "-" + purchaseCountry;
	}

	private TaxValue getTaxValue(final Collection<TaxValue> taxes, final String currencyCode)
	{
		if (taxes != null)
		{
			if (taxes.size() == 1)
			{
				final TaxValue tax = taxes.iterator().next();
				if (tax.isAbsolute())
				{
					if (LOG.isWarnEnabled())
					{
						LOG.warn("System does not accept absolute tax for the order");
					}
					throw new IllegalArgumentException("System does not accept absolute tax for the order");
				}
				return tax;

			}
			if (taxes.size() > 1)
			{
				if (LOG.isWarnEnabled())
				{
					LOG.warn("System does not accept multiple tax for the order");
				}
				throw new IllegalArgumentException("System does not accept multiple tax for the order");
			}
		}

		return new TaxValue("Empty Tax", 0.0D, false, currencyCode);
	}

	private Long getTaxRate(final TaxValue tax)
	{
		return KlarnaConversionUtils.getKlarnaIntValue(Double.valueOf(tax.getValue()));
	}

	private Long calculateOrderEntryTaxAmount(final AbstractOrderEntryModel entry, final String currencyCode)
	{
		final TaxValue tax = getTaxValue(entry.getTaxValues(), currencyCode);
		final Double appliedValue = Double.valueOf(tax.getAppliedValue());
		return KlarnaConversionUtils.getKlarnaLongValue(appliedValue);
	}

	private Long calculateTotalAmount(final AbstractOrderModel source)
	{
		if (klarnaConfigFacade.isNorthAmerianKlarnaPayment())
		{
			final double grandTotalPrice = source.getTotalPrice().doubleValue() + source.getTotalTax().doubleValue();
			return KlarnaConversionUtils.getKlarnaLongValue(Double.valueOf(grandTotalPrice));

		}
		else
		{
			final double grandTotalPrice = source.getTotalPrice().doubleValue();
			return KlarnaConversionUtils.getKlarnaLongValue(Double.valueOf(grandTotalPrice));
		}
	}

	private Long calculateTotalTaxAmount(final AbstractOrderModel source)
	{
		return KlarnaConversionUtils.getKlarnaLongValue(source.getTotalTax());
	}


	private PaymentsOrderLine getKlarnaShipping(final AbstractOrderModel order)
	{
		final PaymentsOrderLine orderLine = new PaymentsOrderLine();
		orderLine.setType(KlarnaOrderTypeEnum.SHIPPING_FEE.getValue());
		if (StringUtils.isNotEmpty(order.getDeliveryMode().getName()))
		{
			orderLine.setName(order.getDeliveryMode().getName());
		}
		else
		{
			orderLine.setName(order.getDeliveryMode().getCode());
		}

		orderLine.setReference(order.getDeliveryMode().getCode());
		orderLine.setQuantity(Long.valueOf(1));
		orderLine.setUnitPrice(KlarnaConversionUtils.getKlarnaLongValue(order.getDeliveryCost()));
		orderLine.setTotalAmount(KlarnaConversionUtils.getKlarnaLongValue(order.getDeliveryCost()));

		if (klarnaConfigFacade.isNorthAmerianKlarnaPayment())
		{
			orderLine.setTaxRate(Long.valueOf(0));
			orderLine.setTotalTaxAmount(Long.valueOf(0));
		}
		else
		{
			orderLine.setTaxRate(getTaxRate(getTaxValue(order.getTotalTaxValues(), order.getCurrency().getIsocode())));
			orderLine.setTotalTaxAmount(calculateDeliveryTaxAmount(order));
		}

		return orderLine;
	}

	private Long calculateDeliveryTaxAmount(final AbstractOrderModel order)
	{
		final TaxValue tax = getTaxValue(order.getTotalTaxValues(), order.getCurrency().getIsocode());
		final Long taxRate = getTaxRate(tax);
		final Long deliveryCost = KlarnaConversionUtils.getKlarnaLongValue(order.getDeliveryCost());
		long deliveryTaxAmount = 0L;
		if (klarnaConfigFacade.isNorthAmerianKlarnaPayment())
		{
			deliveryTaxAmount = deliveryCost.longValue() * taxRate.intValue() / TAX_FACTOR;
		}
		else
		{
			deliveryTaxAmount = deliveryCost.longValue() - deliveryCost.longValue() * TAX_FACTOR / (TAX_FACTOR + taxRate.intValue());
		}
		return Long.valueOf(deliveryTaxAmount);
	}

	private String getEmailId(final AbstractOrderModel source)
	{
		if (source.getUser() instanceof CustomerModel)
		{
			return ((CustomerModel) source.getUser()).getContactEmail();
		}
		return null;
	}
}
