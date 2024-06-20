package com.klarna.payment.facades;

import de.hybris.platform.store.BaseStoreModel;

import com.klarna.data.KlarnaConfigData;
import com.klarna.model.KlarnaConfigModel;
import com.klarna.model.KlarnaCredentialModel;


public interface KPConfigFacade
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

	public KlarnaCredentialModel getKlarnaCredentialForSite(final KlarnaConfigModel klarnaConfig);

	public String getConfigurationString(final String key);
}
