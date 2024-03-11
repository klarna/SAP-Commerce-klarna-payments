package com.klarna.api;

import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Map;

import org.mockito.Mock;

import com.klarna.api.HttpTransport;
import com.klarna.api.HttpUrlConnectionTransport;


public class FakeHttpUrlConnectionTransport extends HttpUrlConnectionTransport
{
	@Mock
	public HttpURLConnection conn = mock(HttpURLConnection.class);
	public String requestPath;
	public Map<String, String> requestHeaders;
	public OutputStream requestPayout;

	public FakeHttpUrlConnectionTransport(final String merchantId, final String sharedSecret, final URI uri)
	{
		super(merchantId, sharedSecret, uri);
		requestPayout = new ByteArrayOutputStream();
	}

	public FakeHttpUrlConnectionTransport()
	{
		this("merchantId", "sharedSecret", HttpTransport.EU_TEST_BASE_URL);
	}

	public HttpURLConnection testBuildConnection(final String path, final Map<String, String> headers) throws IOException
	{
		return super.buildConnection(path, headers);
	}

	@Override
	protected HttpURLConnection buildConnection(final String path, final Map<String, String> headers) throws IOException
	{
		this.requestHeaders = headers;
		this.requestPath = path;

		doNothing().when(conn).setRequestMethod(isA(String.class));
		doNothing().when(conn).setDoOutput(isA(Boolean.class));

		when(conn.getOutputStream()).thenReturn(requestPayout);

		return conn;
	}
}