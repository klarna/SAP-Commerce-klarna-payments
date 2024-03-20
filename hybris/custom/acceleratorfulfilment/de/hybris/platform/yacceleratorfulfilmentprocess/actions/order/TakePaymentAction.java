/*
 * Copyright (c) 2019 SAP SE or an SAP affiliate company. All rights reserved.
 */
package de.hybris.platform.yacceleratorfulfilmentprocess.actions.order;

import de.hybris.platform.core.enums.OrderStatus;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.order.OrderModel;
import de.hybris.platform.orderprocessing.model.OrderProcessModel;
import de.hybris.platform.payment.PaymentService;
import de.hybris.platform.payment.dto.TransactionStatus;
import de.hybris.platform.payment.dto.TransactionStatusDetails;
import de.hybris.platform.payment.enums.PaymentTransactionType;
import de.hybris.platform.payment.model.PaymentTransactionEntryModel;
import de.hybris.platform.payment.model.PaymentTransactionModel;
import de.hybris.platform.processengine.action.AbstractSimpleDecisionAction;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.Iterator;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.klarna.api.order_management.OrderManagementCapturesApi;
import com.klarna.api.order_management.model.OrderManagementCaptureObject;
import com.klarna.payment.facades.KPConfigFacade;
import com.klarna.payment.facades.KPOrderFacade;
import com.klarna.payment.facades.KPPaymentFacade;
import com.klarna.payment.model.KPPaymentInfoModel;
import com.klarna.payment.model.KlarnaPayConfigModel;


/**
 *
 */
public class TakePaymentAction extends AbstractSimpleDecisionAction<OrderProcessModel>
{
	private static final Logger LOG = Logger.getLogger(TakePaymentAction.class);

	private PaymentService paymentService;

	private KPOrderFacade kpOrderFacade;

	private KPPaymentFacade kpPaymentFacade;

	private Converter<AbstractOrderModel, OrderManagementCaptureObject> klarnaOrderCaptureConverter;

	private KPConfigFacade kpConfigFacade;


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
	 * @return the kpPaymentFacade
	 */
	public KPPaymentFacade getKpPaymentFacade()
	{
		return kpPaymentFacade;
	}

	/**
	 * @param kpPaymentFacade
	 *           the kpPaymentFacade to set
	 */
	public void setKpPaymentFacade(final KPPaymentFacade kpPaymentFacade)
	{
		this.kpPaymentFacade = kpPaymentFacade;
	}

	/**
	 * @return the klarnaOrderCaptureConverter
	 */
	public Converter<AbstractOrderModel, OrderManagementCaptureObject> getKlarnaOrderCaptureConverter()
	{
		return klarnaOrderCaptureConverter;
	}

	/**
	 * @param klarnaOrderCaptureConverter
	 *           the klarnaOrderCaptureConverter to set
	 */
	public void setKlarnaOrderCaptureConverter(
			final Converter<AbstractOrderModel, OrderManagementCaptureObject> klarnaOrderCaptureConverter)
	{
		this.klarnaOrderCaptureConverter = klarnaOrderCaptureConverter;
	}

	/**
	 * @return the kpOrderFacade
	 */
	public KPOrderFacade getKpOrderFacade()
	{
		return kpOrderFacade;
	}

	/**
	 * @param kpOrderFacade
	 *           the kpOrderFacade to set
	 */
	public void setKpOrderFacade(final KPOrderFacade kpOrderFacade)
	{
		this.kpOrderFacade = kpOrderFacade;
	}

	@Override
	public Transition executeAction(final OrderProcessModel process)
	{
		final OrderModel order = process.getOrder();
		final KlarnaPayConfigModel klarnaConfig = kpConfigFacade.getKlarnaConfigForStore(order.getStore());
		boolean captureRequired = false;
		if (!klarnaConfig.getAutoCapture().booleanValue() && !klarnaConfig.getIsVCNEnabled().booleanValue())
		{
			captureRequired = true;
		}
		if (captureRequired)
		{
			for (final PaymentTransactionModel txn : order.getPaymentTransactions())
			{
				if (txn.getInfo() instanceof KPPaymentInfoModel)
				{


					if (LOG.isDebugEnabled())
					{
						LOG.debug("The payment transaction has been captured. Order: " + order.getCode() + ". Txn: " + txn.getCode());
					}

					// Call Klarna order captures API
					return doCapture(order, txn);


				}
			}
		}
		else
		{
			setOrderStatus(order, OrderStatus.PAYMENT_CAPTURED);
		}

		return Transition.OK;
	}

	private Transition doCapture(final OrderModel order, final PaymentTransactionModel txn)
	{

		final OrderManagementCapturesApi captureOrder = getKpOrderFacade().captureKlarnaOrder(order.getKpOrderId(),
				order.getStore());
		final OrderManagementCaptureObject orderCaptureObject = getKlarnaOrderCaptureConverter().convert(order);
		try
		{
			final String capture_id = captureOrder.create(orderCaptureObject);

			if (!capture_id.equals(""))
			{
				setOrderStatus(order, OrderStatus.PAYMENT_CAPTURED);
				klarnaCapture(capture_id, txn);

			}
			else
			{
				LOG.error("The payment transaction capture has failed. Order: " + order.getCode() + ". Txn: " + txn.getCode());
				setOrderStatus(order, OrderStatus.PAYMENT_NOT_CAPTURED);
				return Transition.NOK;
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
			LOG.error("Error from Capture API - " + e.getMessage());
			return Transition.NOK;
		}
		return Transition.OK;
	}

	public void klarnaCapture(final String capture_id, final PaymentTransactionModel transaction)
	{

		PaymentTransactionEntryModel auth = null;
		final Iterator txn = transaction.getEntries().iterator();

		while (txn.hasNext())
		{
			final PaymentTransactionEntryModel pte = (PaymentTransactionEntryModel) txn.next();
			if (pte.getType().equals(PaymentTransactionType.AUTHORIZATION))
			{
				auth = pte;
				break;
			}
		}
		final PaymentTransactionType paymentTransactionType = PaymentTransactionType.CAPTURE;

		getKpPaymentFacade().createTransactionEntry(capture_id, (KPPaymentInfoModel) transaction.getInfo(), transaction,
				paymentTransactionType, TransactionStatus.ACCEPTED.name(), TransactionStatusDetails.SUCCESFULL.name());

	}

	protected PaymentService getPaymentService()
	{
		return paymentService;
	}

	@Required
	public void setPaymentService(final PaymentService paymentService)
	{
		this.paymentService = paymentService;
	}
}
