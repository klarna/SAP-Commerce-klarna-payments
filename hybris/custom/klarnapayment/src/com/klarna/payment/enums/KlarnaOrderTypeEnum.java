package com.klarna.payment.enums;

/**
 * Created by taketoshi on 10/30/15.
 */
public enum KlarnaOrderTypeEnum
{
	PHYSICAL("physical"), SALES_TAX("sales_tax"), DISCOUNT("discount"), STORE_CREDIT("store_credit"), SHIPPING_FEE(
			"shipping_fee"), GIFT_CARD("gift_card"), DIGITAL("digital");

	private String value;

	private KlarnaOrderTypeEnum(final String value)
	{
		this.value = value.intern();
	}

	public String getValue()
	{
		return value;
	}
}
