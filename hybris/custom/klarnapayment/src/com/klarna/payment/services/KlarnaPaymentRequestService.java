package com.klarna.payment.services;

import de.hybris.platform.core.model.order.AbstractOrderModel;

import com.klarna.data.KlarnaConfigData;
import com.klarna.integration.dto.KlarnaCreatePaymentResponseDTO;


public interface KlarnaPaymentRequestService
{
	KlarnaCreatePaymentResponseDTO createPaymentRequest(final AbstractOrderModel abstractOrder,
			final KlarnaConfigData klarnaConfigData);

}