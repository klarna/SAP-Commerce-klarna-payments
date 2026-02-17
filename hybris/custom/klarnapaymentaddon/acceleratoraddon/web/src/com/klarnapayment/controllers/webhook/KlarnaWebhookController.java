package com.klarnapayment.controllers.webhook;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.klarna.payment.facades.KlarnaWebhookFacade;
import com.klarna.payment.util.LogHelper;
import com.microsoft.sqlserver.jdbc.StringUtils;


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
	final Map<String, String> headers, @RequestBody
	final String requestBody, final HttpServletRequest request)
	{
		LogHelper.debugLog(LOG, "Webhook processing started.");
		try
		{
			String signature = headers.get("klarna-signature");
			if (StringUtils.isEmpty(signature))
			{
				LOG.error("Webhook cannot be processed. Signature is not available.");
				return ResponseEntity.badRequest().body("Signature is missing");
			}
			if (StringUtils.isEmpty(requestBody))
			{
				LOG.error("Webhook cannot be processed. Request body is empty.");
				return ResponseEntity.badRequest().body("Request body is empty");
			}
			boolean isSuccess = klarnaWebhookFacade.processWebhookNotification(requestBody, signature);
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
