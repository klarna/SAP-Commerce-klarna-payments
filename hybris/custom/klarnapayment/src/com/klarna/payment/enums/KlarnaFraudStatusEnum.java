package com.klarna.payment.enums;

public enum KlarnaFraudStatusEnum
{

	ACCEPTED("ACCEPTED"), REJECTED("REJECTED"), PENDING("PENDING"), FRAUD_RISK_STOPPED("FRAUD_RISK_STOPPED"), FRAUD_RISK_ACCEPTED(
			"FRAUD_RISK_ACCEPTED"), FRAUD_RISK_REJECTED("FRAUD_RISK_REJECTED");


	private String value;

	private KlarnaFraudStatusEnum(final String value)
	{
		this.value = value.intern();
	}

	public String getValue()
	{
		return value;
	}
}
