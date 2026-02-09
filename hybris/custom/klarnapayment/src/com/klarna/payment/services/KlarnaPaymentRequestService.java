package com.klarna.payment.services;

import de.hybris.platform.core.model.order.AbstractOrderModel;

import com.klarna.data.KlarnaConfigData;
import com.klarna.integration.dto.KlarnaCreatePaymentResponseDTO;
import com.klarna.integration.dto.KlarnaInteroperabilityDataDTO;


public interface KlarnaPaymentRequestService
{
	KlarnaCreatePaymentResponseDTO createPaymentRequest(final AbstractOrderModel abstractOrder,
			final KlarnaConfigData klarnaConfigData);

	KlarnaInteroperabilityDataDTO createKlarnaInteroperabilityData(final AbstractOrderModel abstractOrder);
}