package com.klarnapayment.controllers.webhook;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.klarna.payment.facades.KlarnaWebhookFacade;
import com.klarna.payment.util.LogHelper;


@Controller
@RequestMapping(value = "/klarna/webhook")
public class KlarnaWebhookController extends AbstractPageController
{

	private static final Logger LOG = Logger.getLogger(KlarnaWebhookController.class);

	@Resource
	private KlarnaWebhookFacade klarnaWebhookFacade;

	@PostMapping
	@ResponseBody
	public ResponseEntity<String> handleWebhook(@RequestHeader
	final Map<String, String> headers, final HttpServletRequest request)
	{
		LogHelper.debugLog(LOG, "Webhook processing started.");
		try
		{
			final byte[] requestBody = StreamUtils.copyToByteArray(request.getInputStream());
			boolean isValidRequest = klarnaWebhookFacade.validateWebhookRequest(requestBody, headers.get("klarna-signature"));
			if (!isValidRequest)
			{
				return ResponseEntity.badRequest().body("Signature Validation Failed");
			}
			boolean isSuccess = klarnaWebhookFacade.processWebhookNotification(requestBody);
			if (isSuccess)
			{
				LogHelper.debugLog(LOG, "Webhook processed successfully.");
				return ResponseEntity.ok("OK");
			}
		}
		catch (Exception e)
		{
			LOG.error("Webhook processing failed due to Exception :: ", e);
		}
		return ResponseEntity.internalServerError().body("Webhook processing failed");
	}

}
