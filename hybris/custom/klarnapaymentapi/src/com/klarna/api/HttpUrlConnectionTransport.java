/*
 * Copyright 2018 Klarna AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.klarna.api;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klarna.api.model.ApiException;
import com.klarna.api.model.ApiResponse;


/**
 * HttpURLConnection implementation of Transpoert interface. Used to send HTTP requests to API server.
 */
public class HttpUrlConnectionTransport implements HttpTransport
{
	private static final String PATCH = "PATCH";
	/**
	 * Default HTTP request timeout.
	 */
	private static final int DEFAULT_TIMEOUT = 30000;

	/**
	 * Default request Media-Type.
	 */
	private static final String DEFAULT_MEDIA_TYPE = MediaType.APPLICATION_JSON;

	/**
	 * Logger instance.
	 */
	private static final Logger log = LoggerFactory.getLogger(HttpUrlConnectionTransport.class);

	/**
	 * Base API server URL.
	 */
	protected URI baseUri;

	/**
	 * Merchant ID.
	 */
	protected String merchantId;

	/**
	 * Merchant shared secret key.
	 */
	protected String sharedSecret;

	/**
	 * HTTP UserAgent.
	 */
	protected String userAgent;

	/**
	 * HTTP request timeout.
	 */
	protected int timeout = DEFAULT_TIMEOUT;

	/**
	 * HttpUrlConnection Proxy settings.
	 *
	 * @see Proxy
	 */
	protected Proxy proxy;

	/**
	 * Proxy authenticator.
	 *
	 * @see Authenticator
	 */
	protected Authenticator proxyAuth;

	/**
	 * Sets up required params for Klarna API.
	 *
	 * @param merchantId
	 *           Merchant ID/Username (UID)
	 * @param sharedSecret
	 *           Merchant shared secret/password
	 * @param baseUri
	 *           Klarna API URLs.
	 *
	 * @see HttpTransport Base URLs
	 */
	public HttpUrlConnectionTransport(final String merchantId, final String sharedSecret, final URI baseUri)
	{
		this.baseUri = baseUri;
		this.merchantId = merchantId;
		this.sharedSecret = sharedSecret;
		this.userAgent = HttpTransport.USER_AGENT;
	}


	public HttpUrlConnectionTransport(final String merchantId, final String sharedSecret, final URI baseUri,
			final String userAgent)
	{
		this.baseUri = baseUri;
		this.merchantId = merchantId;
		this.sharedSecret = sharedSecret;
		this.userAgent = userAgent;

	}

	public HttpUrlConnectionTransport(final URI baseUri)
	{
		this.baseUri = baseUri;
	}

	/**
	 * Sends HTTP GET request to specified path.
	 *
	 * @param path
	 *           URL path
	 * @param headers
	 *           HTTP request headers
	 * @return Processed response
	 * @throws ApiException
	 *            if API server returned non-20x HTTP CODE and response contains a
	 *            <a href="https://developers.klarna.com/api/#errors">Error</a>
	 * @throws IOException
	 *            if an error occurred when connecting to the server or when parsing a response.
	 */
	public ApiResponse get(final String path, final Map<String, String> headers) throws ApiException, IOException
	{
		final HttpURLConnection conn = this.buildConnection(path, headers);
		conn.setRequestMethod("GET");

		return this.makeRequest(conn, null);
	}

	/**
	 * Sends HTTP POST request to specified path.
	 *
	 * @param path
	 *           URL path
	 * @param data
	 *           Data to be sent to API server in a payload
	 * @param headers
	 *           HTTP request headers
	 * @return Processed response
	 * @throws ApiException
	 *            if API server returned non-20x HTTP CODE and response contains a
	 *            <a href="https://developers.klarna.com/api/#errors">Error</a>
	 * @throws IOException
	 *            if an error occurred when connecting to the server or when parsing a response.
	 */
	public ApiResponse post(final String path, final byte[] data, final Map<String, String> headers)
			throws ApiException, IOException
	{
		final HttpURLConnection conn = this.buildConnection(path, headers);
		conn.setRequestMethod("POST");

		return this.makeRequest(conn, data);
	}

	/**
	 * Sends HTTP PUT request to specified path.
	 *
	 * @param path
	 *           URL path
	 * @param data
	 *           Data to be sent to API server in a payload
	 * @param headers
	 *           HTTP request headers
	 * @return Processed response
	 * @throws ApiException
	 *            if API server returned non-20x HTTP CODE and response contains a
	 *            <a href="https://developers.klarna.com/api/#errors">Error</a>
	 * @throws IOException
	 *            if an error occurred when connecting to the server or when parsing a response.
	 */
	public ApiResponse put(final String path, final byte[] data, final Map<String, String> headers)
			throws ApiException, IOException
	{
		final HttpURLConnection conn = this.buildConnection(path, headers);
		conn.setRequestMethod("PUT");

		return this.makeRequest(conn, data);
	}

