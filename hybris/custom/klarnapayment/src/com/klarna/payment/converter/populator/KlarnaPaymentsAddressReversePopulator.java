package com.klarna.payment.converter.populator;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang3.StringUtils;

import com.klarna.api.payments.model.PaymentsAddress;


public class KlarnaPaymentsAddressReversePopulator implements Populator<PaymentsAddress, AddressData>
{

	@Override
	public void populate(final PaymentsAddress source, final AddressData target)
			throws ConversionException
	{
		target.setTitle(source.getTitle());
		target.setFirstName(source.getGivenName());
		target.setLastName(source.getFamilyName());
		target.setEmail(source.getEmail());
		target.setCompanyName(source.getOrganizationName());
		target.setLine1(source.getStreetAddress());
		target.setLine2(source.getStreetAddress2());
		target.setTown(source.getCity());
		target.setPostalCode(source.getPostalCode());
		target.setPhone(source.getPhone());

		if (StringUtils.isNotEmpty(source.getCountry()))
		{
			final CountryData countryData = new CountryData();
			countryData.setIsocode(source.getCountry());
			target.setCountry(countryData);
		}

		if (StringUtils.isNotEmpty(source.getRegion()))
		{
			final RegionData regionData = new RegionData();
			regionData.setIsocodeShort(source.getRegion());
			target.setRegion(regionData);
		}
	}

}
