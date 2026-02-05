package com.klarna.integration.populator;

import de.hybris.platform.converters.Populator;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;

import com.klarna.data.KlarnaCredentialData;
import com.klarna.integration.constants.KlarnaIntegrationConstants;
import com.klarna.integration.dto.KlarnaAPIRequestDTO;
import com.klarna.integration.dto.KlarnaRequestDTO;
import com.klarna.integration.enums.TransactionTypeEnum;
import com.klarna.integration.util.KlarnaIntegrationUtil;


public class KlarnaAPIRequestPopulator implements Populator<KlarnaRequestDTO, KlarnaAPIRequestDTO>
{

	private static final Logger LOG = LoggerFactory.getLogger(KlarnaAPIRequestPopulator.class);

	@Resource(name = "klarnaIntegrationUtil")
	protected KlarnaIntegrationUtil klarnaIntegrationUtil;

	@Override
	public void populate(final KlarnaRequestDTO source, final KlarnaAPIRequestDTO target)
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		target.setHeaders(getHeaderMap(source));
		target.setMethod(getHttpMethod(source.getType()));
		target.setUrl(klarnaIntegrationUtil.getKlarnaApiUrl(source));
	}

	protected Map<String, Object> getHeaderMap(final KlarnaRequestDTO requestDTO)
	{
		final Map<String, Object> headerMap = new HashMap<>();
		headerMap.put(HttpHeaders.ACCEPT, Arrays.asList(MediaType.ALL));
		headerMap.put(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		headerMap.put(HttpHeaders.AUTHORIZATION, getAuthorizationHeader(requestDTO.getConfig().getCredential()));
		headerMap.put(KlarnaIntegrationConstants.KLARNA_INTEGRATION_METADATA_HEADER,
				klarnaIntegrationUtil.convertRequestDtoToString(requestDTO.getMetaData()));
		return headerMap;
	}

	protected String getAuthorizationHeader(final KlarnaCredentialData credential)
	{
		final String auth = credential.getApiUserName() + ":" + credential.getApiPassword();
		final byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
		return "Basic " + new String(encodedAuth, StandardCharsets.UTF_8);

	}

	protected HttpMethod getHttpMethod(final TransactionTypeEnum transactionType)
	{
		if (transactionType == null)
		{
			LOG.error("Error! Request type is missing.");
			return null;
		}
		switch (transactionType)
		{
			case CREATE_PAYMENT_REQUEST:
				return HttpMethod.POST;

			case CREATE_WEBHOOK:
				return HttpMethod.POST;

			case UPDATE_WEBHOOK:
				return HttpMethod.PATCH;

			case DELETE_WEBHOOK:
				return HttpMethod.DELETE;

			case SIMULATE_WEBHOOK:
				return HttpMethod.POST;

			case CREATE_SIGNING_KEY:
				return HttpMethod.POST;

			case DELETE_SIGNING_KEY:
				return HttpMethod.DELETE;

			default:
				return HttpMethod.GET;
		}
	}

}
