package com.klarna.payment.converter.populator;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import com.klarna.api.payments.model.PaymentsAddress;


public class KlarnaPaymentsAddressPopulator implements Populator<AddressData, PaymentsAddress>
{

	@Override
	public void populate(final AddressData source, final PaymentsAddress target)
			throws ConversionException
	{
		target.setTitle(source.getTitle() != null ? source.getTitle().replace(".", "") : "");
		target.setGivenName(source.getFirstName());
		target.setFamilyName(source.getLastName());
		target.setEmail(source.getEmail());
		target.setPhone(source.getPhone());

		target.setStreetAddress((source.getLine1() != null) ? source.getLine1() : source.getStreetname());
		target.setStreetAddress2((source.getLine2() != null) ? source.getLine2() : source.getStreetnumber());

		target.setPostalCode(source.getPostalCode());
		target.setCity(source.getTown());
		target.setOrganizationName(source.getCompanyName());

		if (source.getCountry() != null)
		{
			target.setCountry(source.getCountry().getIsocode());

		}
		if (source.getRegion() != null)
		{
			target.setRegion(source.getRegion().getIsocodeShort());
		}
	}
}
