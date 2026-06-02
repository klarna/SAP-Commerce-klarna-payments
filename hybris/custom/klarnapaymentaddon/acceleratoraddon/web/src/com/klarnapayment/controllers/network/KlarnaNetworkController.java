/**
 *
 */
package com.klarnapayment.controllers.network;

import de.hybris.platform.servicelayer.session.SessionService;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.klarna.payment.util.LogHelper;
import com.klarnapayment.constants.KlarnapaymentaddonWebConstants;


/**
 *
 */
@Controller
@RequestMapping(value = "/klarna/network")
public class KlarnaNetworkController
{
	private static final Logger LOG = Logger.getLogger(KlarnaNetworkController.class);

	@Resource(name = "sessionService")
	private SessionService sessionService;

	@RequestMapping(value = "/update-session", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Map<String, Object>> updateSession(@RequestBody
	final
	Map<String, String> requestMap)
	{
		Map<String, Object> responseBody = new HashMap<>();
		try
		{
			if (MapUtils.isNotEmpty(requestMap)
					&& StringUtils.isNotEmpty(requestMap.get(KlarnapaymentaddonWebConstants.KLARNA_NETWORK_SESSION_TOKEN)))
			{
				sessionService.setAttribute(KlarnapaymentaddonWebConstants.KLARNA_NETWORK_SESSION_TOKEN,
						requestMap.get(KlarnapaymentaddonWebConstants.KLARNA_NETWORK_SESSION_TOKEN));
				LogHelper.debugLog(LOG, "Stored Klarna Network Session Token:: "
						+ requestMap.get(KlarnapaymentaddonWebConstants.KLARNA_NETWORK_SESSION_TOKEN));
				responseBody.put(KlarnapaymentaddonWebConstants.KLARNA_RESPONSE_STATUS,
						KlarnapaymentaddonWebConstants.KLARNA_RESPONSE_STATUS_SUCCESS);
				return ResponseEntity.ok().body(responseBody);
			}
			else
			{
				LOG.error("Invalid request! Klarna Network Session Token is missing.");
			}
		}
		catch (Exception e)
		{
			LOG.error("Exception occured while storing Klarna Network Session Tokene :: ", e);
		}
		responseBody.put(KlarnapaymentaddonWebConstants.KLARNA_RESPONSE_STATUS,
				KlarnapaymentaddonWebConstants.KLARNA_RESPONSE_STATUS_ERROR);
		return ResponseEntity.ok().body(responseBody);
	}
}
