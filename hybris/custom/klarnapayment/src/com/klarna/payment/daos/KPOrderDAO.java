package com.klarna.payment.daos;

import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.core.model.user.UserModel;
import de.hybris.platform.store.BaseStoreModel;

import java.util.List;

import com.klarna.api.custom.model.PaymentHistoryFull;


public interface KPOrderDAO
{
	/**
	 * find OderModel by Klarna Order ID
	 *
	 * @param klarnaOrderId
	 * @return OrderModel
	 */
	List<OrderModel> findOderByKlarnaOrderId(String klarnaOrderId);

	/**
	 * find Abstract Order Model by klarna reference id
	 *
	 * @param kid
	 * @return AbstractOrderModel
	 */
	List<AbstractOrderModel> findOrderByKid(final String kid);

	PaymentHistoryFull getAggregatePaymentHistory(final UserModel user, final BaseStoreModel baseStore);

}