package com.klarna.integration.constants;

import de.hybris.platform.util.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;


public final class KlarnaIntegrationConstants
{
	private KlarnaIntegrationConstants()
	{
		//empty to avoid instantiating this constant class
	}

	public static final String KLARNA_API_LOGGING_ENABLED_KEY = "klarna.api.logging.enabled";
	public static final String LOG_REDACT_ENABLED_KEY = "log.redact.enabled";
	public static final String REDACT_FIELDS = Config.getParameter("klarna.redact.fields");

	public static final String KLARNA_API_BASE_URL_KEY = "klarna.api.v2";
	public static final String KLARNA_API_WEBHOOK_ENDPOINT_KEY = "notification.webhooks";
	public static final String KLARNA_API_SIGNING_KEY_ENDPOINT_KEY = "notification.signing-keys";

	public static final String PARTNER_CORRELATION_ID_HEADER = "Partner-Correlation-Id";
	public static final String KLARNA_INTEGRATION_METADATA_HEADER = "Klarna-Integration-Metadata";

	public static final String GLOBAL_REGION = "GLOBAL";

	public static List<Pattern> getPatternsToMask()
	{
		if (StringUtils.isEmpty(REDACT_FIELDS))
		{
			return Collections.emptyList();
		}
		return Arrays.stream(REDACT_FIELDS.split(",")).map(String::trim).filter(s -> !s.isEmpty())
				.map(s -> s.replace("[]", "\\[\\d+\\]").replace(".", "\\.")).map(s -> Pattern.compile(s, Pattern.CASE_INSENSITIVE))
				.collect(Collectors.toList());
	}

}
