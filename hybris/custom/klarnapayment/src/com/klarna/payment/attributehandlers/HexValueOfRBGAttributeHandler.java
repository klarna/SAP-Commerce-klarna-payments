package com.klarna.payment.attributehandlers;

import de.hybris.platform.servicelayer.model.attribute.AbstractDynamicAttributeHandler;

import com.klarna.payment.model.RBGColorModel;


public class HexValueOfRBGAttributeHandler extends AbstractDynamicAttributeHandler<String, RBGColorModel>
{
	/**
	 * Convert a RGB color value to an hexadecimal value
	 */
	@Override
	public String get(final RBGColorModel model)
	{
		return String.format("#%02x%02x%02x", model.getRedField(), model.getGreenField(), model.getBlueField()).toUpperCase();
	}
}
