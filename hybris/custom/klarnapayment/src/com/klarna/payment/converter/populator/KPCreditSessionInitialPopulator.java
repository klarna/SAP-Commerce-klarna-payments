package com.klarna.payment.converter.populator;

import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.customer.CustomerEmailResolutionService;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CurrencyModel;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.util.DiscountValue;
import de.hybris.platform.util.TaxValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.klarna.api.payments.model.PaymentsOrderLine;
import com.klarna.api.payments.model.PaymentsProductIdentifiers;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.data.KlarnaConfigData;
import com.klarna.payment.enums.KlarnaOrderTypeEnum;
import com.klarna.payment.facades.KPConfigFacade;
import com.klarna.payment.services.KPCurrencyConversionService;
import com.klarna.payment.util.KlarnaConversionUtils;
import com.klarna.payment.util.LogHelper;


public class KPCreditSessionInitialPopulator implements Populator<AbstractOrderModel, PaymentsSession>
{

	protected static final Logger LOG = Logger.getLogger(KPCreditSessionInitialPopulator.class);

	private static final String SALES_TAX = "Sales Tax";
	private static final String GLOBAL_DISCOUNT = "Global Discount";
	private static final int TAX_FACTOR = 10000;
	private KPConfigFacade kpConfigFacade;
	private UrlResolver<ProductModel> productModelUrlResolver;
	private PageTitleResolver pageTitleResolver;
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;
	private BaseSiteService baseSiteService;
	private ModelService modelService;
	private Populator<ProductModel, ProductData> productPrimaryImagePopulator;
	private KPCurrencyConversionService kpCurrencyConversionService;
	private CommonI18NService commonI18NService;
	private CartService cartService;

