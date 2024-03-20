package com.klarna.payment.services;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;


public interface KPOrderService
{
	/**
	 * Gets the order for Klarna Order ID.
	 *
	 * @param klarnaOrderId
	 * @return OrderModel
	 */
	OrderModel getOderForKlarnaOrderId(String klarnaOrderId);

	/**
	 * Get abstract order model from Klarna order ID.
	 *
	 * @param klarnaOrderId
	 * @return AbstractOrderModel object
	 */
	AbstractOrderModel getOrderForKId(final String kid);

}
