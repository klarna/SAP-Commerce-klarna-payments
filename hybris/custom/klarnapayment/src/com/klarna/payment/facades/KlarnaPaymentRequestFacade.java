package com.klarna.payment.facades;

import com.klarna.integration.dto.KlarnaPaymentResponsePayloadDTO;


public interface KlarnaPaymentRequestFacade
{
	KlarnaPaymentResponsePayloadDTO createPaymentRequest();

}