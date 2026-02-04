package com.klarna.payment.converter.populator;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.CustomerData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.CustomerModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.user.UserService;
import de.hybris.platform.store.services.BaseStoreService;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klarna.api.DefaultMapper;
import com.klarna.api.checkout.model.emd.CustomerAccountInformation;
import com.klarna.api.checkout.model.emd.ExtraMerchantDataBody;
import com.klarna.api.custom.model.PaymentHistoryFull;
import com.klarna.api.payments.model.PaymentsAttachment;
import com.klarna.data.KlarnaConfigData;
import com.klarna.payment.daos.KPOrderDAO;
import com.klarna.payment.facades.KPCustomerFacade;
import com.klarna.payment.facades.KlarnaConfigFacade;


public class KlarnaAttachmentPopulator implements Populator<AbstractOrderModel, PaymentsAttachment>
{

	protected static final Logger LOG = Logger.getLogger(KlarnaAttachmentPopulator.class);

	@Resource(name = "klarnaConfigFacade")
	private KlarnaConfigFacade klarnaConfigFacade;

	@Resource(name = "kpCustomerFacade")
	private KPCustomerFacade kpCustomerFacade;

	@Resource(name = "customerFacade")
	private CustomerFacade customerFacade;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "kpOrderDAO")
	private KPOrderDAO kpOrderDAO;

	@Resource(name = "baseStoreService")
	private BaseStoreService baseStoreService;

	@Override
	public void populate(final AbstractOrderModel source, final PaymentsAttachment target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		if (!kpCustomerFacade.isAnonymousCheckout())
		{
			final KlarnaConfigData klarnaConfig = klarnaConfigFacade.getKlarnaConfig();
			if (klarnaConfig != null && Boolean.TRUE.equals(klarnaConfig.getSendEMD()))
			{
				final CustomerData customerData = customerFacade.getCurrentCustomer();
				final String customerUid = customerData.getUid();
				final CustomerModel customerModel = (CustomerModel) userService.getUserForUID(customerUid);
				final ExtraMerchantDataBody extraMerchantDataBody = new ExtraMerchantDataBody();
				extraMerchantDataBody.setCustomerAccountInfo(getCustomerAccountInfo(customerModel));
				if (Boolean.TRUE.equals(klarnaConfig.getSendOrderHistory()))
				{
					extraMerchantDataBody.setPaymentHistoryFull(getPaymentHistoryFull(customerModel));
				}
				final PaymentsAttachment paymentsAttachment = new PaymentsAttachment();
				paymentsAttachment.setContentType("application/vnd.klarna.internal.emd-v2+json");
				final ObjectMapper om = new DefaultMapper();
				try
				{
					paymentsAttachment.setBody(om.writeValueAsString(extraMerchantDataBody));
				}
				catch (final JsonProcessingException e1)
				{
					LOG.error(e1);
				}
			}
		}
	}

	protected List<CustomerAccountInformation> getCustomerAccountInfo(final CustomerModel customerModel)
	{
		final List<CustomerAccountInformation> custmerInfoList = new ArrayList<>();
		final CustomerAccountInformation customerAccountInformation = new CustomerAccountInformation();
		customerAccountInformation.setUniqueAccountIdentifier(customerModel.getCustomerID());
		customerAccountInformation.setAccountLastModified(
				OffsetDateTime.ofInstant(customerModel.getModifiedtime().toInstant(), ZoneId.systemDefault()));
		customerAccountInformation.setAccountRegistrationDate(
				OffsetDateTime.ofInstant(customerModel.getCreationtime().toInstant(), ZoneId.systemDefault()));
		custmerInfoList.add(customerAccountInformation);
		return custmerInfoList;
	}

	protected List<PaymentHistoryFull> getPaymentHistoryFull(final CustomerModel customerModel)
	{
		final List<PaymentHistoryFull> paymentHistoryList = new ArrayList<>();
		final PaymentHistoryFull paymentHistoryFull = kpOrderDAO.getAggregatePaymentHistory(customerModel,
				baseStoreService.getCurrentBaseStore(), null);
		paymentHistoryList.add(paymentHistoryFull);
		return paymentHistoryList;
	}

}
