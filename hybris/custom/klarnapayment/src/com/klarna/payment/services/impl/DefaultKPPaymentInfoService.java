package com.klarna.payment.services.impl;

import de.hybris.platform.payment.model.PaymentTransactionModel;

import com.klarna.payment.daos.KPPaymentInfoDAO;
import com.klarna.payment.model.KPPaymentInfoModel;
import com.klarna.payment.services.KPPaymentInfoService;


public class DefaultKPPaymentInfoService implements KPPaymentInfoService
{
	private KPPaymentInfoDAO kpPaymentInfoDAO;

	@Override
	public KPPaymentInfoModel getKpPaymentInfo(final String code)
	{
		return kpPaymentInfoDAO.findKpPaymentInfo(code);
	}

	/**
	 * @return the kpPaymentInfoDAO
	 */
	public KPPaymentInfoDAO getKpPaymentInfoDAO()
	{
		return kpPaymentInfoDAO;
	}

	/**
	 * @param kpPaymentInfoDAO
	 *           the kpPaymentInfoDAO to set
	 */
	public void setKpPaymentInfoDAO(final KPPaymentInfoDAO kpPaymentInfoDAO)
	{
		this.kpPaymentInfoDAO = kpPaymentInfoDAO;
	}

	@Override
	public PaymentTransactionModel findKpPaymentTransaction(String code)
	{
		return kpPaymentInfoDAO.findPaymentTransaction(code);
	}




}
