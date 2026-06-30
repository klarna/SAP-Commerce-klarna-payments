package com.klarna.payment.converter.populator;

import de.hybris.platform.commercefacades.order.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.DeliveryModeData;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;

import com.klarna.payment.data.KlarnaLineItemData;
import com.klarna.payment.data.KlarnaShippingChangeResponseData;
import com.klarna.payment.data.KlarnaShippingOptionData;
import com.klarna.payment.util.KlarnaConversionUtils;
import com.klarna.payment.util.KlarnaServicesUtil;


public class KlarnaShippingChangeResponsePopulator implements Populator<AbstractOrderModel, KlarnaShippingChangeResponseData>
{

	@Resource(name = "checkoutFacade")
	private CheckoutFacade checkoutFacade;

	@Resource
	private KlarnaServicesUtil klarnaServicesUtil;

	@Resource
	private Converter<AbstractOrderEntryModel, KlarnaLineItemData> klarnaLineItemDataConvertor;

	@Resource
	private Converter<DeliveryModeData, KlarnaShippingOptionData> klarnaShippingOptionDataConvertor;


	@Override
	public void populate(final AbstractOrderModel source, final KlarnaShippingChangeResponseData target)
			throws ConversionException
	{
		target.setAmount(klarnaServicesUtil.calculateTotalAmount(source));
		if (source.getDeliveryMode() != null)
		{
			target.setSelectedShippingOptionReference(source.getDeliveryMode().getCode());
		}
		populateLineItems(source, target);
		populateShippingLineItem(source, target);
		populateShippingOptions(source, target);
	}

	protected void populateLineItems(final AbstractOrderModel source, final KlarnaShippingChangeResponseData target)
	{
		final List<KlarnaLineItemData> lineItems = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(source.getEntries()))
		{
			for (final AbstractOrderEntryModel entry : source.getEntries())
			{
				lineItems.add(klarnaLineItemDataConvertor.convert(entry));
			}
		}
		target.setLineItems(lineItems);
	}

	protected void populateShippingOptions(final AbstractOrderModel source, final KlarnaShippingChangeResponseData target)
	{
		final List<KlarnaShippingOptionData> shippingOptions = new ArrayList<>();
		final List<? extends DeliveryModeData> delieryModes = checkoutFacade.getSupportedDeliveryModes();
		if (CollectionUtils.isNotEmpty(delieryModes))
		{
			for (final DeliveryModeData delieryMode : delieryModes)
			{
				shippingOptions.add(klarnaShippingOptionDataConvertor.convert(delieryMode));
			}
		}
		target.setShippingOptions(shippingOptions);
	}

	protected void populateShippingLineItem(final AbstractOrderModel source, final KlarnaShippingChangeResponseData target)
	{
		if (source.getDeliveryMode() != null)
		{
			final KlarnaLineItemData shippingLineItem = new KlarnaLineItemData();
			shippingLineItem.setName(source.getDeliveryMode().getName());
			shippingLineItem.setQuantity(1L);
			shippingLineItem.setTotalAmount(KlarnaConversionUtils.getKlarnaLongValue(source.getDeliveryCost()));
			shippingLineItem.setTotalTaxAmount(klarnaServicesUtil.calculateDeliveryTaxAmount(source));
			target.getLineItems().add(shippingLineItem);
		}
	}

}
