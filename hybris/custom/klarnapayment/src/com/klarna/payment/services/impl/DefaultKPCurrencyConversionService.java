/**
 *
 */
package com.klarna.payment.services.impl;

import de.hybris.platform.servicelayer.i18n.CommonI18NService;

import com.klarna.payment.facades.KlarnaConfigFacade;
import com.klarna.payment.services.KPCurrencyConversionService;


public class DefaultKPCurrencyConversionService implements KPCurrencyConversionService
{
	private CommonI18NService commonI18NService;
	private KlarnaConfigFacade klarnaConfigFacade;

	/**
	 * @param klarnaConfigFacade
	 *           the klarnaConfigFacade to set
	 */
	public void setKlarnaConfigFacade(final KlarnaConfigFacade klarnaConfigFacade)
	{
		this.klarnaConfigFacade = klarnaConfigFacade;
	}

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


	@Override
	public Double convertToPurchaseCurrencyPrice(final Double value)
	{

		final Double currentCurrConversion = commonI18NService.getCurrentCurrency().getConversion();
		//final KlarnaConfigData klarnaConfig = klarnaConfigFacade.getKlarnaConfig();
		//final Double purchCurrConversion = commonI18NService.getCurrency(klarnaConfig.getPurchaseCurrency()).getConversion();
		final Double purchCurrConversion = commonI18NService.getCurrentCurrency().getConversion();
		if (currentCurrConversion == purchCurrConversion)
		{
			return value;
		}

		return Double.valueOf(commonI18NService.convertAndRoundCurrency(currentCurrConversion.doubleValue(),
				purchCurrConversion.doubleValue(), 4, value.doubleValue()));

	}
}
