package com.klarnapayment.filters;

import de.hybris.platform.store.BaseStoreModel;
import de.hybris.platform.store.services.BaseStoreService;

import java.io.IOException;

import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import com.klarna.payment.facades.KlarnaConfigFacade;


public class NetPriceForNorthAmericanFilter extends OncePerRequestFilter
{
	private static final Logger LOG = LoggerFactory.getLogger(NetPriceForNorthAmericanFilter.class.getName());

	public final static String KLARNA_CHECKOUT_URL = "/klarna/";

	@Resource(name = "klarnaConfigFacade")
	private KlarnaConfigFacade klarnaConfigFacade;
	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;


	@Override
	protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain) throws ServletException, IOException
	{
		final String requestURL = request.getServletPath();

		if (LOG.isDebugEnabled())
		{
			LOG.debug("Filter on: [" + requestURL + "]");
		}
		if (requestURL.contains(KLARNA_CHECKOUT_URL) && !validBaseStoreConfiguration())
		{
			throw new IllegalArgumentException("Price config for this store invalid!");
		}
		filterChain.doFilter(request, response);
	}

	protected boolean validBaseStoreConfiguration()
	{
		final BaseStoreModel baseStoreModel = baseStoreService.getCurrentBaseStore();
		if (klarnaConfigFacade.isNorthAmerianKlarnaPayment())
		{
			if (!baseStoreModel.isNet())
			{
				LOG.error("Price config for this store should is NET");
				return false;
			}
		}
		else
		{
			if (baseStoreModel.isNet())
			{
				LOG.error("Price config for this store should is GROSS");
				return false;
			}
		}
		return true;
	}


	/**
	 * @param klarnaConfigFacade
	 *           the klarnaConfigFacade to set
	 */
	public void setKlarnaConfigFacade(final KlarnaConfigFacade klarnaConfigFacade)
	{
		this.klarnaConfigFacade = klarnaConfigFacade;
	}

	public BaseStoreService getBaseStoreService()
	{
		return baseStoreService;
	}

	public void setBaseStoreService(final BaseStoreService baseStoreService)
	{
		this.baseStoreService = baseStoreService;
	}
}
