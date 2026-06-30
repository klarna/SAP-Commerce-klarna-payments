package com.klarna.payment.services;

import com.klarna.integration.dto.KlarnaNetworkSessionDataDTO;

public interface KlarnaNetworkSessionService
{
	boolean storeNetworkSessionToken(final String networkSessionToken);

	KlarnaNetworkSessionDataDTO createNetworkSessionData();

	boolean storeNetworkSessionData(final KlarnaNetworkSessionDataDTO networkSessionData);
}