/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2020 SAP SE
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * Hybris ("Confidential Information"). You shall not disclose such
 * Confidential Information and shall use it only in accordance with the
 * terms of the license agreement you entered into with SAP Hybris.
 */
package com.klarna.payment.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.ordersplitting.model.ConsignmentModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import java.util.List;
import java.util.Set;

import com.klarna.api.order_management.model.OrderManagementShippingInfo;


/**
 *
 */
public class KlarnaCaptureShippingInfoPopulator implements Populator<AbstractOrderModel, List<OrderManagementShippingInfo>>
{

	@Override
	public void populate(final AbstractOrderModel source, final List<OrderManagementShippingInfo> target)
			throws ConversionException
	{

		final Set<ConsignmentModel> consignments = source.getConsignments();

		for (final ConsignmentModel consignment : consignments)
		{
			final OrderManagementShippingInfo shippingInfo = new OrderManagementShippingInfo();
			shippingInfo.setShippingCompany(consignment.getCarrier() == null ? "" : consignment.getCarrier());
			shippingInfo.setShippingMethod(consignment.getDeliveryMode().getCode());
			shippingInfo.setTrackingNumber(consignment.getTrackingID() == null ? "" : consignment.getTrackingID());
			shippingInfo.setTrackingUri("");
			target.add(shippingInfo);
		}

	}


}
