package com.klarna.payment.daos;

import de.hybris.platform.payment.model.PaymentTransactionModel;

import com.klarna.payment.model.KPPaymentInfoModel;


public interface KPPaymentInfoDAO
{
	/**
	 * find Klarna Payment Info Model by code
	 *
	 * @param code
	 * @return KPPaymentInfoModel
	 */
	KPPaymentInfoModel findKpPaymentInfo(String code);
	PaymentTransactionModel findPaymentTransaction(String code);
}