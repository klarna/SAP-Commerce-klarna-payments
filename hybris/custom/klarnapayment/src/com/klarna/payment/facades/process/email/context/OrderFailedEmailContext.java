/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2020 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.klarna.payment.facades.process.email.context;

import de.hybris.platform.acceleratorservices.model.cms2.pages.EmailPageModel;
import de.hybris.platform.acceleratorservices.process.email.context.AbstractEmailContext;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.commerceservices.enums.CustomerType;
import de.hybris.platform.commerceservices.model.process.StoreFrontCustomerProcessModel;
import de.hybris.platform.core.model.c2l.LanguageModel;
import de.hybris.platform.core.model.user.CustomerModel;

import java.util.Calendar;
import java.util.Date;

import com.klarna.payment.model.OrderFailedEmailProcessModel;


/**
 *
 */
public class OrderFailedEmailContext extends AbstractEmailContext<StoreFrontCustomerProcessModel>
{
	String klarnaOrderId;
	String countryName;
	Date currDate;
	String cartId;
	String errorMessage;
	String guest;

	@Override
	public void init(final StoreFrontCustomerProcessModel orderFailedEmailProcessModel, final EmailPageModel emailPageModel)
	{

		super.init(orderFailedEmailProcessModel, emailPageModel);
		klarnaOrderId = ((OrderFailedEmailProcessModel) orderFailedEmailProcessModel).getKpOrderId();
		countryName = ((OrderFailedEmailProcessModel) orderFailedEmailProcessModel).getCountryName();
		final Calendar cal = Calendar.getInstance();
		currDate = cal.getTime();
		final CustomerModel customer = getCustomer(orderFailedEmailProcessModel);
		if (CustomerType.GUEST.equals(customer.getType()))
		{
			guest = "Anonymous User";
		}
		else
		{
			guest = "Registered User";
		}
		guest = guest + " / " + customer.getCustomerID();
		cartId = ((OrderFailedEmailProcessModel) orderFailedEmailProcessModel).getCartId();
		errorMessage = ((OrderFailedEmailProcessModel) orderFailedEmailProcessModel).getKperrorMessage();
		if (((OrderFailedEmailProcessModel) orderFailedEmailProcessModel).getStore() != null
				&& ((OrderFailedEmailProcessModel) orderFailedEmailProcessModel).getStore().getConfig() != null
				&& ((OrderFailedEmailProcessModel) orderFailedEmailProcessModel).getStore().getConfig().getKpConfig() != null)
		{
			put(EMAIL, ((OrderFailedEmailProcessModel) orderFailedEmailProcessModel).getStore().getConfig().getKpConfig()
					.getMerchantEmail());
		}

	}

	@Override
	protected BaseSiteModel getSite(final StoreFrontCustomerProcessModel businessProcessModel)
	{
		// YTODO Auto-generated method stub
		return businessProcessModel.getSite();
	}

	@Override
	protected CustomerModel getCustomer(final StoreFrontCustomerProcessModel businessProcessModel)
	{
		// YTODO Auto-generated method stub
		return businessProcessModel.getCustomer();
	}

	@Override
	protected LanguageModel getEmailLanguage(final StoreFrontCustomerProcessModel businessProcessModel)
	{
		// YTODO Auto-generated method stub
		return businessProcessModel.getLanguage();
	}


	/**
	 * @return the klarnaOrderId
	 */
	public String getKlarnaOrderId()
	{
		return klarnaOrderId;
	}

	/**
	 * @param klarnaOrderId
	 *           the klarnaOrderId to set
	 */
	public void setKlarnaOrderId(final String klarnaOrderId)
	{
		this.klarnaOrderId = klarnaOrderId;
	}


	/**
	 * @return the currDate
	 */
	public Date getCurrDate()
	{
		return currDate;
	}

	/**
	 * @param currDate
	 *           the currDate to set
	 */
	public void setCurrDate(final Date currDate)
	{
		this.currDate = currDate;
	}

	/**
	 * @return the guest
	 */
	public String getGuest()
	{
		return guest;
	}

	/**
	 * @param guest
	 *           the guest to set
	 */
	public void setGuest(final String guest)
	{
		this.guest = guest;
	}

	/**
	 * @return the countryName
	 */
	public String getCountryName()
	{
		return countryName;
	}

	/**
	 * @param countryName
	 *           the countryName to set
	 */
	public void setCountryName(final String countryName)
	{
		this.countryName = countryName;
	}

	/**
	 * @return the cartId
	 */
	public String getCartId()
	{
		return cartId;
	}

	/**
	 * @param cartId
	 *           the cartId to set
	 */
	public void setCartId(final String cartId)
	{
		this.cartId = cartId;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage()
	{
		return errorMessage;
	}

	/**
	 * @param errorMessage
	 *           the errorMessage to set
	 */
	public void setErrorMessage(final String errorMessage)
	{
		this.errorMessage = errorMessage;
	}

}
