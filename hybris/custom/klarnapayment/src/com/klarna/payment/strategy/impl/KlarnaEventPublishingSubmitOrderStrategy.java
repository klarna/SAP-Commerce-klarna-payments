/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.klarna.payment.strategy.impl;

import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.order.events.SubmitOrderEvent;
import de.hybris.platform.order.strategies.SubmitOrderStrategy;
import de.hybris.platform.servicelayer.event.EventService;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.klarna.payment.enums.KlarnaFraudStatusEnum;
import com.klarna.payment.util.LogHelper;


/**
 * This implementation sends {@link SubmitOrderEvent} event when order is submitted.
 */
public class KlarnaEventPublishingSubmitOrderStrategy implements SubmitOrderStrategy
{
	protected static final Logger LOG = Logger.getLogger(KlarnaEventPublishingSubmitOrderStrategy.class);
	private EventService eventService;

	@Override
	public void submitOrder(final OrderModel order)
	{
		if (order.getKpFraudStatus() == null || order.getKpFraudStatus().equals(KlarnaFraudStatusEnum.ACCEPTED.getValue())
				|| order.getKpFraudStatus().equals(KlarnaFraudStatusEnum.FRAUD_RISK_ACCEPTED.getValue()))
		{
			eventService.publishEvent(new SubmitOrderEvent(order));
			LogHelper.debugLog(LOG, "Submit Order Publsich Klarna");
		}
	}

	@Required
	public void setEventService(final EventService eventService)
	{
		this.eventService = eventService;
	}

}
