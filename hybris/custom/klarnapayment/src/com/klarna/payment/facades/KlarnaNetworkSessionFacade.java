package com.klarna.payment.facades;

import com.klarna.integration.dto.KlarnaNetworkSessionDataDTO;

public interface KlarnaNetworkSessionFacade
{

	boolean updateNetworkSession(final String networkSessionToken, final String paymentState);

	boolean storeNetworkSessionToken(final String networkSessionToken);

	KlarnaNetworkSessionDataDTO storeNetworkSessionData();

}