package com.klarna.payment.daos.impl;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;

import java.util.List;

import com.klarna.payment.daos.KPOrderDAO;


public class DefaultKPOrderDAO implements KPOrderDAO
{

	private FlexibleSearchService flexibleSearchService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<OrderModel> findOderByKlarnaOrderId(final String KlarnaOrderId)
	{
		final String queryString = "SELECT {p:" + OrderModel.PK + "}" + "FROM {" + OrderModel._TYPECODE + " AS p} " + "WHERE "
				+ "{p:" + OrderModel.KPORDERID + "}=?id ";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("id", KlarnaOrderId);

		return flexibleSearchService.<OrderModel> search(query).getResult();
	}

	@Override
	public List<AbstractOrderModel> findOrderByKid(final String kid)
	{
		final String queryString = "SELECT {p:" + AbstractOrderModel.PK + "}" + "FROM {" + AbstractOrderModel._TYPECODE + " AS p} "
				+ "WHERE " + "{p:" + AbstractOrderModel.KPIDENTIFIER + "}=?id ";

		final FlexibleSearchQuery query = new FlexibleSearchQuery(queryString);
		query.addQueryParameter("id", kid);

		return flexibleSearchService.<AbstractOrderModel> search(query).getResult();
	}



	/**
	 * @param flexibleSearchService
	 *           the flexibleSearchService to set
	 */
	public void setFlexibleSearchService(final FlexibleSearchService flexibleSearchService)
	{
		this.flexibleSearchService = flexibleSearchService;
	}

}
