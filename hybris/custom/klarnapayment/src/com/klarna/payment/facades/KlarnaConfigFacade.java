package com.klarna.payment.facades;

import de.hybris.platform.store.BaseStoreModel;

import com.klarna.data.KlarnaConfigData;
import com.klarna.model.KlarnaConfigModel;


public interface KlarnaConfigFacade
{
	/**
	 * Gets the KlarnaConfig for current base store.
	 *
	 * @return KlarnaConfigData
	 */
	KlarnaConfigData getKlarnaConfig();

	KlarnaConfigModel getKlarnaConfigForStore(BaseStoreModel store);

	boolean isNorthAmerianKlarnaPayment();

	//	String getPaymentOption();
	//
	//	String getLogo();
	//
	//	String getDisplayName();

	boolean isNLKlarnaPayment();
}
