package com.klarna.payment.daos.impl;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.servicelayer.search.FlexibleSearchQuery;
import de.hybris.platform.servicelayer.search.FlexibleSearchService;
import de.hybris.platform.servicelayer.search.SearchResult;
import de.hybris.platform.store.BaseStoreModel;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.collections.CollectionUtils;

import com.klarna.api.custom.model.PaymentHistoryFull;
import com.klarna.payment.daos.KPOrderDAO;
import com.klarna.payment.util.KlarnaConversionUtils;


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

	@Override
	public PaymentHistoryFull getAggregatePaymentHistory(final UserModel user, final BaseStoreModel baseStore, @Nullable
	final List<OrderStatus> orderStatuses)
	{
		final StringBuilder query = new StringBuilder(
				"SELECT " + " COUNT({o.pk}) AS orderCount, " + " SUM({o.totalPriceWithTax}) AS totalAmount, "
						+ " MAX({o.date}) AS lastOrderDate " + "FROM {Order AS o} " + "WHERE {o.user} = ?user AND {o.store} = ?store ");

		final Map<String, Object> params = new HashMap<>();
		params.put("user", user);
		params.put("store", baseStore);

		if (CollectionUtils.isNotEmpty(orderStatuses))
		{
			query.append("AND {o.status} IN (?statuses) ");
			params.put("statuses", orderStatuses);
		}

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(query.toString(), params);
		final SearchResult<Object> result = flexibleSearchService.search(fsq);

		final PaymentHistoryFull paymentHistoryFull = new PaymentHistoryFull();

		if (result.getResult() != null)
		{
			paymentHistoryFull.setNumberPaidPurchases((Integer) result.getResult().get(0));
			paymentHistoryFull
					.setTotalAmountPaidPurchases(KlarnaConversionUtils.getKlarnaLongValue((BigDecimal) result.getResult().get(1)));
			final Date lastOrderDate = (Date) result.getResult().get(2);
			paymentHistoryFull
					.setDateOfLastPaidPurchase(OffsetDateTime.ofInstant(lastOrderDate.toInstant(), ZoneId.systemDefault()));
		}
		else
		{
			paymentHistoryFull.setNumberPaidPurchases(0);
			paymentHistoryFull.setTotalAmountPaidPurchases(0L);
		}

		return paymentHistoryFull;
	}

}
