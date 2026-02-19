package com.klarna.payment.services;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

import com.klarna.data.KlarnaConfigData;
import com.klarna.integration.dto.KlarnaCreateWebhookResponseDTO;
import com.klarna.integration.dto.KlarnaSigningKeyResponseDTO;
import com.klarna.model.KlarnaWebhookModel;
import com.klarna.model.KlarnaWebhookNotificationModel;
import com.klarna.payment.data.KlarnaWebhookData;


public interface KlarnaWebhookService
{
	KlarnaWebhookModel getWebhookForBaseSite(final BaseSiteModel baseSite);

	KlarnaCreateWebhookResponseDTO createWebhook(final BaseSiteModel baseSite, final KlarnaConfigData klarnaConfigData,
			final String signingKeyId);

	KlarnaCreateWebhookResponseDTO deleteWebhook(final KlarnaWebhookModel klarnaWebhookModel,
			final KlarnaConfigData klarnaConfigData);

	String getWebhookUrl(final BaseSiteModel baseSite);

	KlarnaSigningKeyResponseDTO createSigningKey(final KlarnaConfigData klarnaConfigData);

	KlarnaSigningKeyResponseDTO deleteSigningKey(final KlarnaWebhookModel klarnaWebhookModel,
			final KlarnaConfigData klarnaConfigData);

	KlarnaWebhookData getParsedWebhookRequest(final byte[] requestBody);

	boolean saveWebhookNotification(final KlarnaWebhookData webhookData);

	KlarnaWebhookNotificationModel getWebhookNotificationById(final String id);

	KlarnaWebhookNotificationModel getSavedWebhookNotification(final String paymentRequestId);

}