package com.klarna.payment.exceptions;

import de.hybris.platform.servicelayer.exceptions.SystemException;


public class MissingMerchantURLException extends SystemException
{
	public MissingMerchantURLException(final String message)
	{
		super(message);
	}

	public MissingMerchantURLException(final Throwable cause)
	{
		super(cause);
	}

	public MissingMerchantURLException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
}
