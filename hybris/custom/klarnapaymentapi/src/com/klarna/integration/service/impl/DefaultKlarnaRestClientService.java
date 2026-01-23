/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.klarna.integration.service.impl;

import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.klarna.integration.dto.KlarnaAPIRequestDTO;
import com.klarna.integration.dto.KlarnaAPIResponseDTO;
import com.klarna.integration.dto.KlarnaErrorDTO;
import com.klarna.integration.service.KlarnaRestClientService;
import com.klarna.integration.util.KlarnaLoggerUtil;


/**
 *
 */
public class DefaultKlarnaRestClientService implements KlarnaRestClientService
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultKlarnaRestClientService.class);

	protected RestTemplate restTemplate;

	@Resource(name = "klarnaLoggerUtil")
	protected KlarnaLoggerUtil klarnaLoggerUtil;

	@Override
	public KlarnaAPIResponseDTO callRestApi(final KlarnaAPIRequestDTO requestDTO)
	{
		if(LOG.isDebugEnabled()) {
			LOG.debug("Calling Klarna API...");
		}
		klarnaLoggerUtil.logRestRequest(requestDTO);

		final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(requestDTO.getUrl());
		if (MapUtils.isNotEmpty(requestDTO.getQueryParams()))
		{
			requestDTO.getQueryParams().forEach(uriBuilder::queryParam);
		}
		final URI uri = uriBuilder.buildAndExpand(requestDTO.getPathParams() != null ? requestDTO.getPathParams() : Map.of()).toUri();
		final HttpHeaders httpHeaders = getHttpHeaders(requestDTO.getHeaders());

		final KlarnaAPIResponseDTO responseDTO = new KlarnaAPIResponseDTO();
		try
		{
			final ResponseEntity<String> responseEntity = restTemplate.exchange(uri, requestDTO.getMethod(),
					new HttpEntity<>(requestDTO.getPayload(), httpHeaders), String.class);
			klarnaLoggerUtil.logRestResponse(responseEntity);
			populateResponseDTO(responseDTO, responseEntity);
		}
      catch (final HttpStatusCodeException ex) {
			LOG.error("HttpStatusCodeException while calling Klarna API {} ", ex.getMessage());
			LOG.error("Error Details -> Error Code: {}, Error Message: {} ", ex.getRawStatusCode(), ex.getResponseBodyAsString());
			final KlarnaErrorDTO errorDTO = new KlarnaErrorDTO();
			errorDTO.setErrorCode(String.valueOf(ex.getRawStatusCode()));
			errorDTO.setErrorMessage(ex.getResponseBodyAsString());
			responseDTO.setError(errorDTO);
      }
		if(LOG.isDebugEnabled()) {
			LOG.debug("Klarna API call completed.");
		}
		return responseDTO;
	}

	protected HttpHeaders getHttpHeaders(final Map<String, Object> apiHeaderMap)
	{
		final HttpHeaders httpHeaders = new HttpHeaders();
		for (final Map.Entry<String, Object> entry : apiHeaderMap.entrySet())
		{
			if (StringUtils.equalsIgnoreCase(entry.getKey(), HttpHeaders.CONTENT_TYPE))
			{
				httpHeaders.setContentType((MediaType) entry.getValue());
			}
			else if (StringUtils.equalsIgnoreCase(entry.getKey(), HttpHeaders.ACCEPT))
			{
				httpHeaders.setAccept((List<MediaType>) entry.getValue());
			}
			else
			{
				httpHeaders.add(entry.getKey(), (String) entry.getValue());
			}
		}
		return httpHeaders;
	}

	protected KlarnaAPIResponseDTO populateResponseDTO(final KlarnaAPIResponseDTO responseDTO,
			final ResponseEntity<String> responseEntity)
	{
		if (responseEntity.getStatusCode().is2xxSuccessful())
		{
			responseDTO.setPayload(responseEntity.getBody());
		}
		else
		{
			LOG.error("Error while calling Klarna API. Error Code: {}, Error Details: {} ", responseEntity.getStatusCode(),
					responseEntity.getBody());
			final KlarnaErrorDTO errorDTO = new KlarnaErrorDTO();
			errorDTO.setErrorCode(String.valueOf(responseEntity.getStatusCode()));
			errorDTO.setErrorMessage(responseEntity.getBody());
			responseDTO.setError(errorDTO);
		}
		return responseDTO;
	}

	public RestTemplate getRestTemplate()
	{
		return restTemplate;
	}

	public void setRestTemplate(final RestTemplate restTemplate)
	{
		this.restTemplate = restTemplate;
	}
}
