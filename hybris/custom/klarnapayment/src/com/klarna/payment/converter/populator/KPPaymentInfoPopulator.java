package com.klarna.payment.converter.populator;

import de.hybris.platform.commercefacades.order.data.CardTypeData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import com.klarna.payment.data.KPPaymentInfoData;
import com.klarna.payment.model.KPPaymentInfoModel;


/**
 * Created by trung.le on 11/19/15.
 */
public class KPPaymentInfoPopulator implements Populator<KPPaymentInfoModel, KPPaymentInfoData>
{
	private Converter<AddressModel, AddressData> addressConverter;

	private static final String PAYMENT_TYPE = "KLARNA";

	@Override
	public void populate(final KPPaymentInfoModel source, final KPPaymentInfoData target) throws ConversionException
	{
		if (source.getBillingAddress() != null)
		{
			target.setBillingAddress(getAddressConverter().convert(source.getBillingAddress()));
			target.setFinalizeRequired(source.getFinalizeRequired());
			target.setPaymentOption(source.getPaymentOption());
			final CardTypeData cardTypeData = new CardTypeData();
			cardTypeData.setName(PAYMENT_TYPE);
			cardTypeData.setCode(PAYMENT_TYPE);
			target.setCardTypeData(cardTypeData);
			target.setCardType(PAYMENT_TYPE);
		}
	}

	public Converter<AddressModel, AddressData> getAddressConverter()
	{
		return addressConverter;
	}

	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}
}
