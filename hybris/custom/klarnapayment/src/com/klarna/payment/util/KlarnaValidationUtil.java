package com.klarna.payment.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

public final class KlarnaValidationUtil
{
	private static final Logger LOG = Logger.getLogger(KlarnaValidationUtil.class);
	private static final String HMAC_SHA256 = "HmacSHA256";

	public boolean validateSignature(final String requestBody, final String signature, final String savedSigningKey)
	{
		try
		{
			final byte[] derivedSignature = hmacSha256(requestBody.getBytes(StandardCharsets.UTF_8),
					savedSigningKey.getBytes(StandardCharsets.UTF_8));
			final byte[] providedSignature = hexToBytes(signature);
			return MessageDigest.isEqual(derivedSignature, providedSignature);
		}
		catch (final Exception e)
		{
			LOG.error("Signature validation failed. Exception :: ", e);
			return false;
		}

	}

	private static byte[] hmacSha256(final byte[] message, final byte[] key)
	{
		try
		{
			final Mac mac = Mac.getInstance(HMAC_SHA256);
			mac.init(new SecretKeySpec(key, HMAC_SHA256));
			return mac.doFinal(message);
		}
		catch (final Exception e)
		{
			throw new IllegalStateException("Failed to compute HMAC-SHA-256", e);
		}
	}

	private static byte[] hexToBytes(final String s)
	{
		final int len = s.length();
		if ((len & 1) != 0)
		{
			throw new IllegalArgumentException("Odd-length hex string");
		}
		final byte[] out = new byte[len / 2];
		for (int i = 0; i < len; i += 2)
		{
			final int hi = Character.digit(s.charAt(i), 16);
			final int lo = Character.digit(s.charAt(i + 1), 16);
			if (hi < 0 || lo < 0)
			{
				throw new IllegalArgumentException("Invalid hex character");
			}
			out[i / 2] = (byte) ((hi << 4) + lo);
		}
		return out;
	}


}
