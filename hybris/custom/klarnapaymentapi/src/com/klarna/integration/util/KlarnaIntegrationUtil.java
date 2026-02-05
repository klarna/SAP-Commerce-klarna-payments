package com.klarna.integration.util;

import de.hybris.platform.util.Config;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klarna.integration.constants.KlarnaIntegrationConstants;
import com.klarna.integration.dto.KlarnaRequestDTO;

public final class KlarnaIntegrationUtil
{
	private static final Logger LOG = LoggerFactory.getLogger(KlarnaIntegrationUtil.class);

	@Resource(name = "klarnaObjectMapper")
	protected ObjectMapper objectMapper;

	public String getKlarnaApiUrl(final KlarnaRequestDTO requestDTO)
	{
		final String environment = StringUtils.lowerCase(requestDTO.getConfig().getEnvironment());
		final String region = StringUtils.lowerCase(requestDTO.getConfig().getCredential().getMarketRegion());
		final StringBuilder apiUrl = new StringBuilder(getKlarnaApiBaseUrl(environment, region));
		apiUrl.append(getApiEndpoint(requestDTO.getType().name()));
		return apiUrl.toString();
	}

	public String getKlarnaApiBaseUrl(final String environment, final String region)
	{
		final StringBuilder baseUrlKey = new StringBuilder(KlarnaIntegrationConstants.KLARNA_API_BASE_URL_KEY);
		if (StringUtils.isNotEmpty(region))
		{
			baseUrlKey.append(".").append(StringUtils.lowerCase(region));
		}
		if (StringUtils.isNotEmpty(environment))
		{
			baseUrlKey.append(".").append(StringUtils.lowerCase(environment));
		}
		final String baseUrlKeyString = baseUrlKey.toString();
		String baseUrl = Config.getParameter(baseUrlKeyString);
		if(StringUtils.isEmpty(baseUrl)) {
			baseUrl = Config.getParameter(StringUtils.replace(baseUrlKeyString, StringUtils.lowerCase(region),
					StringUtils.lowerCase(KlarnaIntegrationConstants.GLOBAL_REGION)));
		}
		return baseUrl;
	}

	public String getApiEndpoint(final String transactionType)
	{
		if (StringUtils.isNotEmpty(transactionType))
		{
			final StringBuilder endpointKey = new StringBuilder(KlarnaIntegrationConstants.KLARNA_API_BASE_URL_KEY);
			endpointKey.append(".").append(StringUtils.lowerCase(transactionType));
			return Config.getParameter(endpointKey.toString());
		}
		return null;
	}

	public String appendRequestParamsToUrl(final String url, final Map<String, String> requestParamsMap)
	{
		if (StringUtils.isNotEmpty(url) && MapUtils.isNotEmpty(requestParamsMap))
		{
			final StringBuilder urlWithParams = new StringBuilder(url).append("?");
			for (final Map.Entry<String, String> entry : requestParamsMap.entrySet())
			{
				urlWithParams.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
			}
			return StringUtils.substringBeforeLast(urlWithParams.toString(), "&");
		}
		return url;
	}

	public String convertRequestDtoToString(final Object requestDto)
	{
		String requestString = null;
		try
		{
			requestString = objectMapper.writeValueAsString(requestDto);
		}
		catch (final JsonProcessingException e)
		{
			LOG.error("JSON processing exception", e);
		}
		return requestString;
	}

	public <T> T convertResponseStringToDto(final String response, final Class<T> dtoClass)
	{
		try
		{
			if (StringUtils.isNotEmpty(response))
			{
				return objectMapper.readValue(response, dtoClass);
			}
		}
		catch (final Exception e)
		{
			LOG.error("Exception in parsing response string to DTO class " + dtoClass.getName() + " ::", e);
		}
		return null;
	}

}
