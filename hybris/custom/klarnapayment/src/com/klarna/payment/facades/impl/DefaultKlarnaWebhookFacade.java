package com.klarna.payment.facades.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.site.BaseSiteService;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.klarna.data.KlarnaConfigData;
import com.klarna.integration.dto.KlarnaCreateWebhookResponseDTO;
import com.klarna.integration.dto.KlarnaSigningKeyResponseDTO;
import com.klarna.model.KlarnaWebhookModel;
import com.klarna.payment.data.KlarnaWebhookData;
import com.klarna.payment.facades.KlarnaConfigFacade;
import com.klarna.payment.facades.KlarnaWebhookFacade;
import com.klarna.payment.services.KlarnaWebhookService;
import com.klarna.payment.util.KlarnaValidationUtil;
import com.klarna.payment.util.LogHelper;


public class DefaultKlarnaWebhookFacade implements KlarnaWebhookFacade
{

	private static final Logger LOG = Logger.getLogger(DefaultKlarnaWebhookFacade.class);

	@Resource
	private KlarnaWebhookService klarnaWebhookService;

	@Resource
	private KlarnaConfigFacade klarnaConfigFacade;

	@Resource
	private ModelService modelService;

	@Resource
	private BaseSiteService baseSiteService;

	@Resource
	private KlarnaValidationUtil klarnaValidationUtil;


	@Override
	public boolean createWebhook(final BaseSiteModel baseSite)
	{
		final KlarnaConfigData klarnaConfig = klarnaConfigFacade.getKlarnaConfigForBaseSite(baseSite);
		KlarnaWebhookModel klarnaWebhookModel = klarnaWebhookService.getWebhookForBaseSite(baseSite);
		if (klarnaWebhookModel == null)
		{
			klarnaWebhookModel = modelService.create(KlarnaWebhookModel.class);
		}
		if (StringUtils.isEmpty(klarnaWebhookModel.getSigningKey()) || StringUtils.isEmpty(klarnaWebhookModel.getSigningKeyId()))
		{
			final boolean signingKeyCreated = createSigningKey(klarnaWebhookModel, klarnaConfig);
			if (!signingKeyCreated)
			{
				return false;
			}
		}
		final KlarnaCreateWebhookResponseDTO responseDTO = klarnaWebhookService.createWebhook(baseSite, klarnaConfig,
				klarnaWebhookModel.getSigningKeyId());
		if (responseDTO.getWebhoookPayload() != null && StringUtils.isNotEmpty(responseDTO.getWebhoookPayload().getWebhookId()))
		{
			klarnaWebhookModel.setWebhookId(responseDTO.getWebhoookPayload().getWebhookId());
			klarnaWebhookModel.setStatus(responseDTO.getWebhoookPayload().getStatus());
			klarnaWebhookModel.setBaseSite(baseSite);
			modelService.save(klarnaWebhookModel);
			modelService.refresh(klarnaWebhookModel);
			LogHelper.debugLog(LOG, "Created Webhook with ID: " + responseDTO.getWebhoookPayload().getWebhookId());
			return true;
		}
		else
		{
			LOG.error("Webhook creation failed!");
			if (responseDTO.getError() != null)
			{
				LOG.error("Error Code: " + responseDTO.getError().getErrorCode() + " Error Message: "
						+ responseDTO.getError().getErrorMessage());
			}
		}
		return false;
	}

	@Override
	public boolean deleteWebhook(final BaseSiteModel baseSite)
	{
		final KlarnaConfigData klarnaConfig = klarnaConfigFacade.getKlarnaConfigForBaseSite(baseSite);
		final KlarnaWebhookModel klarnaWebhookModel = klarnaWebhookService.getWebhookForBaseSite(baseSite);
		if (klarnaWebhookModel == null)
		{
			LogHelper.debugLog(LOG, "Webhook doesn't exist for the Base Site : " + baseSite.getUid());
			return true;
		}
		if (StringUtils.isNotEmpty(klarnaWebhookModel.getWebhookId()))
		{
			final KlarnaCreateWebhookResponseDTO responseDTO = klarnaWebhookService.deleteWebhook(klarnaWebhookModel, klarnaConfig);
			if (responseDTO.getError() != null)
			{
				LOG.error("Webhook deletion failed! Error Code: " + responseDTO.getError().getErrorCode() + " Error Message: "
						+ responseDTO.getError().getErrorMessage());
			}
			return false;
		}
		if (StringUtils.isNotEmpty(klarnaWebhookModel.getSigningKeyId()))
		{
			deleteSigningKey(klarnaWebhookModel, klarnaConfig);
		}
		return true;
	}

