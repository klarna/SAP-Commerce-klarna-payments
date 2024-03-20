package com.klarna.payment.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang3.StringUtils;

import com.klarna.payment.data.KlarnaExpCheckoutConfigData;
import com.klarna.payment.model.KlarnaExpCheckoutConfigModel;


public class KlarnaExpCheckoutConfigPopulator implements Populator<KlarnaExpCheckoutConfigModel, KlarnaExpCheckoutConfigData>
{

	@Override
	public void populate(final KlarnaExpCheckoutConfigModel source, final KlarnaExpCheckoutConfigData target)
			throws ConversionException
	{
		target.setCode(source.getCode());
		target.setActive(source.getActive());
		target.setClientKey(source.getClientKey());
		target.setScriptUrl(source.getScriptUrl());
		target.setCollectShippingAddress(source.getCollectShippingAddress());
		/* target.setAutoFinalize(source.getAutoFinalize()); */
		target.setButtonShape(source.getButtonShape().getCode());
		target.setButtonTheme(source.getButtonTheme().getCode());
		target.setCountry((source.getCountry() != null) ? source.getCountry().getIsocode() : StringUtils.EMPTY);
	}

}
