package com.klarna.payment.event;

import java.beans.PropertyChangeEvent;

import org.apache.log4j.Logger;
import org.springframework.context.event.EventListener;

import com.klarna.payment.util.LogHelper;


public class KlarnaEventListener
{
	private static final Logger LOG = Logger.getLogger(KlarnaEventListener.class);

	@EventListener(condition = "#event.propertyName == 'klarnaNetworkSessionToken'")
	public void onNetworkSessionTokenUpdate(final PropertyChangeEvent event)
	{
		// This is a sample event listener
		LogHelper.debugLog(LOG, "Klarna Network Session Token has been updated. Old Value: " + event.getOldValue() + ", New Value: "
				+ event.getNewValue());
	}
}