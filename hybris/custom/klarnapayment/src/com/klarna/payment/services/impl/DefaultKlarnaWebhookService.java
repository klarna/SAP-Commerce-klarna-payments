package com.klarna.payment.services.impl;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klarna.data.KlarnaConfigData;
import com.klarna.integration.dto.KlarnaSigningKeyPayloadDTO;
import com.klarna.integration.dto.KlarnaSigningKeyRequestDTO;
import com.klarna.integration.dto.KlarnaSigningKeyResponseDTO;
import com.klarna.integration.dto.KlarnaWebhookPayloadDTO;
import com.klarna.integration.dto.KlarnaWebhookRequestDTO;
import com.klarna.integration.dto.KlarnaWebhookResponseDTO;
import com.klarna.integration.enums.TransactionTypeEnum;
import com.klarna.integration.service.KlarnaIntegrationService;
import com.klarna.model.KlarnaWebhookModel;
import com.klarna.payment.daos.KlarnaWebhookDAO;
import com.klarna.payment.services.KlarnaWebhookService;
import com.klarna.payment.util.KlarnaServicesUtil;


public class DefaultKlarnaWebhookService implements KlarnaWebhookService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultKlarnaWebhookService.class);

	@Resource
	private KlarnaWebhookDAO klarnaWebhookDAO;

	@Resource
	private KlarnaIntegrationService klarnaIntegrationService;

	@Resource
	private KlarnaServicesUtil klarnaServicesUtil;

	@Resource
	protected SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	@Override
	public KlarnaWebhookModel getWebhookForBaseSite(final BaseSiteModel baseSite)
	{
		final List<KlarnaWebhookModel> webhooks = klarnaWebhookDAO.findWebhookByBaseSite(baseSite);
		if (CollectionUtils.isNotEmpty(webhooks))
		{
			return webhooks.get(0);
		}
		return null;
	}

	@Override
	public KlarnaWebhookResponseDTO createWebhook(final BaseSiteModel baseSite, final KlarnaConfigData klarnaConfigData)
	{
		final KlarnaWebhookRequestDTO requestDTO = new KlarnaWebhookRequestDTO();
		requestDTO.setConfig(klarnaConfigData);
		requestDTO.setType(TransactionTypeEnum.CREATE_WEBHOOK);
		requestDTO.setMetaData(klarnaServicesUtil.getKlarnaMetaData());
		final KlarnaWebhookPayloadDTO payload = new KlarnaWebhookPayloadDTO();
		payload.setUrl(getWebhookUrl(baseSite));
		payload.setEventTypes(Arrays.asList((Config.getParameter("klarna.webhook.event.types")).split(",")));
		payload.setEventVersion(Config.getParameter("klarna.webhook.event.version"));
		requestDTO.setWebhoookPayload(payload);
		return klarnaIntegrationService.createWebhook(requestDTO);
	}

	@Override
	public KlarnaWebhookResponseDTO deleteWebhook(final KlarnaWebhookModel klarnaWebhookModel,
			final KlarnaConfigData klarnaConfigData)
	{
		final KlarnaWebhookRequestDTO requestDTO = new KlarnaWebhookRequestDTO();
		requestDTO.setConfig(klarnaConfigData);
		requestDTO.setType(TransactionTypeEnum.DELETE_WEBHOOK);
		requestDTO.setMetaData(klarnaServicesUtil.getKlarnaMetaData());
		final KlarnaWebhookPayloadDTO payload = new KlarnaWebhookPayloadDTO();
		payload.setWebhookId(klarnaWebhookModel.getWebhookId());
		requestDTO.setWebhoookPayload(payload);
		return klarnaIntegrationService.deleteWebhook(requestDTO);
	}

	@Override
	public KlarnaSigningKeyResponseDTO createSigningKey(final KlarnaConfigData klarnaConfigData)
	{
		final KlarnaSigningKeyRequestDTO requestDTO = new KlarnaSigningKeyRequestDTO();
		requestDTO.setConfig(klarnaConfigData);
		requestDTO.setType(TransactionTypeEnum.CREATE_SIGNING_KEY);
		requestDTO.setMetaData(klarnaServicesUtil.getKlarnaMetaData());
		return klarnaIntegrationService.createSigningKey(requestDTO);
	}

	@Override
	public KlarnaSigningKeyResponseDTO deleteSigningKey(final KlarnaWebhookModel klarnaWebhookModel,
			final KlarnaConfigData klarnaConfigData)
	{
		final KlarnaSigningKeyRequestDTO requestDTO = new KlarnaSigningKeyRequestDTO();
		requestDTO.setConfig(klarnaConfigData);
		requestDTO.setType(TransactionTypeEnum.DELETE_SIGNING_KEY);
		requestDTO.setMetaData(klarnaServicesUtil.getKlarnaMetaData());
		final KlarnaSigningKeyPayloadDTO payload = new KlarnaSigningKeyPayloadDTO();
		payload.setSigningKeyId(klarnaWebhookModel.getSigningKeyId());
		requestDTO.setSigningKeyPayload(payload);
		return klarnaIntegrationService.deleteSigningKey(requestDTO);
	}

	@Override
	public String getWebhookUrl(final BaseSiteModel baseSite)
	{
		final String relativeUrl = Config.getParameter("klarna.webhook.url");
		return siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, true, relativeUrl);

	}
}
