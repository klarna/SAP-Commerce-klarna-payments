package com.klarna.payment.facades;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

import com.klarna.data.KlarnaConfigData;
import com.klarna.model.KlarnaWebhookModel;


public interface KlarnaWebhookFacade
{
	boolean createWebhook(final BaseSiteModel baseSite);

	boolean deleteWebhook(final BaseSiteModel baseSite);

	boolean createSigningKey(final KlarnaWebhookModel klarnaWebhookModel, final KlarnaConfigData klarnaConfig);

	boolean deleteSigningKey(final KlarnaWebhookModel klarnaWebhookModel, final KlarnaConfigData klarnaConfig);

	boolean processWebhookNotification(final String requestBody, final String signature);

}