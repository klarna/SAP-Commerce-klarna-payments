package com.klarna.payment.services;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;

import com.klarna.data.KlarnaConfigData;
import com.klarna.integration.dto.KlarnaSigningKeyResponseDTO;
import com.klarna.integration.dto.KlarnaWebhookResponseDTO;
import com.klarna.model.KlarnaWebhookModel;


public interface KlarnaWebhookService
{
	KlarnaWebhookModel getWebhookForBaseSite(final BaseSiteModel baseSite);

	KlarnaWebhookResponseDTO createWebhook(final BaseSiteModel baseSite, final KlarnaConfigData klarnaConfigData);

	KlarnaWebhookResponseDTO deleteWebhook(final KlarnaWebhookModel klarnaWebhookModel, final KlarnaConfigData klarnaConfigData);

	String getWebhookUrl(final BaseSiteModel baseSite);

	KlarnaSigningKeyResponseDTO createSigningKey(final KlarnaConfigData klarnaConfigData);

	KlarnaSigningKeyResponseDTO deleteSigningKey(final KlarnaWebhookModel klarnaWebhookModel,
			final KlarnaConfigData klarnaConfigData);

}