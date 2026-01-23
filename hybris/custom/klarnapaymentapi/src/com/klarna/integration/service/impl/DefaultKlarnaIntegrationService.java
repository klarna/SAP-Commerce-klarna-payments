/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.klarna.integration.service.impl;

import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestTemplate;

import com.klarna.integration.dto.KlarnaAPIRequestDTO;
import com.klarna.integration.dto.KlarnaAPIResponseDTO;
import com.klarna.integration.dto.KlarnaRequestDTO;
import com.klarna.integration.dto.KlarnaSigningKeyPayloadDTO;
import com.klarna.integration.dto.KlarnaSigningKeyRequestDTO;
import com.klarna.integration.dto.KlarnaSigningKeyResponseDTO;
import com.klarna.integration.dto.KlarnaWebhookPayloadDTO;
import com.klarna.integration.dto.KlarnaWebhookRequestDTO;
import com.klarna.integration.dto.KlarnaWebhookResponseDTO;
import com.klarna.integration.service.KlarnaIntegrationService;
import com.klarna.integration.service.KlarnaRestClientService;
import com.klarna.integration.util.KlarnaIntegrationUtil;


/**
 *
 */
public class DefaultKlarnaIntegrationService implements KlarnaIntegrationService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultKlarnaIntegrationService.class);

	protected RestTemplate restTemplate;

	@Resource(name = "klarnaRestClientService")
	protected KlarnaRestClientService klarnaRestClientService;

	@Resource(name = "klarnaIntegrationUtil")
	protected KlarnaIntegrationUtil klarnaIntegrationUtil;

	@Resource(name = "klarnaAPIRequestConverter")
	protected Converter<KlarnaRequestDTO, KlarnaAPIRequestDTO> klarnaAPIRequestConverter;

	@Override
	public KlarnaSigningKeyResponseDTO createSigningKey(final KlarnaSigningKeyRequestDTO requestDTO)
	{
		final KlarnaAPIRequestDTO klarnaAPIRequestDTO = klarnaAPIRequestConverter.convert(requestDTO);
		final KlarnaAPIResponseDTO klarnaAPIResponseDTO = klarnaRestClientService.callRestApi(klarnaAPIRequestDTO);
		final KlarnaSigningKeyResponseDTO responseDTO = new KlarnaSigningKeyResponseDTO();
		if (klarnaAPIResponseDTO.getPayload() != null)
		{
			responseDTO.setSigningKeyPayload(klarnaIntegrationUtil.convertResponseStringToDto(klarnaAPIResponseDTO.getPayload(),
					KlarnaSigningKeyPayloadDTO.class));
		}
		else
		{
			responseDTO.setError(klarnaAPIResponseDTO.getError());
		}
		return responseDTO;
	}

	@Override
	public KlarnaSigningKeyResponseDTO deleteSigningKey(final KlarnaSigningKeyRequestDTO requestDTO)
	{
		final KlarnaAPIRequestDTO klarnaAPIRequestDTO = klarnaAPIRequestConverter.convert(requestDTO);
		final Map<String, String> pathParams = new HashMap<>();
		pathParams.put("signing_key_id", requestDTO.getSigningKeyPayload().getSigningKeyId());
		final KlarnaAPIResponseDTO klarnaAPIResponseDTO = klarnaRestClientService.callRestApi(klarnaAPIRequestDTO);
		final KlarnaSigningKeyResponseDTO responseDTO = new KlarnaSigningKeyResponseDTO();
		responseDTO.setError(klarnaAPIResponseDTO.getError());
		return responseDTO;
	}

	@Override
	public KlarnaWebhookResponseDTO createWebhook(final KlarnaWebhookRequestDTO requestDTO)
	{
		final KlarnaAPIRequestDTO klarnaAPIRequestDTO = klarnaAPIRequestConverter.convert(requestDTO);
		klarnaAPIRequestDTO.setPayload(klarnaIntegrationUtil.convertRequestDtoToString(requestDTO.getWebhoookPayload()));
		final KlarnaAPIResponseDTO klarnaAPIResponseDTO = klarnaRestClientService.callRestApi(klarnaAPIRequestDTO);
		final KlarnaWebhookResponseDTO responseDTO = new KlarnaWebhookResponseDTO();
		if (klarnaAPIResponseDTO.getPayload() != null)
		{
			responseDTO.setWebhoookPayload(klarnaIntegrationUtil.convertResponseStringToDto(klarnaAPIResponseDTO.getPayload(),
					KlarnaWebhookPayloadDTO.class));
		}
		else
		{
			responseDTO.setError(klarnaAPIResponseDTO.getError());
		}
		return responseDTO;
	}

	@Override
	public KlarnaWebhookResponseDTO updateWebhook(final KlarnaWebhookRequestDTO requestDTO)
	{
		final KlarnaAPIRequestDTO klarnaAPIRequestDTO = klarnaAPIRequestConverter.convert(requestDTO);
		klarnaAPIRequestDTO.setPayload(klarnaIntegrationUtil.convertRequestDtoToString(requestDTO.getWebhoookPayload()));
		final Map<String, String> pathParams = new HashMap<>();
		pathParams.put("webhook_id", requestDTO.getWebhoookPayload().getWebhookId());
		final KlarnaAPIResponseDTO klarnaAPIResponseDTO = klarnaRestClientService.callRestApi(klarnaAPIRequestDTO);
		final KlarnaWebhookResponseDTO responseDTO = new KlarnaWebhookResponseDTO();
		if (klarnaAPIResponseDTO.getPayload() != null)
		{
			responseDTO.setWebhoookPayload(klarnaIntegrationUtil.convertResponseStringToDto(klarnaAPIResponseDTO.getPayload(),
					KlarnaWebhookPayloadDTO.class));
		}
		else
		{
			responseDTO.setError(klarnaAPIResponseDTO.getError());
		}
		return responseDTO;
	}

	@Override
	public KlarnaWebhookResponseDTO deleteWebhook(final KlarnaWebhookRequestDTO requestDTO)
	{
		final KlarnaAPIRequestDTO klarnaAPIRequestDTO = klarnaAPIRequestConverter.convert(requestDTO);
		final Map<String, String> pathParams = new HashMap<>();
		pathParams.put("webhook_id", requestDTO.getWebhoookPayload().getWebhookId());
		final KlarnaAPIResponseDTO klarnaAPIResponseDTO = klarnaRestClientService.callRestApi(klarnaAPIRequestDTO);
		final KlarnaWebhookResponseDTO responseDTO = new KlarnaWebhookResponseDTO();
		responseDTO.setError(klarnaAPIResponseDTO.getError());
		return responseDTO;
	}
}
