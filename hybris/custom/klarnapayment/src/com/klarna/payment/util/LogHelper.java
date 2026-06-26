/**
 *
 */
package com.klarna.payment.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LogHelper
{
	public static void debugLog(final Logger LOG, final String message)
	{
		if (LOG.isDebugEnabled())
		{
			LOG.debug(message);
		}
	}
}
