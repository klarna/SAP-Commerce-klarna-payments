package com.klarna.payment.util;

import java.math.BigDecimal;


public final class KlarnaConversionUtils
{
	private static final int FACTOR = 100;

	private KlarnaConversionUtils()
	{
	}

	public static Long getKlarnaLongValue(final BigDecimal source)
	{
		return Long.valueOf(source.multiply(new BigDecimal(FACTOR)).longValue());
	}

	public static Long getKlarnaIntValue(final Double source)
	{
		final long calculateValue = Math.round(source.doubleValue() * FACTOR);
		return Long.valueOf((int) calculateValue);
	}

	public static Long getKlarnaLongValue(final Double source)
	{
		final long calculateValue = Math.round(source.doubleValue() * FACTOR);
		return Long.valueOf(calculateValue);
	}

	public static boolean isNumeric(final String str)
	{
		try
		{
			Double.parseDouble(str);
		}
		catch (final NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

	public static Double getKlarnaDoubleValue(final Long source)
	{
		return Double.valueOf(source.doubleValue() / FACTOR);
	}

}
