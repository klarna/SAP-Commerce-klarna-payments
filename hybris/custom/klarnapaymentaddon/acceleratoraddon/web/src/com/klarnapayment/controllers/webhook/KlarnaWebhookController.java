package com.klarnapayment.controllers.webhook;

import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.klarna.payment.facades.KlarnaWebhookFacade;


@Controller
@RequestMapping(value = "/klarna/webhook")
public class KlarnaWebhookController extends AbstractPageController
{

	@Resource
	private KlarnaWebhookFacade klarnaWebhookFacade;

	@PostMapping
	@ResponseBody
	public ResponseEntity<String> handleWebhook(@RequestBody
	final String requestBody, @RequestHeader(value = "klarna-signature", required = false)
	final String signature)
	{
		boolean isSuccess = klarnaWebhookFacade.processWebhookNotification(requestBody, signature);
		if (isSuccess)
		{
			return new ResponseEntity<>("OK", HttpStatus.OK);
		}
		return new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
