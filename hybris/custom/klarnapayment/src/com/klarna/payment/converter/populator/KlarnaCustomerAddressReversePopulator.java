package com.klarna.payment.converter.populator;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang3.StringUtils;

import com.klarna.payment.data.KlarnaCustomerProfileData;


public class KlarnaCustomerAddressReversePopulator implements Populator<KlarnaCustomerProfileData, AddressData>
{

	@Override
	public void populate(final KlarnaCustomerProfileData source, final AddressData target) throws ConversionException
	{

		target.setFirstName(source.getGivenName());
		target.setLastName(source.getFamilyName());
		target.setEmail(source.getEmail());

		target.setLine1(source.getAddress().getStreetAddress());
		target.setLine2(source.getAddress().getStreetAddress2());
		target.setTown(source.getAddress().getCity());
		target.setPostalCode(source.getAddress().getPostalCode());
		target.setPhone(source.getPhone());

		if (StringUtils.isNotEmpty(source.getCountry()))
		{
			final CountryData countryData = new CountryData();
			countryData.setIsocode(source.getCountry());
			target.setCountry(countryData);
		}

		if (StringUtils.isNotEmpty(source.getAddress().getRegion()))
		{
			final RegionData regionData = new RegionData();
			regionData.setIsocodeShort(source.getAddress().getRegion());
			target.setRegion(regionData);
		}
	}

}
