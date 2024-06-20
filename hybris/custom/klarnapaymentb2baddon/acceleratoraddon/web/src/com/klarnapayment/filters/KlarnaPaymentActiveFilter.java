package com.klarnapayment.filters;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.web.filter.OncePerRequestFilter;

import com.klarna.data.KlarnaConfigData;
import com.klarna.payment.facades.KPConfigFacade;
import com.klarna.payment.facades.KPPaymentCheckoutFacade;
import com.klarna.payment.util.LogHelper;


/**
 * This filter intercept request and direct customer to Klarna checkout page if it's activated. Otherwise, it will
 * direct customer to default checkout flow.
 */
public class KlarnaPaymentActiveFilter extends OncePerRequestFilter
{

	private static final Logger LOG = Logger.getLogger(KlarnaPaymentActiveFilter.class.getName());


	public final static String KLARNA_PAYMENT = "/klarna/payment";
	public final static String DEFAULT_CHECKOUT_URL = "/checkout/multi/summary/placeOrder";
	public final static String KLARNA_CHECKOUT_URL = "/checkout/multi/summary/klarna/placeOrder";
	public final static String DEFAULT_CONFIRMATION = "/checkout/orderConfirmation/";
	private static final String PAYMENT_OPTION = "paymentOption";
	private static final String KLARNA_LOGO = "klarna_logo";
	private static final String KLARNA_DISPLAYNAME = "klarna_displayname";
	private static final String IS_KLARNA_ACTIVE = "is_klarna_active";

	@Resource(name = "kpConfigFacade")
	private KPConfigFacade kpConfigFacade;
	@Resource(name = "kpPaymentCheckoutFacade")
	private KPPaymentCheckoutFacade kpPaymentCheckoutFacade;

	/**
	 * This filter is used to load config from KlarnaConfig Model, to analyze the environment checkout. If exist and is
	 * active use the Klarna checkout page else use default checkout page.
	 *
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws ServletException
	 * @throws IOException
	 */
	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{

		final String requestURL = request.getServletPath();
		final KlarnaConfigData klarnaConfig = kpConfigFacade.getKlarnaConfig();
		final HttpSession session = request.getSession();
		if (requestURL.contains(DEFAULT_CONFIRMATION))
		{
			LogHelper.debugLog(LOG, "Remiving the klaran secific session attributes ...");
			session.removeAttribute("sessionId");
			session.removeAttribute("clientToken");
		}
		LogHelper.debugLog(LOG, "The current request URL [" + requestURL + "] ...");

		if (requestURL.contains(DEFAULT_CHECKOUT_URL) && klarnaConfig != null && BooleanUtils.isTrue(klarnaConfig.getActive())
				&& kpPaymentCheckoutFacade.isKlarnaPayment())
		{
			LogHelper.debugLog(LOG, "Redirecting to Klarna Checkout URL");

			sendRedirect(KLARNA_CHECKOUT_URL, request, response);
			return;
		}
		if (requestURL.contains(KLARNA_CHECKOUT_URL) && !kpPaymentCheckoutFacade.isKlarnaPayment())
		{
			LogHelper.debugLog(LOG, "Redirecting to Default Checkout URL");
			sendRedirect(DEFAULT_CHECKOUT_URL, request, response);
			return;
		}
		setRequestAttributes(requestURL, klarnaConfig, request);
		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);

		filterChain.doFilter(request, response);
	}

	private void setRequestAttributes(final String requestURL, final KlarnaConfigData klarnaConfig,
			final HttpServletRequest request)
	{
		if (requestURL.contains("/checkout/multi/payment-method"))
		{
			if (klarnaConfig != null && klarnaConfig.getActive().booleanValue())
			{
				LogHelper.debugLog(LOG, "setting klarna parameters");
				//request.setAttribute(PAYMENT_OPTION, kpConfigFacade.getPaymentOption());
				//request.setAttribute(KLARNA_LOGO, kpConfigFacade.getLogo());
				//request.setAttribute(KLARNA_DISPLAYNAME, kpConfigFacade.getDisplayName());
				request.setAttribute(IS_KLARNA_ACTIVE, kpConfigFacade.getKlarnaConfig().getActive());
			}
			else
			{
				request.setAttribute(IS_KLARNA_ACTIVE, Boolean.FALSE);
			}
		}
	}

	private void sendRedirect(final String url, final HttpServletRequest request, final HttpServletResponse response)
			throws IOException
	{
		final String contextPath = request.getContextPath();
		final String encodedRedirectUrl = response.encodeRedirectURL(contextPath + url);
		response.sendRedirect(encodedRedirectUrl);
	}

}
