package com.klarna.payment.converter.populator;

import de.hybris.platform.acceleratorservices.storefront.util.PageTitleResolver;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.url.UrlResolver;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import javax.annotation.Resource;

import org.springframework.util.Assert;

import com.klarna.integration.dto.KlarnaLineItemDTO;
import com.klarna.payment.facades.KlarnaConfigFacade;
import com.klarna.payment.util.KlarnaConversionUtils;
import com.klarna.payment.util.KlarnaServicesUtil;


public class KlarnaOrderLineItemPopulator implements Populator<AbstractOrderEntryModel, KlarnaLineItemDTO>
{

	@Resource
	private KlarnaServicesUtil klarnaServicesUtil;

	@Resource(name = "klarnaConfigFacade")
	private KlarnaConfigFacade klarnaConfigFacade;

	@Resource(name = "productModelUrlResolver")
	private UrlResolver<ProductModel> productModelUrlResolver;

	@Resource(name = "siteBaseUrlResolutionService")
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	@Resource(name = "pageTitleResolver")
	private PageTitleResolver pageTitleResolver;


	@Override
	public void populate(final AbstractOrderEntryModel source, final KlarnaLineItemDTO target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setQuantity(source.getQuantity());
		target.setUnitPrice(KlarnaConversionUtils.getKlarnaLongValue(source.getBasePrice()));
		target.setTotalAmount(KlarnaConversionUtils.getKlarnaLongValue(source.getTotalPrice()));

		final String currencyCode = source.getOrder().getCurrency().getIsocode();
		if (klarnaConfigFacade.isNorthAmerianKlarnaPayment())
		{
			target.setTotalTaxAmount(Long.valueOf(0));
		}
		else
		{
			target.setTotalTaxAmount(klarnaServicesUtil.getOrderEntryTaxAmount(source, currencyCode));
		}

		final ProductModel product = source.getProduct();
		target.setLineItemReference(product.getCode());
		target.setName(product.getName());
		target.setProductIdentifier(product.getCode());

		final BaseSiteModel baseSite = source.getOrder().getSite();
		final String relUrl = productModelUrlResolver.resolve(source.getProduct());
		final String prodUrl = siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, true, relUrl);
		final String mediaUrl = siteBaseUrlResolutionService.getMediaUrlForSite(baseSite, true);
		target.setProductUrl(prodUrl);
		final String imgUrl = klarnaServicesUtil.getProductImageURL(product);
		if (imgUrl != null)
		{
			if (mediaUrl != null)
			{
				target.setImageUrl(mediaUrl + imgUrl);
			}
			else
			{
				target.setImageUrl(imgUrl);
			}
		}
	}

}
