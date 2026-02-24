package com.klarna.payment.util;

import de.hybris.platform.util.Config;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.log4j.Logger;

import com.microsoft.sqlserver.jdbc.StringUtils;


public final class KlarnaValidationUtil
{
	private static final Logger LOG = Logger.getLogger(KlarnaValidationUtil.class);
	private static final String HMAC_SHA_256 = "HmacSHA256";
	private static final String KLARNA_WEBHOOK_SIGNATURE_VERIFICATION_FLAG = "klarna.webhook.signature.verification.enabled";

	public boolean validateSignature(final byte[] requestBody, final String signature, final String savedSigningKey)
	{
		if (!Config.getBoolean(KLARNA_WEBHOOK_SIGNATURE_VERIFICATION_FLAG, true))
		{
			LogHelper.debugLog(LOG, "Signature validation is not enabled.");
			return true;
		}

		LogHelper.debugLog(LOG, "Validating Webhook Signature");
		if (StringUtils.isEmpty(savedSigningKey))
		{
			LOG.error("Cannot validate webhook request. No Signing Key available for the store.");
			return false;
		}
		try
		{
			final SecretKeySpec secretKeySpec = new SecretKeySpec(savedSigningKey.getBytes(StandardCharsets.UTF_8), HMAC_SHA_256);

			final Mac mac = Mac.getInstance(HMAC_SHA_256);
			mac.init(secretKeySpec);

			final byte[] hmacBytes = mac.doFinal(requestBody);
			final String computedSignature = Hex.encodeHexString(hmacBytes);
			return computedSignature.equals(signature);
		}
		catch (final NoSuchAlgorithmException | InvalidKeyException e)
		{
			LOG.error("Failed to compute HMAC-SHA-256 signature", e);
			return false;
		}
	}

}
