/**
 *
 */
package com.klarna.payment.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import com.klarna.api.merchant_card_service.model.CardServiceCard;



public class KlarnaPaymentEncrpt
{
	protected static final Logger LOG = Logger.getLogger(KlarnaPaymentEncrpt.class);

	public static String getDecrypted(final CardServiceCard data, final String Key) throws Exception
	{
		try
		{
			final PrivateKey privKey = generatePrivate(Key);
			final SecretKey key = generateSecretKey(data, privKey);
			final byte[] result = extractPCI(data, key);
			LOG.warn(new String(result));
			return new String(result);
		}
		catch (final GeneralSecurityException e)
		{
			LOG.error(e.getMessage());
			throw new Exception(e);
		}


	}

	/**
	 *
	 */
	private static byte[] extractPCI(final CardServiceCard data, final SecretKey key)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException
	{
		final byte[] decodedIVKEY = Base64.getDecoder().decode(data.getIv().getBytes(StandardCharsets.UTF_8));
		final IvParameterSpec ivSpec = new IvParameterSpec(decodedIVKEY);
		final Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
		return (cipher.doFinal(Base64.getDecoder().decode(data.getPciData())));
	}

	/**
	 *
	 */
	private static SecretKey generateSecretKey(final CardServiceCard data, final PrivateKey privKey)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException,
			BadPaddingException
	{
		final byte[] decodedAesKey = Base64.getDecoder().decode(data.getAesKey().getBytes());
		final Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		cipher.init(Cipher.DECRYPT_MODE, privKey);

		return (new SecretKeySpec(cipher.doFinal(decodedAesKey), "AES"));
	}

	/**
	 *
	 */
	private static PrivateKey generatePrivate(final String Key)
			throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
	{
		final StringBuilder pkcs8Lines = new StringBuilder();
		final BufferedReader rdr = new BufferedReader(new StringReader(Key));
		String line;
		while ((line = rdr.readLine()) != null)
		{
			pkcs8Lines.append(line);
		}
		String pkcs8Pem = pkcs8Lines.toString();
		pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "");
		pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "");
		pkcs8Pem = pkcs8Pem.replaceAll("\\s+", "");

		final byte[] pkcs8EncodedBytes = Base64.getDecoder().decode(pkcs8Pem);
		final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(pkcs8EncodedBytes);
		final KeyFactory kf = KeyFactory.getInstance("RSA");
		return (kf.generatePrivate(keySpec));
	}

}
