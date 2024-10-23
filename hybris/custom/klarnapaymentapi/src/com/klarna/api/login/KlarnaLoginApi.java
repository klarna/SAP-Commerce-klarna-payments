package com.klarna.api.login;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.klarna.api.BaseApi;
import com.klarna.api.HttpTransport;
import com.klarna.api.model.ApiException;
import com.klarna.api.model.ApiResponse;
import com.klarna.api.signin.model.KlarnaSigninTokenRequest;
import com.klarna.api.signin.model.KlarnaSigninTokenResponse;

public class KlarnaLoginApi extends BaseApi
{
	protected String TOKEN_API_PATH = "/lp/idp/oauth2/token";

	public KlarnaLoginApi(final HttpTransport transport)
	{
		super(transport);
	}

	public KlarnaSigninTokenResponse getTokens(final KlarnaSigninTokenRequest klarnaSigninTokenRequest)
			throws ApiException, IOException
	{
		final byte[] data = objectMapper.writeValueAsBytes(klarnaSigninTokenRequest);
		final ApiResponse response = this.makePostRequest(TOKEN_API_PATH, data, null);
		response.expectSuccessful().expectStatusCode(Response.Status.OK).expectContentType(MediaType.APPLICATION_JSON);
		return fromJson(response.getBody(), KlarnaSigninTokenResponse.class);
	}

	protected ApiResponse makePostRequest(final String path, final byte[] data, final Map<String, String> headers)
			throws ApiException, IOException
	{
		final ApiResponse response = this.transport.postRequest(path, data, headers);

		// Check if the response has a "Location" header
		final List<String> header = response.getHeader("Location");
		if (header != null)
		{
			this.location = header.get(0);
		}

		this.lastResponse = response;

		return response;
	}
}
