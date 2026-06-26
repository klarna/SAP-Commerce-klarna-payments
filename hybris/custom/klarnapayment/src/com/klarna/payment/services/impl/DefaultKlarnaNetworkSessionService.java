package com.klarna.payment.services.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.session.SessionService;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klarna.integration.dto.KlarnaNetworkSessionDataDTO;
import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.services.KlarnaNetworkSessionService;
import com.klarna.payment.util.KlarnaServicesUtil;
import com.klarna.payment.util.LogHelper;


public class DefaultKlarnaNetworkSessionService implements KlarnaNetworkSessionService
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultKlarnaNetworkSessionService.class);

	@Resource
	private Converter<AbstractOrderModel, KlarnaNetworkSessionDataDTO> klarnaNetworkSessionDataConverter;

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource
	private KlarnaServicesUtil klarnaServicesUtil;

	@Override
	public boolean storeNetworkSessionToken(final String networkSessionToken)
	{
		if (StringUtils.isNotEmpty(networkSessionToken))
		{
			sessionService.setAttribute(KlarnapaymentConstants.KLARNA_NETWORK_SESSION_TOKEN, networkSessionToken);
			LogHelper.debugLog(LOG, "Klarna Network Session Token saved to session:: " + networkSessionToken);
			return true;
		}
		else
		{
			LOG.error("Klarna Network Session Token is not available in the request.");
			return false;
		}
	}

	@Override
	public boolean storeNetworkSessionData(final KlarnaNetworkSessionDataDTO networkSessionData)
	{
		if (networkSessionData != null)
		{
			final String networkSessionDataStr = klarnaServicesUtil.convertRequestDtoToString(networkSessionData);
			sessionService.setAttribute(KlarnapaymentConstants.KLARNA_NETWORK_DATA, networkSessionDataStr);
			LogHelper.debugLog(LOG, "Klarna Network Session Data stored in the session :: " + networkSessionDataStr);
			return true;
		}
		else
		{
			LOG.error("Klarna Network Session Data is null.");
			return false;
		}
	}

	@Override
	public KlarnaNetworkSessionDataDTO createNetworkSessionData()
	{
		final CartModel cartModel = cartService.getSessionCart();
		return klarnaNetworkSessionDataConverter.convert(cartModel);
	}
}
