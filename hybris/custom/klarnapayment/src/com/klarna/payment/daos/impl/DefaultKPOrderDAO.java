package com.klarna.payment.daos.impl;

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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.klarna.api.custom.model.PaymentHistoryFull;
import com.klarna.payment.daos.KPOrderDAO;
import com.klarna.payment.util.KlarnaConversionUtils;


public class DefaultKPOrderDAO implements KPOrderDAO
{

	private static final Logger LOG = LoggerFactory.getLogger(DefaultKPOrderDAO.class);

	private static final String ORDER_HISTORY_QUERY = "SELECT COUNT({o:" + OrderModel.PK + "}) AS orderCount, " + " SUM({o:"
			+ OrderModel.TOTALPRICE + "} + {o:" + OrderModel.TOTALTAX + "} + COALESCE({o:" + OrderModel.DELIVERYCOST
			+ "},0) + COALESCE({o:" + OrderModel.PAYMENTCOST + "},0))  AS totalAmount, " + " MAX({o:" + OrderModel.DATE
			+ "}) AS lastOrderDate, " + "FROM {" + OrderModel._TYPECODE + " AS o} " + "WHERE {o:" + OrderModel.USER
			+ "} = ?user AND {o:" + OrderModel.STORE + "} = ?store ";

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
	public PaymentHistoryFull getAggregatePaymentHistory(final UserModel user, final BaseStoreModel baseStore)
	{

		final FlexibleSearchQuery fsq = new FlexibleSearchQuery(ORDER_HISTORY_QUERY);
		fsq.addQueryParameter("user", user);
		fsq.addQueryParameter("store", baseStore);
		fsq.setResultClassList(Arrays.asList(Integer.class, BigDecimal.class, Date.class));
		final SearchResult<List<Object>> result = flexibleSearchService.search(fsq);

		final PaymentHistoryFull paymentHistoryFull = new PaymentHistoryFull();

		if (result != null && result.getCount() > 0)
		{
			paymentHistoryFull.setNumberPaidPurchases((Integer) result.getResult().get(0).get(0));
			paymentHistoryFull
					.setTotalAmountPaidPurchases(
							KlarnaConversionUtils.getKlarnaLongValue((BigDecimal) result.getResult().get(0).get(1)));
			final Date lastOrderDate = (Date) result.getResult().get(0).get(2);
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
