package com.klarnapayment.strategy.impl;

import de.hybris.platform.acceleratorstorefrontcommons.security.GUIDCookieStrategy;
import de.hybris.platform.acceleratorstorefrontcommons.strategy.CartRestorationStrategy;
import de.hybris.platform.commercefacades.consent.CustomerConsentDataStrategy;
import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.jalo.JaloSession;
import de.hybris.platform.jalo.user.UserManager;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.security.spring.HybrisSessionFixationProtectionStrategy;
import de.hybris.platform.servicelayer.session.SessionService;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.site.BaseSiteService;
import de.hybris.platform.yacceleratorstorefront.security.GUIDAuthenticationSuccessHandler;
import de.hybris.platform.yacceleratorstorefront.security.impl.DefaultAutoLoginStrategy;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.WebAuthenticationDetails;


public class DefaultKlarnaSignInLoginStrategy extends DefaultAutoLoginStrategy
{
	private static final Logger LOG = LoggerFactory.getLogger(DefaultKlarnaSignInLoginStrategy.class);

	private static final String CUSTOMER_GROUP = "ROLE_CUSTOMERGROUP";

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "guidCookieStrategy")
	private GUIDCookieStrategy guidCookieStrategy;

	@Resource(name = "cartRestorationStrategy")
	private CartRestorationStrategy cartRestorationStrategy;

	@Resource(name = "fixation")
	HybrisSessionFixationProtectionStrategy sessionFixationStrategy;

	@Resource(name = "customerConsentDataStrategy")
	CustomerConsentDataStrategy customerConsentDataStrategy;

	@Resource(name = "rememberMeServices")
	private RememberMeServices rememberMeServices;

	@Resource(name = "loginGuidAuthenticationSuccessHandler")
	private GUIDAuthenticationSuccessHandler loginGuidAuthenticationSuccessHandler;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "baseSiteService")
	private BaseSiteService baseSiteService;

	@Resource(name = "modelService")
	private ModelService modelService;

	@Override
	public void login(final String userId, final String password, final HttpServletRequest request,
			final HttpServletResponse response)
	{

		try
		{
			Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
			authorities.add(new SimpleGrantedAuthority(CUSTOMER_GROUP));
			final UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userId, null, authorities);
			final WebAuthenticationDetails webAuthenticationDetails = new WebAuthenticationDetails(request);
			token.setDetails(webAuthenticationDetails);

			SecurityContextHolder.getContext().setAuthentication(token);
			userService.setCurrentUser(userService.getUserForUID(userId));
			JaloSession.getCurrentSession().setUser(UserManager.getInstance().getUserByLogin(userId));
			guidCookieStrategy.setCookie(request, response);
			sessionFixationStrategy.onAuthentication(token, request, response);
			customerConsentDataStrategy.populateCustomerConsentDataInSession();
			cartService.changeCurrentCartUser(userService.getUserForUID(userId));
			cartRestorationStrategy.restoreCart(request);
			rememberMeServices.loginSuccess(request, response, token);
			customerFacade.loginSuccess();
			LOG.debug("Login Successful for user " + userId);
		}
		catch (final Exception e)
		{
			SecurityContextHolder.getContext().setAuthentication(null);
			LOG.error("Login Failed for user " + userId + " with error " + e.getMessage());
			throw e;
		}
	}

}
