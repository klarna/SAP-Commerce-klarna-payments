package com.klarna.integration.service;

import com.klarna.integration.dto.KlarnaSigningKeyRequestDTO;
import com.klarna.integration.dto.KlarnaSigningKeyResponseDTO;
import com.klarna.integration.dto.KlarnaWebhookRequestDTO;
import com.klarna.integration.dto.KlarnaWebhookResponseDTO;


public interface KlarnaIntegrationService
{
	KlarnaSigningKeyResponseDTO createSigningKey(final KlarnaSigningKeyRequestDTO requestDTO);

	KlarnaSigningKeyResponseDTO deleteSigningKey(final KlarnaSigningKeyRequestDTO requestDTO);

	KlarnaWebhookResponseDTO createWebhook(final KlarnaWebhookRequestDTO requestDTO);

	KlarnaWebhookResponseDTO updateWebhook(final KlarnaWebhookRequestDTO requestDTO);

	KlarnaWebhookResponseDTO deleteWebhook(final KlarnaWebhookRequestDTO requestDTO);
}
