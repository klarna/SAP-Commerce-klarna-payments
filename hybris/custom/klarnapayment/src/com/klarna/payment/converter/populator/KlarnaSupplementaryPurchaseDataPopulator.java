package com.klarna.payment.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.Assert;

import com.klarna.integration.dto.KlarnaLineItemDTO;
import com.klarna.integration.dto.KlarnaSupplementaryPurchaseDataDTO;
import com.klarna.payment.util.KlarnaConversionUtils;
import com.klarna.payment.util.KlarnaServicesUtil;


public class KlarnaSupplementaryPurchaseDataPopulator implements Populator<AbstractOrderModel, KlarnaSupplementaryPurchaseDataDTO>
{

	@Resource
	private Converter<AbstractOrderEntryModel, KlarnaLineItemDTO> klarnaLineItemConverter;

	@Resource
	private KlarnaServicesUtil klarnaServicesUtil;


	@Override
	public void populate(final AbstractOrderModel source, final KlarnaSupplementaryPurchaseDataDTO target)
			throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setPurchaseReference(source.getCode());
		populateLineItems(source, target);
		populateShippingLineItem(source, target);
	}

	protected void populateLineItems(final AbstractOrderModel source, final KlarnaSupplementaryPurchaseDataDTO target)
	{
		final List<KlarnaLineItemDTO> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(source.getEntries()))
		{
			for (final AbstractOrderEntryModel entry : source.getEntries())
			{
				lineItems.add(klarnaLineItemConverter.convert(entry));
			}
		}
		target.setLineItems(lineItems);
	}

	protected void populateShippingLineItem(final AbstractOrderModel source, final KlarnaSupplementaryPurchaseDataDTO target)
	{
		if (source.getDeliveryMode() != null)
		{
			final KlarnaLineItemDTO shippingLineItem = new KlarnaLineItemDTO();
			shippingLineItem.setName(source.getDeliveryMode().getName());
			shippingLineItem.setQuantity(1L);
			shippingLineItem.setTotalAmount(KlarnaConversionUtils.getKlarnaLongValue(source.getDeliveryCost()));
			shippingLineItem.setTotalTaxAmount(klarnaServicesUtil.calculateDeliveryTaxAmount(source));
			target.getLineItems().add(shippingLineItem);
		}
	}

}