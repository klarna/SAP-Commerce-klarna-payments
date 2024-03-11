/**
 *
 */
package com.klarna.payment.services;

/**
 * @author rajani.narayanan
 *
 */
public interface KPCurrencyConversionService
{
	public Double convertToPurchaseCurrencyPrice(final Double value);
}
