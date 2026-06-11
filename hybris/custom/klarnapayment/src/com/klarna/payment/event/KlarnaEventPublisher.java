package com.klarna.payment.event;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import com.klarna.payment.util.LogHelper;


public class KlarnaEventPublisher
{
	protected static final Logger LOG = Logger.getLogger(KlarnaEventPublisher.class);

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	public void publishProperyChangeEvent(final String propertyName, final Object oldValue, final Object newValue)
	{
		LogHelper.debugLog(LOG, "Publishing PropertyChangeEvent for the property :: " + propertyName);
		try
		{
			applicationEventPublisher.publishEvent(new java.beans.PropertyChangeEvent(this, propertyName, oldValue, newValue));
		}
		catch (final Exception e)
		{
			LOG.error("Proprty Change Event failed due to error :: ", e);
		}
	}

}
