package com.klarna.payment.converter.populator;

import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.commercefacades.user.data.RegionData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang3.StringUtils;

import com.klarna.payment.data.KlarnaAddressData;


public class KlarnaAddressReversePopulator implements Populator<KlarnaAddressData, AddressData>
{

	@Override
	public void populate(final KlarnaAddressData source, final AddressData target)
			throws ConversionException
	{
		target.setFirstName("Dummy FirstName");
		target.setLastName("Dummy LastName");
		target.setLine1((StringUtils.isNotEmpty(source.getStreetAddress())) ? source.getStreetAddress() : "Dummy Line1");
		target.setTown(source.getCity());
		target.setPostalCode(source.getPostalCode());
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
