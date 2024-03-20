/**
 *
 */
package com.klarna.payment.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author hybris
 *
 */
public class KlarnaDateFormatterUtil
{
	public static final String DATE_FORMAT_YEAR_PATTERN = "yyyy-MM-dd";
	public static final String DATE_FORMAT_DATE_PATTERN = "dd-MM-yyyy";
	public static final String DATE_FORMAT_MONTH_PATTERN = "MM-dd-yyyy";

	public static String getFormattedDateString(final Date date, final String dateFormat)
	{
		final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		return sdf.format(date);
	}

	public static Date getFormattedDate(final String date, final String dateFormat)
	{
		final SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		try
		{
			return sdf.parse(date);
		}
		catch (final ParseException e)
		{
			e.printStackTrace();
			return null;
		}
	}

}