	@Override
	public void populate(final AbstractOrderModel source, final PaymentsSession target) throws ConversionException
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator ... ");
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		final KlarnaConfigData klarnaConfig = kpConfigFacade.getKlarnaConfig();
		addKlarnaCommon(source, target, klarnaConfig);
		addKlarnaOrder(source, target, klarnaConfig);
		addKlarnOrderTotal(source, target);
		//target.setOptions(getCheckoutOptions());

	}

	private void addKlarnaCommon(final AbstractOrderModel source, final PaymentsSession target,
			final KlarnaConfigData klarnaConfig)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.addKlarnaCommon ... ");
		if (source.getDeliveryAddress() != null)
		{
			target.setPurchaseCountry(source.getDeliveryAddress().getCountry().getIsocode());
			target.setLocale(getLocale(source.getDeliveryAddress().getCountry().getIsocode()));
		}
		final CurrencyModel currentCurrency = commonI18NService.getCurrentCurrency();
		target.setPurchaseCurrency(currentCurrency != null ? currentCurrency.getIsocode() : null);
	}

	private void addKlarnaOrder(final AbstractOrderModel source, final PaymentsSession target, final KlarnaConfigData klarnaConfig)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.addKlarnaOrder ... ");
		final List<PaymentsOrderLine> orderLines = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(source.getEntries()))
		{
			for (final AbstractOrderEntryModel entry : source.getEntries())
			{
				orderLines.add(getKlarnaOrderLine(entry, klarnaConfig));
			}
			if (source.getDeliveryCost() != null && source.getDeliveryMode() != null)
			{
				orderLines.add(getKlarnaShipping(source));
			}
			if (CollectionUtils.isNotEmpty(source.getGlobalDiscountValues()))
			{
				orderLines.add(getGlobalDiscount(source));
			}
			if (kpConfigFacade.isNorthAmerianKlarnaPayment())
			{
				orderLines.add(getKlarnaSalesTaxLine(source));
			}
		}
		target.setOrderLines(orderLines);
	}

	private PaymentsOrderLine getKlarnaShipping(final AbstractOrderModel order)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.getKlarnaShipping ... ");
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
		orderLine.setUnitPrice(KlarnaConversionUtils.getKlarnaLongValue(convertToPurchaseCurrencyPrice(order.getDeliveryCost())));
		orderLine.setTotalAmount(KlarnaConversionUtils.getKlarnaLongValue(convertToPurchaseCurrencyPrice(order.getDeliveryCost())));

		if (kpConfigFacade.isNorthAmerianKlarnaPayment())
		{
			orderLine.setTaxRate(Long.valueOf(0));
			orderLine.setTotalTaxAmount(Long.valueOf(0));
		}
		else
		{
			orderLine.setTaxRate(getTaxRate(getTaxValue(order.getTotalTaxValues())));
			orderLine.setTotalTaxAmount(calculateDeliveryTaxAmount(order));
		}

		return orderLine;
	}

	//	private PaymentsOptions getCheckoutOptions()
	//	{
	//		LogHelper.debugLog(LOG, "Entering getCheckoutOptions ");
	//		final KlarnaConfigData klarnaConfig = kpConfigFacade.getKlarnaConfig();
	//		final PaymentsOptions checkoutOption = new PaymentsOptions();

	//		checkoutOption.setColorBorder(klarnaConfig.getColorBorder());
	//		checkoutOption.setColorBorderSelected(klarnaConfig.getColorBorderSelected());
	//		checkoutOption.setColorText(klarnaConfig.getColorText());
	//		checkoutOption.setRadiusBorder(klarnaConfig.getRadiusborder());
	//
	//		checkoutOption.setColorButton(klarnaConfig.getColorButton());
	//		checkoutOption.setColorButtonText(klarnaConfig.getColorButtonText());
	//		checkoutOption.setColorCheckbox(klarnaConfig.getColorCheckbox());
	//		checkoutOption.setColorCheckboxCheckmark(klarnaConfig.getColorCheckboxCheckMark());
	//		checkoutOption.setColorHeader(klarnaConfig.getColorHeader());
	//		checkoutOption.setColorLink(klarnaConfig.getColorLink());

	//		return checkoutOption;
	//	}

	private PaymentsOrderLine getGlobalDiscount(final AbstractOrderModel source)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.getGlobalDiscount ... ");
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

		if (kpConfigFacade.isNorthAmerianKlarnaPayment())
		{
			orderLine.setTaxRate(Long.valueOf(0));
			orderLine.setTotalTaxAmount(Long.valueOf(0));
		}
		else
		{
			final Long taxRate = getTaxRate(getTaxValue(source.getTotalTaxValues()));
			orderLine.setTaxRate(taxRate);
			orderLine.setTotalTaxAmount(calculateGlobalTotalTaxAmount(source));
		}

		return orderLine;
	}

	protected String getCouponCodes(final AbstractOrderModel source)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.getCouponCodes ... ");
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
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.getGlobalDiscountValue ... ");
		final List<DiscountValue> discounts = source.getGlobalDiscountValues();
		double discountValue = 0.0;
		for (final DiscountValue discount : discounts)
		{
			discountValue += discount.getAppliedValue();
		}
		discountValue = -discountValue;

		return convertToPurchaseCurrencyPrice(Double.valueOf(discountValue));
	}

	private Long calculateGlobalTotalTaxAmount(final AbstractOrderModel source)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.calculateGlobalTotalTaxAmount ... ");
		final Long totalDiscount = KlarnaConversionUtils.getKlarnaLongValue(getGlobalDiscountValue(source));
		final Long taxRate = getTaxRate(getTaxValue(source.getTotalTaxValues()));
		if (kpConfigFacade.isNorthAmerianKlarnaPayment())
		{
			return Long.valueOf((totalDiscount.longValue() * taxRate.intValue()) / TAX_FACTOR);
		}
		else
		{
			return Long.valueOf(
					totalDiscount.longValue() - ((totalDiscount.longValue() * TAX_FACTOR) / (TAX_FACTOR + taxRate.intValue())));
		}
	}

	private PaymentsOrderLine getKlarnaOrderLine(final AbstractOrderEntryModel entry, final KlarnaConfigData klarnaConfig)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.getKlarnaOrderLine ... ");
		final ProductModel product = entry.getProduct();
		final Double promotionEntryValue = getPromotionEntryValue(entry);
		final PaymentsOrderLine orderLine = new PaymentsOrderLine();

		orderLine.setType(KlarnaOrderTypeEnum.PHYSICAL.getValue());
		orderLine.setReference(product.getCode());
		orderLine.setName(product.getName());
		orderLine.setQuantity(entry.getQuantity());
		orderLine.setQuantityUnit(entry.getUnit().getName());
		orderLine.setUnitPrice(KlarnaConversionUtils.getKlarnaLongValue(convertToPurchaseCurrencyPrice(entry.getBasePrice())));
		orderLine.setTotalAmount(KlarnaConversionUtils.getKlarnaLongValue(convertToPurchaseCurrencyPrice(entry.getTotalPrice())));
		//if (klarnaConfig.getProductUrlsRequired() != null && klarnaConfig.getProductUrlsRequired().booleanValue())
		//{
		final BaseSiteModel currentBaseSite = getBaseSiteService().getCurrentBaseSite();
		final String relUrl = getProductModelUrlResolver().resolve(entry.getProduct());
		final String prodUrl = getSiteBaseUrlResolutionService().getWebsiteUrlForSite(currentBaseSite, true, relUrl);
		final String mediaUrl = getSiteBaseUrlResolutionService().getMediaUrlForSite(currentBaseSite, true);
		orderLine.setProductUrl(prodUrl);
		final String imgUrl = getImageURL(entry.getProduct());
		orderLine.setImageUrl(imgUrl != null ? mediaUrl != null ? mediaUrl + imgUrl : imgUrl : null);
		//}
		final PaymentsProductIdentifiers productIdentifiers = getProductIdentifiers(entry.getProduct());
		orderLine.setProductIdentifiers(productIdentifiers);

		if (promotionEntryValue.doubleValue() > 0)
		{
			orderLine.setTotalDiscountAmount(KlarnaConversionUtils.getKlarnaLongValue(promotionEntryValue));
		}

		if (kpConfigFacade.isNorthAmerianKlarnaPayment())
		{
			orderLine.setTaxRate(Long.valueOf(0));
			orderLine.setTotalTaxAmount(Long.valueOf(0));
		}
		else
		{
			orderLine.setTaxRate(getTaxRate(getTaxValue(entry.getTaxValues())));
			orderLine.setTotalTaxAmount(calculateOrderEntryTaxAmount(entry));
		}

		return orderLine;
	}

	private String getImageURL(final ProductModel product)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.getImageURL ... ");
		String imageURL = null;
		final ProductData productData = new ProductData();
		getProductPrimaryImagePopulator().populate(product, productData);
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
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.getProductIdentifiers ... ");
		final PaymentsProductIdentifiers productIdentifiers = new PaymentsProductIdentifiers();
		productIdentifiers.setGlobalTradeItemNumber(product.getEan());
		productIdentifiers.setBrand(product.getManufacturerName());
		productIdentifiers.setManufacturerPartNumber(product.getManufacturerAID());
		final String productPath = StringEscapeUtils
				.unescapeHtml(getPageTitleResolver().resolveProductPageTitle(product.getCode()));
		productIdentifiers.setCategoryPath(StringUtils.replace(productPath, "|", ">"));
		return productIdentifiers;
	}

	protected Double getPromotionEntryValue(final AbstractOrderEntryModel entry)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.getPromotionEntryValue ... ");
		final double acctualPrice = entry.getBasePrice().doubleValue() * entry.getQuantity().intValue();
		final double promotionValue = acctualPrice - entry.getTotalPrice().doubleValue();
		return convertToPurchaseCurrencyPrice(Double.valueOf(promotionValue));
	}

	protected PaymentsOrderLine getKlarnaSalesTaxLine(final AbstractOrderModel order)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.getKlarnaSalesTaxLine ... ");
		final PaymentsOrderLine orderLine = new PaymentsOrderLine();
		orderLine.setType(KlarnaOrderTypeEnum.SALES_TAX.getValue());
		orderLine.setReference(KlarnaOrderTypeEnum.SALES_TAX.getValue());
		orderLine.setName(SALES_TAX);
		orderLine.setQuantity(Long.valueOf(1));
		orderLine.setTaxRate(Long.valueOf(0));
		orderLine.setTotalTaxAmount(Long.valueOf(0));
		orderLine.setUnitPrice(KlarnaConversionUtils.getKlarnaLongValue(convertToPurchaseCurrencyPrice(order.getTotalTax())));
		orderLine.setTotalAmount(KlarnaConversionUtils.getKlarnaLongValue(convertToPurchaseCurrencyPrice(order.getTotalTax())));

		return orderLine;
	}

	private void addKlarnOrderTotal(final AbstractOrderModel source, final PaymentsSession target)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.addKlarnOrderTotal ... ");
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
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.getLocale ... ");
		final Locale currentLocale = commonI18NService.getLocaleForLanguage(commonI18NService.getCurrentLanguage());
		return currentLocale.getLanguage() + "-" + purchaseCountry;
	}

	private TaxValue getTaxValue(final Collection<TaxValue> taxes)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.getTaxValue ... ");
		final KlarnaConfigData klarnaConfig = kpConfigFacade.getKlarnaConfig();
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
		final CurrencyModel currentCurrency = commonI18NService.getCurrentCurrency();
		return new TaxValue("Empty Tax", 0.0D, false, currentCurrency != null ? currentCurrency.getIsocode() : null);
	}

	private Long getTaxRate(final TaxValue tax)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.getTaxRate ... ");
		return KlarnaConversionUtils.getKlarnaIntValue(Double.valueOf(tax.getValue()));
	}

	private Long calculateOrderEntryTaxAmount(final AbstractOrderEntryModel entry)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.calculateOrderEntryTaxAmount ... ");
		final TaxValue tax = getTaxValue(entry.getTaxValues());
		final Double appliedValue = Double.valueOf(tax.getAppliedValue());
		return KlarnaConversionUtils.getKlarnaLongValue(convertToPurchaseCurrencyPrice(appliedValue));
	}

	private Long calculateTotalAmount(final AbstractOrderModel source)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.calculateTotalAmount ... ");
		if (kpConfigFacade.isNorthAmerianKlarnaPayment())
		{
			final double grandTotalPrice = convertToPurchaseCurrencyPrice(source.getTotalPrice()).doubleValue()
					+ convertToPurchaseCurrencyPrice(source.getTotalTax()).doubleValue();
			return KlarnaConversionUtils.getKlarnaLongValue(Double.valueOf(grandTotalPrice));

		}
		else
		{
			final double grandTotalPrice = convertToPurchaseCurrencyPrice(source.getTotalPrice()).doubleValue();
			return KlarnaConversionUtils.getKlarnaLongValue(Double.valueOf(grandTotalPrice));

		}
	}

	private Long calculateTotalTaxAmount(final AbstractOrderModel source)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.calculateTotalTaxAmount ... ");
		return KlarnaConversionUtils.getKlarnaLongValue(convertToPurchaseCurrencyPrice(source.getTotalTax()));
	}

	private Long calculateDeliveryTaxAmount(final AbstractOrderModel order)
	{
		LogHelper.debugLog(LOG, "entering KPCreditSessionInitialPopulator.calculateDeliveryTaxAmount ... ");
		final TaxValue tax = getTaxValue(order.getTotalTaxValues());
		final Long taxRate = getTaxRate(tax);
		final Long deliveryCost = KlarnaConversionUtils.getKlarnaLongValue(convertToPurchaseCurrencyPrice(order.getDeliveryCost()));
		long deliveryTaxAmount = 0L;
		if (kpConfigFacade.isNorthAmerianKlarnaPayment())
		{
			deliveryTaxAmount = deliveryCost.longValue() * taxRate.intValue() / TAX_FACTOR;
		}
		else
		{
			deliveryTaxAmount = deliveryCost.longValue() - deliveryCost.longValue() * TAX_FACTOR / (TAX_FACTOR + taxRate.intValue());
		}
		return Long.valueOf(deliveryTaxAmount);
	}

	private Double convertToPurchaseCurrencyPrice(final Double value)
	{

		return kpCurrencyConversionService.convertToPurchaseCurrencyPrice(value);

	}

	/**
	 * @param kpCurrencyConversionService
	 *           the kpCurrencyConversionService to set
	 */
	public void setKpCurrencyConversionService(final KPCurrencyConversionService kpCurrencyConversionService)
	{
		this.kpCurrencyConversionService = kpCurrencyConversionService;
	}


	/**
	 * @param kpConfigFacade
	 *           the kpConfigFacade to set
	 */
	public void setKpConfigFacade(final KPConfigFacade kpConfigFacade)
	{
		this.kpConfigFacade = kpConfigFacade;
	}

	/**
	 * @return the cartService
	 */
	public CartService getCartService()
	{
		return cartService;
	}

	/**
	 * @param cartService
	 *           the cartService to set
	 */
	public void setCartService(final CartService cartService)
	{
		this.cartService = cartService;
	}

	/**
	 * @return the customerEmailResolutionService
	 */
	public CustomerEmailResolutionService getCustomerEmailResolutionService()
	{
		return customerEmailResolutionService;
	}

	/**
	 * @param customerEmailResolutionService
	 *           the customerEmailResolutionService to set
	 */
	public void setCustomerEmailResolutionService(final CustomerEmailResolutionService customerEmailResolutionService)
	{
		this.customerEmailResolutionService = customerEmailResolutionService;
	}

	private CustomerEmailResolutionService customerEmailResolutionService;

	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}



	/**
	 * @return the productPrimaryImagePopulator
	 */
	public Populator<ProductModel, ProductData> getProductPrimaryImagePopulator()
	{
		return productPrimaryImagePopulator;
	}

	/**
	 * @param productPrimaryImagePopulator
	 *           the productPrimaryImagePopulator to set
	 */
	public void setProductPrimaryImagePopulator(final Populator<ProductModel, ProductData> productPrimaryImagePopulator)
	{
		this.productPrimaryImagePopulator = productPrimaryImagePopulator;
	}

	/**
	 * @return the modelService
	 */
	public ModelService getModelService()
	{
		return modelService;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

	/**
	 * @return the baseSiteService
	 */
	public BaseSiteService getBaseSiteService()
	{
		return baseSiteService;
	}

	/**
	 * @param baseSiteService
	 *           the baseSiteService to set
	 */
	public void setBaseSiteService(final BaseSiteService baseSiteService)
	{
		this.baseSiteService = baseSiteService;
	}

	/**
	 * @return the siteBaseUrlResolutionService
	 */
	public SiteBaseUrlResolutionService getSiteBaseUrlResolutionService()
	{
		return siteBaseUrlResolutionService;
	}

	/**
	 * @param siteBaseUrlResolutionService
	 *           the siteBaseUrlResolutionService to set
	 */
	public void setSiteBaseUrlResolutionService(final SiteBaseUrlResolutionService siteBaseUrlResolutionService)
	{
		this.siteBaseUrlResolutionService = siteBaseUrlResolutionService;
	}

	public PageTitleResolver getPageTitleResolver()
	{
		return pageTitleResolver;
	}

	public void setPageTitleResolver(final PageTitleResolver pageTitleResolver)
	{
		this.pageTitleResolver = pageTitleResolver;
	}

	public UrlResolver<ProductModel> getProductModelUrlResolver()
	{
		return productModelUrlResolver;
	}

	public void setProductModelUrlResolver(final UrlResolver<ProductModel> productModelUrlResolver)
	{
		this.productModelUrlResolver = productModelUrlResolver;
	}

	/**
	 * @return the kpConfigFacade
	 */
	public KPConfigFacade getKpConfigFacade()
	{
		return kpConfigFacade;
	}
}
