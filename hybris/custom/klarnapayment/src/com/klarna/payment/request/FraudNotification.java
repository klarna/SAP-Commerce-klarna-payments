package com.klarna.payment.request;

public class FraudNotification
{
	String order_id;
	String event_type;

	/**
	 * @return the order_id
	 */
	public String getOrder_id()
	{
		return order_id;
	}

	/**
	 * @param order_id
	 *           the order_id to set
	 */
	public void setOrder_id(final String order_id)
	{
		this.order_id = order_id;
	}

	/**
	 * @return the event_type
	 */
	public String getEvent_type()
	{
		return event_type;
	}

	/**
	 * @param event_type
	 *           the event_type to set
	 */
	public void setEvent_type(final String event_type)
	{
		this.event_type = event_type;
	}

}
