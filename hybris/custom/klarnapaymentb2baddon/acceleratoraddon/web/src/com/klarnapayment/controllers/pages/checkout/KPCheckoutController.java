/**
 *
 */
package com.klarnapayment.controllers.pages.checkout;

import java.io.IOException;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.klarna.api.model.ApiException;
import com.klarna.api.order_management.OrderManagementOrdersApi;
import com.klarna.api.order_management.model.OrderManagementOrder;
import com.klarna.payment.enums.KlarnaFraudStatusEnum;
import com.klarna.payment.facades.KPOrderFacade;
import com.klarna.payment.facades.KPPaymentFacade;
import com.klarna.payment.request.FraudNotification;
import com.klarna.payment.util.LogHelper;


/**
 * @author hybris
 *
 */
@Controller
@RequestMapping(value = "/klarna/payment/checkout")
public class KPCheckoutController
{
	@Resource(name = "kpOrderFacade")
	private KPOrderFacade kpOrderFacade;
	@Resource(name = "kpPaymentFacade")
	private KPPaymentFacade kpPaymentFacade;
	protected static final Logger LOG = Logger.getLogger(KPCheckoutController.class);

	@RequestMapping(value = "/pending-update", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> pendingOrder(@RequestBody final FraudNotification fraudNotification)
			throws ApiException, IOException
	{

		final String klarnaOrderID = fraudNotification.getOrder_id();

		LogHelper.debugLog(LOG, "FRAUD NOTIFICATION --------------------------");
		LogHelper.debugLog(LOG, fraudNotification.getOrder_id());
		LogHelper.debugLog(LOG, fraudNotification.getEvent_type());

		if (klarnaOrderID != null && validateFraudStatus(fraudNotification.getOrder_id(), fraudNotification.getEvent_type()))
		{
			kpOrderFacade.updateOrderForPending(klarnaOrderID, fraudNotification.getEvent_type());
			return new ResponseEntity<>("OK", HttpStatus.OK);

		}
		LOG.error("Validation Fails");
		return new ResponseEntity<>("OK", HttpStatus.BAD_REQUEST);
	}

	private boolean validateFraudStatus(final String orderId, final String eventType) throws ApiException, IOException
	{

		final OrderManagementOrdersApi klarnaOrder = kpPaymentFacade.getKlarnaOrderById();
		final OrderManagementOrder klarnaOrderData = klarnaOrder.fetch(orderId);

		LOG.error("Klarna Order Fraud Status " + klarnaOrderData.getFraudStatus());
		boolean validFraudReq = false;
		if (eventType.equalsIgnoreCase(KlarnaFraudStatusEnum.FRAUD_RISK_ACCEPTED.getValue())
				&& klarnaOrderData.getFraudStatus().equalsIgnoreCase(KlarnaFraudStatusEnum.ACCEPTED.getValue()))
		{
			validFraudReq = true;
			LOG.error("Valid Request for event  " + eventType + " and status " + klarnaOrderData.getFraudStatus());
		}
		if ((eventType.equalsIgnoreCase(KlarnaFraudStatusEnum.FRAUD_RISK_REJECTED.getValue()) || eventType
				.equalsIgnoreCase(KlarnaFraudStatusEnum.FRAUD_RISK_STOPPED.getValue()))
				&& klarnaOrderData.getFraudStatus().equalsIgnoreCase(KlarnaFraudStatusEnum.REJECTED.getValue()))
		{
			validFraudReq = true;
			LOG.error("Valid Request for event  " + eventType + " and status " + klarnaOrderData.getFraudStatus());
		}
		return validFraudReq;

	}

}
