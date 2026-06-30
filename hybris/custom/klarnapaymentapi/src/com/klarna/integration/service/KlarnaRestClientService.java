package com.klarna.integration.service;

import com.klarna.integration.dto.KlarnaAPIRequestDTO;
import com.klarna.integration.dto.KlarnaAPIResponseDTO;


public interface KlarnaRestClientService
{
	KlarnaAPIResponseDTO callRestApi(final KlarnaAPIRequestDTO requestDTO);
}
