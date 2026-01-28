package com.klarna.payment.services.impl;

import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klarna.data.KlarnaConfigData;
import com.klarna.integration.dto.KlarnaCreateWebhookPayloadDTO;
import com.klarna.integration.dto.KlarnaCreateWebhookRequestDTO;
import com.klarna.integration.dto.KlarnaCreateWebhookResponseDTO;
import com.klarna.integration.dto.KlarnaSigningKeyPayloadDTO;
import com.klarna.integration.dto.KlarnaSigningKeyRequestDTO;
import com.klarna.integration.dto.KlarnaSigningKeyResponseDTO;
import com.klarna.integration.enums.TransactionTypeEnum;
import com.klarna.integration.service.KlarnaIntegrationService;
import com.klarna.integration.util.KlarnaIntegrationUtil;
import com.klarna.model.KlarnaWebhookModel;
import com.klarna.model.KlarnaWebhookNotificationModel;
import com.klarna.payment.daos.KlarnaWebhookDAO;
import com.klarna.payment.data.KlarnaWebhookData;
import com.klarna.payment.data.KlarnaWebhookMetaData;
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
	private KlarnaIntegrationUtil klarnaIntegrationUtil;

	@Resource
	protected ModelService modelService;

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
	public KlarnaCreateWebhookResponseDTO createWebhook(final BaseSiteModel baseSite, final KlarnaConfigData klarnaConfigData)
	{
		final KlarnaCreateWebhookRequestDTO requestDTO = new KlarnaCreateWebhookRequestDTO();
		requestDTO.setConfig(klarnaConfigData);
		requestDTO.setType(TransactionTypeEnum.CREATE_WEBHOOK);
		requestDTO.setMetaData(klarnaServicesUtil.getKlarnaMetaData());
		final KlarnaCreateWebhookPayloadDTO payload = new KlarnaCreateWebhookPayloadDTO();
		payload.setUrl(getWebhookUrl(baseSite));
		payload.setEventTypes(Arrays.asList((Config.getParameter("klarna.webhook.event.types")).split(",")));
		payload.setEventVersion(Config.getParameter("klarna.webhook.event.version"));
		requestDTO.setWebhoookPayload(payload);
		return klarnaIntegrationService.createWebhook(requestDTO);
	}

	@Override
	public KlarnaCreateWebhookResponseDTO deleteWebhook(final KlarnaWebhookModel klarnaWebhookModel,
			final KlarnaConfigData klarnaConfigData)
	{
		final KlarnaCreateWebhookRequestDTO requestDTO = new KlarnaCreateWebhookRequestDTO();
		requestDTO.setConfig(klarnaConfigData);
		requestDTO.setType(TransactionTypeEnum.DELETE_WEBHOOK);
		requestDTO.setMetaData(klarnaServicesUtil.getKlarnaMetaData());
		final KlarnaCreateWebhookPayloadDTO payload = new KlarnaCreateWebhookPayloadDTO();
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

	@Override
	public boolean saveWebhookNotification(final String requestBody)
	{
		try
		{
			final KlarnaWebhookData klarnaWebhookData = klarnaIntegrationUtil.convertResponseStringToDto(requestBody, KlarnaWebhookData.class);
			if (klarnaWebhookData == null || klarnaWebhookData.getMetadata() == null | klarnaWebhookData.getPayload() == null)
			{
				LOG.error("Invalid webhook notification format.");
				return false;
			}
			final KlarnaWebhookMetaData metadata = klarnaWebhookData.getMetadata();
			final KlarnaWebhookNotificationModel klarnaWebhookNotificationModel = modelService
					.create(KlarnaWebhookNotificationModel.class);
			klarnaWebhookNotificationModel.setEventId(metadata.getEventId());
			klarnaWebhookNotificationModel.setEventType(metadata.getEventType());
			klarnaWebhookNotificationModel.setRequestReference(klarnaWebhookData.getPayload().getPaymentRequestId());
			klarnaWebhookNotificationModel
					.setPayload(klarnaIntegrationUtil.convertRequestDtoToString(klarnaWebhookData.getPayload()));
			modelService.save(klarnaWebhookNotificationModel);
			LOG.debug("Saved webhook notification successfully!");
			return true;
		}
		catch (final Exception e)
		{
			LOG.error("Error parsing webhook request body :: ", e);
			return false;
		}


	}
}
