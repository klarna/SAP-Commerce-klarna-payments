package com.klarna.payment.converter.populator;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klarna.api.DefaultMapper;
import com.klarna.api.checkout.model.emd.CustomerAccountInformation;
import com.klarna.api.checkout.model.emd.ExtraMerchantDataBody;
import com.klarna.api.payments.model.PaymentsAttachment;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.data.KlarnaConfigData;
import com.klarna.payment.facades.KPConfigFacade;
import com.klarna.payment.facades.KPCustomerFacade;
import com.klarna.payment.util.LogHelper;


public class KPCreditSessionAttPopulator implements Populator<AbstractOrderModel, PaymentsSession>
{

	protected static final Logger LOG = Logger.getLogger(KPCreditSessionAttPopulator.class);


	private KPConfigFacade kpConfigFacade;


	private KPCreditSessionPopulator kpCreditSessionPopulator;
	private KPCustomerFacade kpCustomerFacade;
	private CustomerFacade customerFacade;
	private UserService userService;


	/**
	 * @return the kpConfigFacade
	 */
	public KPConfigFacade getKpConfigFacade()
	{
		return kpConfigFacade;
	}

	/**
	 * @param kpConfigFacade
	 *           the kpConfigFacade to set
	 */
	public void setKpConfigFacade(final KPConfigFacade kpConfigFacade)
	{
		this.kpConfigFacade = kpConfigFacade;
	}

	/**
	 * @return the kpCreditSessionPopulator
	 */
	public KPCreditSessionPopulator getKpCreditSessionPopulator()
	{
		return kpCreditSessionPopulator;
	}

	/**
	 * @param kpCreditSessionPopulator
	 *           the kpCreditSessionPopulator to set
	 */
	public void setKpCreditSessionPopulator(final KPCreditSessionPopulator kpCreditSessionPopulator)
	{
		this.kpCreditSessionPopulator = kpCreditSessionPopulator;
	}

	/**
	 * @return the kpCustomerFacade
	 */
	public KPCustomerFacade getKpCustomerFacade()
	{
		return kpCustomerFacade;
	}

	/**
	 * @param kpCustomerFacade
	 *           the kpCustomerFacade to set
	 */
	public void setKpCustomerFacade(final KPCustomerFacade kpCustomerFacade)
	{
		this.kpCustomerFacade = kpCustomerFacade;
	}

	/**
	 * @return the customerFacade
	 */
	public CustomerFacade getCustomerFacade()
	{
		return customerFacade;
	}

	/**
	 * @param customerFacade
	 *           the customerFacade to set
	 */
	public void setCustomerFacade(final CustomerFacade customerFacade)
	{
		this.customerFacade = customerFacade;
	}

	/**
	 * @return the userService
	 */
	public UserService getUserService()
	{
		return userService;
	}

	/**
	 * @param userService
	 *           the userService to set
	 */
	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	@Override
	public void populate(final AbstractOrderModel source, final PaymentsSession target) throws ConversionException
	{
		LogHelper.debugLog(LOG, "inside full populator");
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		final KlarnaConfigData klarnaConfig = kpConfigFacade.getKlarnaConfig();
		kpCreditSessionPopulator.populate(source, target);
		addAttachment(target, klarnaConfig);

	}

	private void addAttachment(final PaymentsSession target, final KlarnaConfigData klarnaConfig)
	{
		//if (klarnaConfig.getAttachementRequired() != null && klarnaConfig.getAttachementRequired().booleanValue())
		//{
		LogHelper.debugLog(LOG, "inside attachemnt creation");
		final CustomerAccountInformation customerAccountInformation = new CustomerAccountInformation();

		if (!kpCustomerFacade.isAnonymousCheckout())

		{
			final List<CustomerAccountInformation> custmerInfoList = new ArrayList();
			final CustomerData customerData = customerFacade.getCurrentCustomer();
			final String customerUid = customerData.getUid();
			final CustomerModel customerModel = (CustomerModel) userService.getUserForUID(customerUid);

			customerAccountInformation.setUniqueAccountIdentifier(customerModel.getCustomerID());

			customerAccountInformation.setAccountLastModified(
					OffsetDateTime.ofInstant(customerModel.getModifiedtime().toInstant(), ZoneId.systemDefault()));
			customerAccountInformation.setAccountRegistrationDate(
					OffsetDateTime.ofInstant(customerModel.getCreationtime().toInstant(), ZoneId.systemDefault()));
			custmerInfoList.add(customerAccountInformation);
			final ExtraMerchantDataBody extraMerchantDataBody = new ExtraMerchantDataBody();
			extraMerchantDataBody.setCustomerAccountInfo(custmerInfoList);
			final PaymentsAttachment paymentsAttachment = new PaymentsAttachment();
			paymentsAttachment.setContentType("application/vnd.klarna.internal.emd-v2+json");
			final ObjectMapper om = new DefaultMapper();
			try
			{
				paymentsAttachment.setBody(om.writeValueAsString(extraMerchantDataBody));
			}
			catch (final JsonProcessingException e1)
			{
				// YTODO Auto-generated catch block
				LOG.error(e1);
			}
			target.setAttachment(paymentsAttachment);
		}
		//}

	}


}
