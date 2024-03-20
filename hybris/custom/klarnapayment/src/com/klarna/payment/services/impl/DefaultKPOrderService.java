package com.klarna.payment.services.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.exceptions.AmbiguousIdentifierException;

import java.util.List;

import org.apache.log4j.Logger;

import com.klarna.payment.daos.KPOrderDAO;
import com.klarna.payment.services.KPOrderService;


public class DefaultKPOrderService implements KPOrderService
{
	private static final Logger LOG = Logger.getLogger(DefaultKlarnapaymentService.class);

	private KPOrderDAO kpOrderDAO;

	private static final String KLARNA_ORDER_NOT_UNIQUE = "Klarna Order Id is not unique : ";
	private static final String ORDERS_FOUND = ". Orders Found - ";

	/**
	 * @return the kpOrderDAO
	 */
	public KPOrderDAO getKpOrderDAO()
	{
		return kpOrderDAO;
	}

	/**
	 * @param kpOrderDAO
	 *           the kpOrderDAO to set
	 */
	public void setKpOrderDAO(final KPOrderDAO kpOrderDAO)
	{
		this.kpOrderDAO = kpOrderDAO;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrderModel getOderForKlarnaOrderId(final String KlarnaOrderId)
	{
		final List<OrderModel> result = kpOrderDAO.findOderByKlarnaOrderId(KlarnaOrderId);
		if (result.isEmpty())
		{
			return null;
		}
		if (result.size() > 1)
		{
			LOG.error(KLARNA_ORDER_NOT_UNIQUE + KlarnaOrderId + ORDERS_FOUND + result.size());
			throw new AmbiguousIdentifierException("Klarna Order Id '" + KlarnaOrderId + "' is not unique, " + result.size()
					+ " orders found!");
		}
		return result.get(0);
	}

	@Override
	public AbstractOrderModel getOrderForKId(final String kid)
	{
		final List<AbstractOrderModel> result = kpOrderDAO.findOrderByKid(kid);
		if (result.isEmpty())
		{
			return null;
		}
		if (result.size() > 1)
		{
			LOG.error(KLARNA_ORDER_NOT_UNIQUE + kid + ORDERS_FOUND + result.size());
			throw new AmbiguousIdentifierException(KLARNA_ORDER_NOT_UNIQUE + kid + ORDERS_FOUND + result.size());
		}
		return result.get(0);
	}




}
