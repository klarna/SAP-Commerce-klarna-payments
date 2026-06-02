package com.klarnapayment.listeners;

import java.beans.PropertyChangeEvent;

import org.apache.log4j.Logger;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.klarna.payment.util.LogHelper;


@Component
public class KlarnaEventListener
{
	private static final Logger LOG = Logger.getLogger(KlarnaEventListener.class);

	@EventListener(condition = "#evt.propertyName == 'klarnaNetworkSessionToken'")
	public void onNetworkSessionTokenUpdate(final PropertyChangeEvent event)
	{
		// This is a sample event listener
		LogHelper.debugLog(LOG, "Klarna Network Session Token has been updated. Old Value: " + event.getOldValue() + ", New Value: "
				+ event.getNewValue());
	}
}