package com.klarna.integration.util;

import de.hybris.platform.util.Config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klarna.integration.constants.KlarnaIntegrationConstants;
import com.klarna.integration.dto.KlarnaAPIRequestDTO;

public final class KlarnaLoggerUtil
{
	private static final Logger LOG = LoggerFactory.getLogger(KlarnaLoggerUtil.class);

	@Resource(name = "klarnaObjectMapper")
	protected ObjectMapper objectMapper;

	public void logRestRequest(final KlarnaAPIRequestDTO requestDTO)
	{
		if (!Boolean.TRUE.equals(Config.getBoolean(KlarnaIntegrationConstants.API_LOGGING_ENABLED_KEY, false)))
		{
			return;
		}
		LOG.debug("**************** Rest API Log START ****************");
		LOG.debug("Http Method: {}", requestDTO.getMethod());
		LOG.debug("Api Url: {}", requestDTO.getUrl());
		LOG.debug("Path Params: {}", getMaskedDataMap(requestDTO.getPathParams()));
		LOG.debug("Query Params: {}", getMaskedDataMap(requestDTO.getQueryParams()));

		final Map<String, Object> headerParamMap = requestDTO.getHeaders();
		final MediaType contentType = (MediaType) headerParamMap.get(HttpHeaders.CONTENT_TYPE);
		final Map<String, Object> requestMap = createDataObjectMap(requestDTO.getPayload(), contentType);
		redactSensitiveFields(requestMap, "");
		LOG.debug("Request Body: {}", requestMap);
		LOG.debug("**************** Rest API Request END ****************");
	}

	public void logRestResponse(final ResponseEntity<String> responseEntity)
	{
		if (!Boolean.TRUE.equals(Config.getBoolean(KlarnaIntegrationConstants.API_LOGGING_ENABLED_KEY, false)))
		{
			return;
		}
		if (responseEntity != null)
		{
			final MediaType contentType = (responseEntity.getHeaders() != null
					&& responseEntity.getHeaders().getContentType() != null) ? responseEntity.getHeaders().getContentType()
							: MediaType.APPLICATION_JSON;
			final Map<String, Object> responeMap = createDataObjectMap(responseEntity.getBody(), contentType);
			redactSensitiveFields(responeMap, "");
			LOG.debug("Rest API Call Response Status: {}, Response Body: {}", responseEntity.getStatusCode(), responeMap);
		}
	}

	public Map<String, Object> getMaskedDataMap(final Map<String, String> dataMap)
	{
		final Map<String, Object> maskedDataMap = createDataObjectMap(dataMap, null);
		redactSensitiveFields(maskedDataMap, "");
		return maskedDataMap;
	}

	public Map<String, Object> createDataObjectMap(final Object dataObject, final MediaType contentType)
	{
		if (dataObject == null)
		{
			return Collections.emptyMap();
		}
		try
		{
			if (dataObject instanceof Map)
			{
				return new HashMap<>((Map<String, String>) dataObject);
			}
			else if (MediaType.APPLICATION_JSON.includes(contentType))
			{
				final String dataObjectString = dataObject.toString();
				if (StringUtils.isNotEmpty(dataObjectString))
				{
					return objectMapper.readValue(dataObject.toString(), new TypeReference<>()
					{
					});
				}
			}
			else
			{
				LOG.debug("Unsupported input type");
			}
		}
		catch (final Exception e)
		{
			LOG.error("Error while Mapping data object: " + e);
		}
		return Collections.emptyMap();
	}

	// Recursive masking method with full key path tracking
	public void redactSensitiveFields(final Map<String, Object> map, final String parentPath)
	{
		if (MapUtils.isEmpty(map)
				|| !Boolean.TRUE.equals(Config.getBoolean(KlarnaIntegrationConstants.LOG_REDACT_ENABLED_KEY, true)))
		{
			return;
		}
		for (final Map.Entry<String, Object> entry : map.entrySet())
		{
			final String key = entry.getKey();
			final Object value = entry.getValue();
			final String fullKey = parentPath.isEmpty() ? key : parentPath + "." + key;
			if (isRedactField(fullKey))
			{
				// Mask the value
				entry.setValue("****");
			}
			else if (value instanceof Map)
			{
				redactSensitiveFields((Map<String, Object>) value, fullKey);
			}
			else if (value instanceof List)
			{
				final List<?> list = (List<?>) value;
				IntStream.range(0, list.size()).mapToObj(i -> Map.entry(i, list.get(i))).filter(e -> e.getValue() instanceof Map)
						.forEach(e -> redactSensitiveFields((Map<String, Object>) e.getValue(), fullKey + "[" + e.getKey() + "]"));
			}
		}
	}

	public boolean isRedactField(final String key)
	{
		if (KlarnaIntegrationConstants.getPatternsToMask() != null)
		{
			for (final Pattern pattern : KlarnaIntegrationConstants.getPatternsToMask())
			{
				if (pattern.matcher(key).matches())
				{
					return true;
				}
			}
		}
		return false;
	}

}
