package com.klarna.payment.facades.impl;

import javax.annotation.Resource;

import com.klarna.data.KlarnaConfigData;
import com.klarna.integration.dto.KlarnaNetworkSessionDataDTO;
import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.data.KlarnaEventDTO;
import com.klarna.payment.event.KlarnaEventPublisher;
import com.klarna.payment.facades.KlarnaConfigFacade;
import com.klarna.payment.facades.KlarnaNetworkSessionFacade;
import com.klarna.payment.services.KlarnaNetworkSessionService;
import com.klarna.payment.util.KlarnaServicesUtil;


public class DefaultKlarnaNetworkSessionFacade implements KlarnaNetworkSessionFacade
{

	@Resource
	private KlarnaNetworkSessionService klarnaNetworkSessionService;

	@Resource
	private KlarnaConfigFacade klarnaConfigFacade;

	@Resource
	private KlarnaEventPublisher klarnaEventPublisher;

	@Resource
	private KlarnaServicesUtil klarnaServicesUtil;

	@Override
	public boolean updateNetworkSession(final String networkSessionToken, final String paymentState)
	{
		if (storeNetworkSessionToken(networkSessionToken))
		{
			final KlarnaNetworkSessionDataDTO networkSessionData = storeNetworkSessionData();
			publishPaymentUpdateEvent(networkSessionData, networkSessionToken, paymentState);
			return true;
		}
		return false;
	}

	@Override
	public boolean storeNetworkSessionToken(final String networkSessionToken)
	{
		return klarnaNetworkSessionService.storeNetworkSessionToken(networkSessionToken);
	}

	@Override
	public KlarnaNetworkSessionDataDTO storeNetworkSessionData()
	{
		final KlarnaConfigData klarnaConfig = klarnaConfigFacade.getKlarnaConfig();
		if (Boolean.TRUE.equals(klarnaConfig.getIntegratedViaPSP()) && Boolean.TRUE.equals(klarnaConfig.getShareShoppingData()))
		{
			final KlarnaNetworkSessionDataDTO networkSessionData = klarnaNetworkSessionService.createNetworkSessionData();
			klarnaNetworkSessionService.storeNetworkSessionData(networkSessionData);
			return networkSessionData;
		}
		return null;
	}

	private void publishPaymentUpdateEvent(final KlarnaNetworkSessionDataDTO networkSessionData, final String networkSessionToken,
			final String paymentState)
	{
		final KlarnaEventDTO klarnaEventDTO = new KlarnaEventDTO();
		klarnaEventDTO.setKlarnaNetworkSessionToken(networkSessionToken);
		klarnaEventDTO.setKlarnaNetworkData(networkSessionData);
		klarnaEventDTO.setKlarnaNetworkPaymentState(paymentState);
		klarnaEventPublisher.publishProperyChangeEvent(KlarnapaymentConstants.KLARNA_EVENT_DATA, null,
				klarnaServicesUtil.convertRequestDtoToString(klarnaEventDTO));
	}

}
