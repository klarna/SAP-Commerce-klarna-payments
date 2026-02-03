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


public class KlarnaSupplementaryPurchaseDataPopulator implements Populator<AbstractOrderModel, KlarnaSupplementaryPurchaseDataDTO>
{

	@Resource
	private Converter<AbstractOrderEntryModel, KlarnaLineItemDTO> klarnaLineItemConverter;


	@Override
	public void populate(final AbstractOrderModel source, final KlarnaSupplementaryPurchaseDataDTO target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setPurchaseReference(source.getGuid());

		populateLineItems(source, target);

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
}
