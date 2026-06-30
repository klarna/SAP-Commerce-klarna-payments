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
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.data.KlarnaConfigData;
import com.klarna.payment.facades.KPCustomerFacade;
import com.klarna.payment.facades.KlarnaConfigFacade;


public class KPCreditSessionAttPopulator implements Populator<AbstractOrderModel, PaymentsSession>
{

	protected static final Logger LOG = Logger.getLogger(KPCreditSessionAttPopulator.class);

	private KlarnaConfigFacade klarnaConfigFacade;

	private KPCreditSessionPopulator kpCreditSessionPopulator;


	/**
	 * @return the klarnaConfigFacade
	 */
	public KlarnaConfigFacade getKlarnaConfigFacade()
	{
		return klarnaConfigFacade;
	}

	/**
	 * @param klarnaConfigFacade
	 *           the klarnaConfigFacade to set
	 */
	public void setKlarnaConfigFacade(final KlarnaConfigFacade klarnaConfigFacade)
	{
		this.klarnaConfigFacade = klarnaConfigFacade;
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

	@Override
	public void populate(final AbstractOrderModel source, final PaymentsSession target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		final KlarnaConfigData klarnaConfig = klarnaConfigFacade.getKlarnaConfig();
		kpCreditSessionPopulator.populate(source, target);

	}

}
