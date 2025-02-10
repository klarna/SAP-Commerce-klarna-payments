package com.klarna.payment.util;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;

import org.apache.log4j.Logger;

import com.klarna.payment.constants.GeneratedKlarnapaymentConstants.Enumerations.KlarnaEnv;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.SignedJWT;

public final class KlarnaTokenUtils
{
	private static final Logger LOG = Logger.getLogger(KlarnaTokenUtils.class);

	public static boolean validateJWTToken(final String token, String environment)
	{
		try {
			URL url = null;
			if(environment.equalsIgnoreCase(KlarnaEnv.PRODUCTION)) {
				url = new URL("https://login.klarna.com/eu/lp/idp/.well-known/jwks.json");
			}
			else
			{
				url = new URL("https://login.playground.klarna.com/eu/lp/idp/.well-known/jwks.json");
			}
			
			final JWKSet jwkSet = JWKSet.load(url);
			final SignedJWT signedJWT = SignedJWT.parse(token);
			final String keyId = signedJWT.getHeader().getKeyID();
			final JWK jwk = jwkSet.getKeyByKeyId(keyId);
			final JWSVerifier verifier = new RSASSAVerifier(jwk.toRSAKey().toRSAPublicKey());
			return signedJWT.verify(verifier);
			// Check claims
		}
		catch (ParseException | JOSEException | IOException e)
		{
			LOG.error("Exception while validating token :: ", e);
			return false;
		}
	}
}