	@Override
	public boolean createSigningKey(final KlarnaWebhookModel klarnaWebhookModel, final KlarnaConfigData klarnaConfig)
	{
		final KlarnaSigningKeyResponseDTO responseDTO = klarnaWebhookService.createSigningKey(klarnaConfig);
		if (responseDTO.getSigningKeyPayload() != null
				&& StringUtils.isNotEmpty(responseDTO.getSigningKeyPayload().getSigningKeyId()))
		{
			klarnaWebhookModel.setSigningKey(responseDTO.getSigningKeyPayload().getSigningKey());
			klarnaWebhookModel.setSigningKeyId(responseDTO.getSigningKeyPayload().getSigningKeyId());
			//modelService.save(klarnaWebhookModel);
			//modelService.refresh(klarnaWebhookModel);
			LogHelper.debugLog(LOG, "Created Signing Key with ID: " + responseDTO.getSigningKeyPayload().getSigningKeyId());
			return true;
		}
		else
		{
			LOG.error("Signing key creation failed!");
			if (responseDTO.getError() != null)
			{
				LOG.error("Error Code: " + responseDTO.getError().getErrorCode() + " Error Message: "
						+ responseDTO.getError().getErrorMessage());
			}
		}
		return false;
	}

	@Override
	public boolean deleteSigningKey(final KlarnaWebhookModel klarnaWebhookModel, final KlarnaConfigData klarnaConfig)
	{
		final KlarnaSigningKeyResponseDTO responseDTO = klarnaWebhookService.deleteSigningKey(klarnaWebhookModel, klarnaConfig);
		if (responseDTO.getError() != null)
		{
			LOG.error("Signing Key deletion failed! Error Code: " + responseDTO.getError().getErrorCode() + " Error Message: "
					+ responseDTO.getError().getErrorMessage());
			return false;
		}
		return true;
	}

	@Override
	public boolean validateWebhookRequest(final byte[] requestBody, final String signature)
	{
		if (StringUtils.isEmpty(signature))
		{
			LOG.error("Webhook cannot be processed. Signature is not available.");
			return false;
		}
		final boolean isValidSignature = klarnaValidationUtil.validateSignature(requestBody, signature, getSavedSigningKey());
		if (isValidSignature)
		{
			LogHelper.debugLog(LOG, "Signature validation success!");
		}
		else
		{
			LOG.error("Webhook processing failed. Invalid signature!");
		}
		return isValidSignature;
	}

	@Override
	public boolean processWebhookNotification(final byte[] requestBody)
	{
		final KlarnaWebhookData webhookData = klarnaWebhookService.getParsedWebhookRequest(requestBody);
		if (webhookData == null || webhookData.getPayload() == null
				|| StringUtils.isEmpty(webhookData.getPayload().getPaymentRequestId()))
		{
			LOG.error("Invalid webhook request.");
			return false;
		}
		LogHelper.debugLog(LOG, "Webhook request is valid.");
		return klarnaWebhookService.saveWebhookNotification(webhookData);
	}

	protected String getSavedSigningKey()
	{
		final BaseSiteModel currentSite = baseSiteService.getCurrentBaseSite();
		final KlarnaWebhookModel klarnaWebhookModel = klarnaWebhookService.getWebhookForBaseSite(currentSite);
		if (klarnaWebhookModel != null)
		{
			return klarnaWebhookModel.getSigningKey();
		}
		return null;
	}

}
