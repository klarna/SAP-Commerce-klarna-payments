package com.klarna.payment.util;

import de.hybris.platform.commercefacades.product.data.ImageData;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.product.ProductModel;
import de.hybris.platform.util.Config;
import de.hybris.platform.util.TaxValue;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klarna.integration.dto.KlarnaClientSystemDTO;
import com.klarna.integration.dto.KlarnaIntegrationMetaDataDTO;
import com.klarna.payment.facades.KlarnaConfigFacade;

public final class KlarnaServicesUtil
{

	protected static final Logger LOG = Logger.getLogger(KlarnaServicesUtil.class);

	@Resource(name = "klarnaConfigFacade")
	private KlarnaConfigFacade klarnaConfigFacade;

	@Resource(name = "productPrimaryImagePopulator")
	private Populator<ProductModel, ProductData> productPrimaryImagePopulator;

	@Resource(name = "klarnaObjectMapper")
	protected ObjectMapper objectMapper;

	public KlarnaIntegrationMetaDataDTO getKlarnaMetaData()
	{
		final KlarnaIntegrationMetaDataDTO metaData = new KlarnaIntegrationMetaDataDTO();

		final KlarnaClientSystemDTO integrator = new KlarnaClientSystemDTO();
		integrator.setName(Config.getParameter("klarna.integrator.name"));
		integrator.setModuleName(Config.getParameter("klarna.integrator.module.name"));
		integrator.setModuleVersion(Config.getParameter("klarna.integrator.module.version"));
		metaData.setIntegrator(integrator);

		final KlarnaClientSystemDTO originator = new KlarnaClientSystemDTO();
		originator.setName(Config.getParameter("klarna.originator.name"));
		originator.setModuleName(Config.getParameter("klarna.originator.module.name"));
		originator.setModuleVersion(Config.getParameter("klarna.originator.module.version"));
		metaData.setOriginators(Arrays.asList(originator));

		return metaData;
	}

	public String getProductImageURL(final ProductModel product)
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

	public Long calculateTotalAmount(final AbstractOrderModel source)
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

	public Long getOrderEntryTaxAmount(final AbstractOrderEntryModel entry, final String currencyCode)
	{
		final TaxValue tax = getTaxValue(entry.getTaxValues(), currencyCode);
		final Double appliedValue = Double.valueOf(tax.getAppliedValue());
		return KlarnaConversionUtils.getKlarnaLongValue(appliedValue);
	}

	public TaxValue getTaxValue(final Collection<TaxValue> taxes, final String currencyCode)
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

	public String convertRequestDtoToString(final Object requestDto)
	{
		String requestString = null;
		try
		{
			requestString = objectMapper.writeValueAsString(requestDto);
		}
		catch (final JsonProcessingException e)
		{
			LOG.error("JSON processing exception", e);
		}
		return requestString;
	}

}
