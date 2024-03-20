package com.klarna.payment.facades;

import de.hybris.platform.store.BaseStoreModel;

import com.klarna.payment.data.KlarnaConfigData;
import com.klarna.payment.model.KlarnaPayConfigModel;


public interface KPConfigFacade
{
	/**
	 * Gets the KlarnaConfig for current base store.
	 *
	 * @return KlarnaConfigData
	 */
	KlarnaConfigData getKlarnaConfig();

	KlarnaPayConfigModel getKlarnaConfigForStore(BaseStoreModel store);

	boolean isNorthAmerianKlarnaPayment();

	String getPaymentOption();

	String getLogo();

	String getDisplayName();

	boolean isNLKlarnaPayment();
}
