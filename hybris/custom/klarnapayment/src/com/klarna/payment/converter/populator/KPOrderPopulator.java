package com.klarna.payment.converter.populator;

import de.hybris.platform.commercefacades.order.converters.populator.AbstractOrderPopulator;
import de.hybris.platform.commercefacades.order.data.AbstractOrderData;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import com.klarna.payment.data.KPPaymentInfoData;
import com.klarna.payment.model.KPPaymentInfoModel;


public class KPOrderPopulator extends AbstractOrderPopulator<AbstractOrderModel, AbstractOrderData>
{
	private Converter<KPPaymentInfoModel, KPPaymentInfoData> kpPaymentInfoConverter;

	@Override
	public void populate(final AbstractOrderModel source, final AbstractOrderData target) throws ConversionException
	{
		if (source.getPaymentInfo() instanceof KPPaymentInfoModel)
		{
			target.setKpOrderId(source.getKpOrderId());
			target.setKpFraudStatus(source.getKpFraudStatus().toString());
			target.setKpIsPendingOrder(source.getIsKpPendingOrder());
			target.setKpIdentifier(source.getKpIdentifier());
			if (source.getPaymentAddress() != null)
			{
				final KPPaymentInfoData kpPaymentInfoData = getKpPaymentInfoConverter()
						.convert((KPPaymentInfoModel) source.getPaymentInfo());
				target.setKpPaymentInfo(kpPaymentInfoData);
				target.setPaymentInfo(kpPaymentInfoData);
			}
		}
	}

	/**
	 * @return the kpPaymentInfoConverter
	 */
	public Converter<KPPaymentInfoModel, KPPaymentInfoData> getKpPaymentInfoConverter()
	{
		return kpPaymentInfoConverter;
	}

	/**
	 * @param kpPaymentInfoConverter
	 *           the kpPaymentInfoConverter to set
	 */
	public void setKpPaymentInfoConverter(final Converter<KPPaymentInfoModel, KPPaymentInfoData> kpPaymentInfoConverter)
	{
		this.kpPaymentInfoConverter = kpPaymentInfoConverter;
	}

}
