package com.klarna.payment.converter.populator;

import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.springframework.util.Assert;

import com.klarna.payment.data.KlarnaShippingOptionData;
import com.klarna.payment.util.KlarnaConversionUtils;


public class KlarnaShippingOptionDataPopulator implements Populator<DeliveryModeData, KlarnaShippingOptionData>
{

	@Override
	public void populate(final DeliveryModeData source, final KlarnaShippingOptionData target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setShippingOptionReference(source.getCode());
		target.setDescription(source.getDescription());
		target.setDisplayName(source.getName());
		target.setAmount(KlarnaConversionUtils.getKlarnaLongValue(source.getDeliveryCost().getValue()));
		//target.setShippingType(null);
	}
}
