package com.klarna.payment.facades.impl;

import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.model.ModelService;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klarna.data.KlarnaConfigData;
import com.klarna.integration.dto.KlarnaSigningKeyResponseDTO;
import com.klarna.integration.dto.KlarnaWebhookResponseDTO;
import com.klarna.model.KlarnaWebhookModel;
import com.klarna.payment.facades.KlarnaConfigFacade;
import com.klarna.payment.facades.KlarnaWebhookFacade;
import com.klarna.payment.services.KlarnaWebhookService;


public class DefaultKlarnaWebhookFacade implements KlarnaWebhookFacade
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultKlarnaWebhookFacade.class);

	@Resource
	private KlarnaWebhookService klarnaWebhookService;

	@Resource
	private KlarnaConfigFacade klarnaConfigFacade;

	@Resource
	private ModelService modelService;


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
		final KlarnaWebhookResponseDTO responseDTO = klarnaWebhookService.createWebhook(baseSite, klarnaConfig);
		if (responseDTO.getWebhoookPayload() != null && StringUtils.isNotEmpty(responseDTO.getWebhoookPayload().getWebhookId()))
		{
			klarnaWebhookModel.setWebhookId(responseDTO.getWebhoookPayload().getWebhookId());
			klarnaWebhookModel.setStatus(responseDTO.getWebhoookPayload().getStatus());
			modelService.save(klarnaWebhookModel);
			modelService.refresh(klarnaWebhookModel);
			LOG.debug("Created Webhook with ID: {}", responseDTO.getWebhoookPayload().getWebhookId());
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
			LOG.debug("Webhook doesn't exist for the Base Site {} ", baseSite.getUid());
			return true;
		}
		if (StringUtils.isNotEmpty(klarnaWebhookModel.getWebhookId()))
		{
			final KlarnaWebhookResponseDTO responseDTO = klarnaWebhookService.deleteWebhook(klarnaWebhookModel, klarnaConfig);
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
			modelService.save(klarnaWebhookModel);
			modelService.refresh(klarnaWebhookModel);
			LOG.debug("Created Signing Key with ID: {}", responseDTO.getSigningKeyPayload().getSigningKeyId());
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


}
