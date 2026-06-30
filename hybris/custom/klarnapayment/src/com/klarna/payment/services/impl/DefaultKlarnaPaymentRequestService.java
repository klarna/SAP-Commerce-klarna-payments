package com.klarna.payment.services.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.klarna.data.KlarnaConfigData;
import com.klarna.integration.dto.KlarnaCreatePaymentRequestDTO;
import com.klarna.integration.dto.KlarnaCreatePaymentResponseDTO;
import com.klarna.integration.dto.KlarnaPaymentRequestPayloadDTO;
import com.klarna.integration.enums.TransactionTypeEnum;
import com.klarna.integration.service.KlarnaIntegrationService;
import com.klarna.payment.services.KlarnaPaymentRequestService;
import com.klarna.payment.util.KlarnaServicesUtil;
import com.klarna.payment.util.LogHelper;


public class DefaultKlarnaPaymentRequestService implements KlarnaPaymentRequestService
{

	private static final Logger LOG = Logger.getLogger(DefaultKlarnaPaymentRequestService.class);


	@Resource
	private KlarnaIntegrationService klarnaIntegrationService;

	@Resource
	private KlarnaServicesUtil klarnaServicesUtil;

	@Resource
	private Converter<AbstractOrderModel, KlarnaPaymentRequestPayloadDTO> klarnaPaymentRequestPayloadConverter;

	@Override
	public KlarnaCreatePaymentResponseDTO createPaymentRequest(final AbstractOrderModel abstractOrder,
			final KlarnaConfigData klarnaConfigData)
	{
		LogHelper.debugLog(LOG, "Inside createPaymentRequest method");
		final KlarnaCreatePaymentRequestDTO requestDTO = new KlarnaCreatePaymentRequestDTO();
		requestDTO.setConfig(klarnaConfigData);
		requestDTO.setType(TransactionTypeEnum.CREATE_PAYMENT_REQUEST);
		requestDTO.setMetaData(klarnaServicesUtil.getKlarnaMetaData());
		requestDTO.setPaymentRequestPayload(klarnaPaymentRequestPayloadConverter.convert(abstractOrder));
		return klarnaIntegrationService.createPaymentRequest(requestDTO);
	}

}
