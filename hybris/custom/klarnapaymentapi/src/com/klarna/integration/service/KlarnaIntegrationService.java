package com.klarna.integration.service;

import com.klarna.integration.dto.KlarnaCreateWebhookRequestDTO;
import com.klarna.integration.dto.KlarnaCreateWebhookResponseDTO;
import com.klarna.integration.dto.KlarnaSigningKeyRequestDTO;
import com.klarna.integration.dto.KlarnaSigningKeyResponseDTO;


public interface KlarnaIntegrationService
{
	KlarnaSigningKeyResponseDTO createSigningKey(final KlarnaSigningKeyRequestDTO requestDTO);

	KlarnaSigningKeyResponseDTO deleteSigningKey(final KlarnaSigningKeyRequestDTO requestDTO);

	KlarnaCreateWebhookResponseDTO createWebhook(final KlarnaCreateWebhookRequestDTO requestDTO);

	KlarnaCreateWebhookResponseDTO updateWebhook(final KlarnaCreateWebhookRequestDTO requestDTO);

	KlarnaCreateWebhookResponseDTO deleteWebhook(final KlarnaCreateWebhookRequestDTO requestDTO);
}
