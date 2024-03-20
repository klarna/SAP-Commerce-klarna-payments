package com.klarna.payment.services;

import de.hybris.platform.payment.model.PaymentTransactionModel;

import com.klarna.payment.model.KPPaymentInfoModel;


public interface KPPaymentInfoService
{
	KPPaymentInfoModel getKpPaymentInfo(String code);

	PaymentTransactionModel findKpPaymentTransaction(String code);
}
