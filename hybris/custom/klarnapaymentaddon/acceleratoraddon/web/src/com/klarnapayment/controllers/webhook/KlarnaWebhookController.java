package com.klarnapayment.controllers.webhook;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;

import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
	final byte[] requestBody, final HttpServletRequest request)
	{
		LogHelper.debugLog(LOG, "Webhook processing started.");
		try
		{
			boolean isValidRequest = klarnaWebhookFacade.validateWebhookRequest(requestBody, headers.get("Klarna-Signature"));
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



	@RequestMapping(value = "/check-payment-status", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> checkPaymentStatus(@RequestBody
	final String paymentRequestId, final HttpServletRequest request, final HttpServletResponse response)
	{
		try
		{
			if (StringUtils.isEmpty(paymentRequestId))
			{
				return ResponseEntity.badRequest().body("Payment request id is missing");
			}
		}
		catch (Exception e)
		{
			LOG.error("Exception occured during shipping address update :: ", e);
		}
		return ResponseEntity.internalServerError().body("Error");
	}


}
