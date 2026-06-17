package com.klarna.payment.converter.populator;

import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.config.ConfigurationService;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;

import javax.annotation.Resource;

import org.springframework.util.Assert;

import com.klarna.integration.dto.KlarnaContentDTO;
import com.klarna.integration.dto.KlarnaNetworkSessionDataDTO;
import com.klarna.integration.dto.KlarnaShareRequestDTO;
import com.klarna.integration.dto.KlarnaSupplementaryPurchaseDataDTO;
import com.klarna.payment.constants.KlarnapaymentConstants;
import com.klarna.payment.util.KlarnaServicesUtil;


public class KlarnaNetworkSessionDataPopulator implements Populator<AbstractOrderModel, KlarnaNetworkSessionDataDTO>
{

	@Resource(name = "configurationService")
	private ConfigurationService configurationService;

	@Resource
	private KlarnaServicesUtil klarnaServicesUtil;

	@Resource
	private Converter<AbstractOrderModel, KlarnaSupplementaryPurchaseDataDTO> klarnaSupplementaryPurchaseDataConverter;

	@Override
	public void populate(final AbstractOrderModel source, final KlarnaNetworkSessionDataDTO target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		final KlarnaContentDTO content = new KlarnaContentDTO();
		final KlarnaShareRequestDTO request = new KlarnaShareRequestDTO();
		request.setSupplementaryPurchaseData(klarnaSupplementaryPurchaseDataConverter.convert(source));
		request.setAmount(klarnaServicesUtil.calculateTotalAmount(source));
		content.setOperation(
				configurationService.getConfiguration().getString(KlarnapaymentConstants.KLARNA_NETWORK_SESSION_DATA_OPERATION_KEY));
		content.setRequest(request);

		target.setContent(content);
		target.setContentType(
				configurationService.getConfiguration()
						.getString(KlarnapaymentConstants.KLARNA_NETWORK_SESSION_DATA_CONTENT_TYPE_KEY));

	}

}
