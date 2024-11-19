package com.klarna.payment.facades;

import de.hybris.platform.store.BaseStoreModel;

import java.io.IOException;

import com.klarna.api.Client;
import com.klarna.api.model.ApiException;
import com.klarna.api.order_management.OrderManagementCapturesApi;
import com.klarna.api.order_management.OrderManagementRefundsApi;
import com.klarna.api.shoppingdata.KlarnaShoppingDataAPI;


public interface KPOrderFacade
{
	String getKlarnaIdByOrderByKid(final String kid);

	Client getKlarnaClient(BaseStoreModel store);

	void updateOrderForPending(String klarnaOrderId, String status) throws ApiException, IOException;

	OrderManagementCapturesApi captureKlarnaOrder(String orderId, BaseStoreModel store);

	String cancelKlarnaOrder(String order_Id);

	OrderManagementRefundsApi getRefundKlarnaOrder(String orderId);

	String refundKlarnaOrder(String orderId);
	
	KlarnaShoppingDataAPI getKlarnaShoppingDataAPI(String sessionId);

}
