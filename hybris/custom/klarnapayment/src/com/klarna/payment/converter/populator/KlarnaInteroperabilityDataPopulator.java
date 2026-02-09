package com.klarna.payment.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderEntryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import javax.annotation.Resource;

import org.springframework.util.Assert;

import com.klarna.data.KlarnaConfigData;
import com.klarna.integration.dto.KlarnaContentDTO;
import com.klarna.integration.dto.KlarnaInteroperabilityDataDTO;
import com.klarna.integration.dto.KlarnaLineItemDTO;
import com.klarna.integration.dto.KlarnaShareRequestDTO;
import com.klarna.integration.dto.KlarnaSupplementaryPurchaseDataDTO;
import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.facades.KlarnaConfigFacade;
import com.klarna.payment.util.KlarnaServicesUtil;






public class KlarnaInteroperabilityDataPopulator implements Populator<AbstractOrderModel, KlarnaInteroperabilityDataDTO>
{

	@Resource
	private Converter<AbstractOrderEntryModel, KlarnaLineItemDTO> klarnaLineItemConverter;

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;
	@Resource(name = "klarnaConfigFacade")
	KlarnaConfigFacade klarnaConfigFacade;

	@Resource
	private KlarnaServicesUtil klarnaServicesUtil;

	@Resource
	private Converter<AbstractOrderModel, KlarnaSupplementaryPurchaseDataDTO> klarnaSupplementaryPurchaseDataConverter;

	@Override
	public void populate(final AbstractOrderModel source, final KlarnaInteroperabilityDataDTO target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		final KlarnaContentDTO content = new KlarnaContentDTO();
		final KlarnaShareRequestDTO request = new KlarnaShareRequestDTO();

		final KlarnaConfigData klarnaConfig = klarnaConfigFacade.getKlarnaConfig();
		if (Boolean.TRUE.equals(klarnaConfig.getShareShoppingData()))
		{
			request.setSupplementaryPurchaseData(klarnaSupplementaryPurchaseDataConverter.convert(source));
		}
		// TODO - ASk if this is needed

		request.setAmount(klarnaServicesUtil.calculateTotalAmount(source));
		content.setOperation(
				configurationService.getConfiguration().getString(KlarnapaymentConstants.KLARNA_INTEROPERABILITY_DATA_OPERATION));
		content.setRequest(request);

		target.setContent(content);
		target.setContentType(
				configurationService.getConfiguration().getString(KlarnapaymentConstants.KLARNA_INTEROPERABILITY_DATA_CONTENT_TYPE));


	}

}
