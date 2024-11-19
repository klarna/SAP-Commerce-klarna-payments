/*
 * Copyright (c) 2024 SAP SE or an SAP affiliate company. All rights reserved.
 */
package com.klarna.api.shoppingdata;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.log4j.Logger;

import com.klarna.api.BaseApi;
import com.klarna.api.HttpTransport;
import com.klarna.api.model.ApiException;
import com.klarna.api.model.ApiResponse;
import com.klarna.api.shoppingdata.model.ShoppingCreateSessionRequest;
import com.klarna.api.shoppingdata.model.ShoppingCreateSessionResponse;

/**
 *
 */
public class KlarnaShoppingDataAPI extends BaseApi
{
	protected static final Logger LOG = Logger.getLogger(KlarnaShoppingDataAPI.class);

	protected String SIDE_CHANNEL_API_PATH;

	public KlarnaShoppingDataAPI(final HttpTransport transport, final String sessionId )
	{
		super(transport);
		this.SIDE_CHANNEL_API_PATH = String.format("/v1/shopping/sessions/", sessionId);
	}

	public ShoppingCreateSessionResponse updateShoppingSession(final ShoppingCreateSessionRequest klarnaShoppingDataRequest)
			throws ApiException, IOException
	{
		final byte[] data = objectMapper.writeValueAsBytes(klarnaShoppingDataRequest);
		final ShoppingCreateSessionResponse response = this.makePatchRequest(SIDE_CHANNEL_API_PATH, data, null);
		return response;
	}

	protected ShoppingCreateSessionResponse makePatchRequest(final String path, final byte[] data,
			final Map<String, String> headers) throws ApiException, IOException
	{
		final ApiResponse response = this.transport.patch(path, data, headers);

		LOG.debug("entering KlarnaShoppingDataAPI.makePostRequest ... response " + response);
		if (response != null)
		{

			// Check if the response has a "Location" header
			final List<String> header = response.getHeader("Location");
			if (header != null)
			{
				this.location = header.get(0);
			}
			this.lastResponse = response;
		}
		response.expectSuccessful().expectStatusCode(Response.Status.OK).expectContentType(MediaType.APPLICATION_JSON);
		return fromJson(response.getBody(), ShoppingCreateSessionResponse.class);
	}
}
