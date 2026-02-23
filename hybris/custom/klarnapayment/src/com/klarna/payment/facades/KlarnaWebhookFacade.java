package com.klarna.payment.facades;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

import com.klarna.data.KlarnaConfigData;
import com.klarna.model.KlarnaWebhookModel;
import com.klarna.payment.data.KlarnaWebhookData;


public interface KlarnaWebhookFacade
{
	boolean createWebhook(final BaseSiteModel baseSite);

	boolean deleteWebhook(final BaseSiteModel baseSite);

	boolean createSigningKey(final KlarnaWebhookModel klarnaWebhookModel, final KlarnaConfigData klarnaConfig);

	boolean deleteSigningKey(final KlarnaWebhookModel klarnaWebhookModel, final KlarnaConfigData klarnaConfig);

	boolean validateWebhookRequest(final byte[] requestBody, final String signature);

	boolean processWebhookNotification(final byte[] requestBody);

	KlarnaWebhookData getSavedWebhookData(final String paymentRequestId);

}