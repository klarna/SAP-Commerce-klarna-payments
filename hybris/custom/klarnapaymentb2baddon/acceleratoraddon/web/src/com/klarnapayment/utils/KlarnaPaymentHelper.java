package com.klarnapayment.utils;

import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.servicelayer.session.SessionService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;


public class KlarnaPaymentHelper
{
	protected static final Logger LOG = Logger.getLogger(KlarnaPaymentHelper.class);

	@Resource(name = "guidCookieStrategy")
	private GUIDCookieStrategy guidCookieStrategy;

	@Resource(name = "sessionService")
	private SessionService sessionService;


	public void updateAnonymousCookie(final String uid, final HttpServletRequest request, final HttpServletResponse response)
	{
		guidCookieStrategy.setCookie(request, response);
		sessionService.setAttribute(WebConstants.ANONYMOUS_CHECKOUT, Boolean.TRUE);
		sessionService.setAttribute(WebConstants.ANONYMOUS_CHECKOUT_GUID, uid);
	}
}