	/**
	 * Sends HTTP PATCH request to specified path.
	 *
	 * @param path
	 *           URL path
	 * @param data
	 *           Data to be sent to API server in a payload
	 * @param headers
	 *           HTTP request headers
	 * @return Processed response
	 * @throws ApiException
	 *            if API server returned non-20x HTTP CODE and response contains a
	 *            <a href="https://developers.klarna.com/api/#errors">Error</a>
	 * @throws IOException
	 *            if an error occurred when connecting to the server or when parsing a response.
	 */
	public ApiResponse patch(final String path, final byte[] data, final Map<String, String> headers)
			throws ApiException, IOException
	{
		final HttpURLConnection conn = this.buildConnection(path, headers);
		conn.setRequestMethod(PATCH);

		return this.makeRequest(conn, data);
	}

	/**
	 * Sends HTTP DELETE request to specified path.
	 *
	 * @deprecated As of 3.1.0, adding `data` params to delete in order to cover the InstantShoppingApi Use
	 *             {@link #delete(String, byte[], Map)} instead.
	 *
	 * @param path
	 *           URL path
	 * @param headers
	 *           HTTP request headers
	 * @return Processed response
	 * @throws ApiException
	 *            if API server returned non-20x HTTP CODE and response contains a
	 *            <a href="https://developers.klarna.com/api/#errors">Error</a>
	 * @throws IOException
	 *            if an error occurred when connecting to the server or when parsing a response.
	 */
	@Deprecated
	public ApiResponse delete(final String path, final Map<String, String> headers) throws ApiException, IOException
	{
		final HttpURLConnection conn = this.buildConnection(path, headers);
		conn.setRequestMethod("DELETE");

		return this.makeRequest(conn, null);
	}

	/**
	 * Sends HTTP DELETE request to specified path.
	 *
	 * @param path
	 *           URL path
	 * @param headers
	 *           HTTP request headers
	 * @return Processed response
	 * @throws ApiException
	 *            if API server returned non-20x HTTP CODE and response contains a
	 *            <a href="https://developers.klarna.com/api/#errors">Error</a>
	 * @throws IOException
	 *            if an error occurred when connecting to the server or when parsing a response.
	 */
	public ApiResponse delete(final String path, final byte[] data, final Map<String, String> headers)
			throws ApiException, IOException
	{
		final HttpURLConnection conn = this.buildConnection(path, headers);
		conn.setRequestMethod("DELETE");

		return this.makeRequest(conn, data);
	}

	/**
	 * Gets current UserAgent.
	 *
	 * @return UserAgent
	 */
	public String getUserAgent()
	{
		return this.userAgent;
	}

	/**
	 * Sets new UserAgent. The UserAgent will be added as 'User-Agent' header to the HTTP request.
	 *
	 * @param userAgent
	 *           new UserAgent
	 * @return self
	 */
	public HttpUrlConnectionTransport setUserAgent(final String userAgent)
	{
		this.userAgent = userAgent;

		return this;
	}

	/**
	 * Gets current Timeout limit (in seconds) for an HTTP request.
	 *
	 * @return Timeout
	 */
	public int getTimeout()
	{
		return this.timeout;
	}

	/**
	 * Sets current Timeout limit (in seconds) for an HTTP request.
	 *
	 * @param timeout
	 *           Timeout in milliseconds
	 * @return self
	 */
	public HttpUrlConnectionTransport setTimeout(final int timeout)
	{
		this.timeout = timeout;

		return this;
	}

	/**
	 * Sets new Proxy settings
	 *
	 * @param proxy
	 *           Proxy
	 */
	public void setProxy(final Proxy proxy)
	{
		this.proxy = proxy;
	}

	/**
	 * Sets new Proxy settings
	 *
	 * @param scheme
	 *           Proxy scheme (http, https)
	 * @param host
	 *           Proxy host
	 * @param port
	 *           Proxy port
	 */
	public void setProxy(final Proxy.Type scheme, final String host, final int port)
	{
		this.proxy = new Proxy(scheme, new InetSocketAddress(host, port));
	}

