/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2020 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.klarna.payment.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import java.util.List;

import com.klarna.api.order_management.model.OrderManagementCaptureObject;
import com.klarna.api.order_management.model.OrderManagementShippingInfo;
import com.klarna.payment.enums.KPEndpointType;
import com.klarna.payment.model.KlarnaPayConfigModel;
import com.klarna.payment.util.KlarnaConversionUtils;


/**
 *
 */
public class KlarnaOrderCapturePopulator implements Populator<AbstractOrderModel, OrderManagementCaptureObject>
{
	Converter<AbstractOrderModel, List<OrderManagementShippingInfo>> klarnaCaptureShippingInfoConverter;
	CommonI18NService commonI18NService;

	/**
	 * @return the commonI18NService
	 */
	public CommonI18NService getCommonI18NService()
	{
		return commonI18NService;
	}



	/**
	 * @param commonI18NService
	 *           the commonI18NService to set
	 */
	public void setCommonI18NService(final CommonI18NService commonI18NService)
	{
		this.commonI18NService = commonI18NService;
	}



	/**
	 * @return the klarnaCaptureShippingInfoConverter
	 */
	public Converter<AbstractOrderModel, List<OrderManagementShippingInfo>> getKlarnaCaptureShippingInfoConverter()
	{
		return klarnaCaptureShippingInfoConverter;
	}



	/**
	 * @param klarnaCaptureShippingInfoConverter
	 *           the klarnaCaptureShippingInfoConverter to set
	 */
	public void setKlarnaCaptureShippingInfoConverter(
			final Converter<AbstractOrderModel, List<OrderManagementShippingInfo>> klarnaCaptureShippingInfoConverter)
	{
		this.klarnaCaptureShippingInfoConverter = klarnaCaptureShippingInfoConverter;
	}



	@Override
	public void populate(final AbstractOrderModel source, final OrderManagementCaptureObject target) throws ConversionException
	{
		double grandTotalPrice;
		final KlarnaPayConfigModel config = source.getStore().getKlarnaPayConfig();
		if (config.getEndpointType().equals(KPEndpointType.NORTH_AMERICA))
		{
			grandTotalPrice = convertToPurchaseCurrencyPrice(source.getCurrency().getIsocode(), config, source.getTotalPrice())
					.doubleValue()
					+ convertToPurchaseCurrencyPrice(source.getCurrency().getIsocode(), config, source.getTotalTax()).doubleValue();

		}
		else
		{
			grandTotalPrice = convertToPurchaseCurrencyPrice(source.getCurrency().getIsocode(), config, source.getTotalPrice())
					.doubleValue();


		}



		final long totalPrice = new Double(KlarnaConversionUtils.getKlarnaIntValue(grandTotalPrice)).longValue();
		target.setCapturedAmount(totalPrice);
		target.setShippingInfo(getKlarnaCaptureShippingInfoConverter().convert(source));

	}

	public Double convertToPurchaseCurrencyPrice(final String currency, final KlarnaPayConfigModel config, final Double value)
	{

		final Double currentCurrConversion = commonI18NService.getCurrency(currency).getConversion();

		final Double purchCurrConversion = commonI18NService.getCurrency(config.getPurchaseCurrency().getIsocode()).getConversion();
		if (currentCurrConversion == purchCurrConversion)
		{
			return value;
		}

		return Double.valueOf(commonI18NService.convertAndRoundCurrency(currentCurrConversion.doubleValue(),
				purchCurrConversion.doubleValue(), 4, value.doubleValue()));

	}


}
