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
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.klarna.api.payments.model.PaymentsCreateOrderRequest;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.payment.facades.KPConfigFacade;



/**
 *
 */
public class KlarnaPaymentOrderPopulator implements Populator<PaymentsSession, PaymentsCreateOrderRequest>
{

	KPConfigFacade kpConfigFacade;

	/**
	 * @param kpConfigFacade
	 *           the kpConfigFacade to set
	 */
	public void setKpConfigFacade(final KPConfigFacade kpConfigFacade)
	{
		this.kpConfigFacade = kpConfigFacade;
	}

	@Override
	public void populate(final PaymentsSession source, final PaymentsCreateOrderRequest target) throws ConversionException
	{
		//final KlarnaConfigData klarnaConfig = kpConfigFacade.getKlarnaConfig();

		target.setBillingAddress(source.getBillingAddress());
		target.setShippingAddress(source.getShippingAddress());
		target.setPurchaseCountry(source.getPurchaseCountry());
		target.setPurchaseCurrency(source.getPurchaseCurrency());
		target.setLocale(source.getLocale());
		target.setOrderAmount(source.getOrderAmount());
		target.setOrderTaxAmount(source.getOrderTaxAmount());
		target.setOrderLines(source.getOrderLines());
		target.setMerchantUrls(source.getMerchantUrls());
		target.setMerchantReference1(source.getMerchantReference1());
		if (null != source.getMerchantReference2())
		{
			target.setMerchantReference2(source.getMerchantReference2());
		}
		target.setCustomer(source.getCustomer());
		target.setOptions(source.getOptions());
		target.setAttachment(source.getAttachment());
		//target.setAutoCapture(klarnaConfig.getAutoCapture());

	}

}