	/**
	 * Sets new Proxy settings
	 *
	 * @param scheme
	 *           Proxy scheme (http, https)
	 * @param host
	 *           Proxy host
	 * @param port
	 *           Proxy port
	 * @param username
	 *           Auth: Proxy user name
	 * @param password
	 *           Auth: Proxy password
	 */
	public void setProxy(final Proxy.Type scheme, final String host, final int port, final String username, final String password)
	{
		this.setProxy(scheme, host, port);
		this.proxyAuth = new Authenticator()
		{
			@Override
			public PasswordAuthentication getPasswordAuthentication()
			{
				return (new PasswordAuthentication(username, password.toCharArray()));
			}
		};
		Authenticator.setDefault(this.proxyAuth);
	}

	protected HttpURLConnection buildConnection(final String path, final Map<String, String> headers) throws IOException
	{
		final URL url = this.buildPath(path);

		HttpURLConnection conn;
		if (this.proxy != null)
		{
			conn = (HttpURLConnection) url.openConnection(this.proxy);
		}
		else
		{
			conn = (HttpURLConnection) url.openConnection();
		}

		conn.setRequestProperty("Content-Type", DEFAULT_MEDIA_TYPE);
		conn.setRequestProperty("User-Agent", this.userAgent);
		conn.setConnectTimeout(this.timeout);
		conn.setReadTimeout(this.timeout);

		this.authorize(conn);

		if (headers != null)
		{
			for (final String key : headers.keySet())
			{
				conn.setRequestProperty(key, headers.get(key));
			}
		}

		return conn;
	}

	public ApiResponse postRequest(final String path, final byte[] data, final Map<String, String> headers)
			throws ApiException, IOException
	{
		final HttpURLConnection conn = this.buildConnectionWithoutAuthorize(path, headers);
		conn.setRequestMethod("POST");

		return this.makeRequest(conn, data);
	}

	protected HttpURLConnection buildConnectionWithoutAuthorize(final String path, final Map<String, String> headers)
			throws IOException
	{
		final URL url = this.buildPath(path);

		HttpURLConnection conn;
		if (this.proxy != null)
		{
			conn = (HttpURLConnection) url.openConnection(this.proxy);
		}
		else
		{
			conn = (HttpURLConnection) url.openConnection();
		}

		//conn.setRequestProperty("Content-Type", DEFAULT_MEDIA_TYPE);
		//conn.setRequestProperty("User-Agent", this.userAgent);
		conn.setConnectTimeout(this.timeout);
		conn.setReadTimeout(this.timeout);
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		if (headers != null)
		{
			for (final String key : headers.keySet())
			{
				conn.setRequestProperty(key, headers.get(key));
			}
		}

		return conn;
	}

	protected void authorize(final HttpURLConnection conn) throws IOException
	{
		this.setBase64Auth(conn, this.merchantId, this.sharedSecret);
	}

	protected ApiResponse makeRequest(final HttpURLConnection conn, final byte[] payout) throws IOException
	{
		if (log.isDebugEnabled())
		{
			log.debug("Request\n" + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n" + conn.getRequestMethod() + ": " + conn.getURL()
					+ "\n" + "Headers: " + conn.getRequestProperties() + "\n" + "Payout: "
					+ (payout == null ? "null" : new String(payout)) + "\n");
		}

		if (payout != null)
		{
			conn.setDoOutput(true);


			final OutputStream os = conn.getOutputStream();
			os.write(payout);

		}

		final ApiResponse response = new ApiResponse();

		response.setStatus(conn.getResponseCode());
		response.setHeaders(conn.getHeaderFields());


		final InputStream is = response.isSuccessful() ? conn.getInputStream() : conn.getErrorStream();
		if (is != null)
		{

			final ByteArrayOutputStream os1 = new ByteArrayOutputStream();
			int bytes;
			while ((bytes = is.read()) != -1)
			{
				os1.write(bytes);
			}

			response.setBody(os1.toByteArray());

		}


		if (log.isDebugEnabled())
		{
			log.debug("Response\n" + "<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<\n" + "Headers: " + conn.getHeaderFields() + "\n"
					+ "Body: " + (response.getBody() == null ? "null" : new String(response.getBody())) + "\n");
		}

		return response;
	}

	private URL buildPath(final String path) throws MalformedURLException
	{
		URI uri = this.baseUri;
		final String newPath = uri.getPath() + path;
		uri = uri.resolve(newPath);
		return uri.toURL();
	}

	private void setBase64Auth(final HttpURLConnection conn, final String username, final String password) throws IOException
	{
		final byte[] message = (username + ":" + password).getBytes(StandardCharsets.UTF_8);
		final String encoded = javax.xml.bind.DatatypeConverter.printBase64Binary(message);

		conn.setRequestProperty("Authorization", "Basic " + encoded);
	}


}