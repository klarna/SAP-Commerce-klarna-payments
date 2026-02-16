package com.klarna.payment.converter.populator;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.acceleratorservices.urlresolver.SiteBaseUrlResolutionService;
import de.hybris.platform.basecommerce.model.site.BaseSiteModel;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.c2l.CountryModel;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.store.BaseStoreModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.klarna.api.payments.model.PaymentsAttachment;
import com.klarna.integration.dto.KlarnaCustomerInteractionConfigDTO;
import com.klarna.integration.dto.KlarnaPaymentRequestPayloadDTO;
import com.klarna.integration.dto.KlarnaShippingConfigDTO;
import com.klarna.integration.dto.KlarnaSupplementaryPurchaseDataDTO;
import com.klarna.payment.util.KlarnaServicesUtil;


public class KlarnaPaymentRequestPayloadPopulator implements Populator<AbstractOrderModel, KlarnaPaymentRequestPayloadDTO>
{

	protected static final Logger LOG = Logger.getLogger(KlarnaPaymentRequestPayloadPopulator.class);


	@Resource
	private KlarnaServicesUtil klarnaServicesUtil;

	@Resource
	private SiteConfigService siteConfigService;

	@Resource
	private SiteBaseUrlResolutionService siteBaseUrlResolutionService;

	@Resource
	private Converter<AbstractOrderModel, KlarnaSupplementaryPurchaseDataDTO> klarnaSupplementaryPurchaseDataConverter;

	@Resource(name = "klarnaAttachmentConverter")
	private Converter<AbstractOrderModel, PaymentsAttachment> klarnaAttachmentConverter;


	@Override
	public void populate(final AbstractOrderModel source, final KlarnaPaymentRequestPayloadDTO target) throws ConversionException
	{
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");

		target.setCurrency(source.getCurrency().getIsocode());
		target.setAmount(klarnaServicesUtil.calculateTotalAmount(source));
		target.setPaymentRequestReference(source.getGuid());
		target.setSupplementaryPurchaseData(klarnaSupplementaryPurchaseDataConverter.convert(source));
		populateShippingConfig(source, target);
		populateCustomerInteractionConfig(source, target);
		populateCollectCustomerProfile(source, target);
		populateAttachment(source, target);
	}

	protected void populateShippingConfig(final AbstractOrderModel source, final KlarnaPaymentRequestPayloadDTO target)
	{
		final KlarnaShippingConfigDTO shippingConfig = new KlarnaShippingConfigDTO();
		shippingConfig.setMode("EDITABLE");
		final BaseStoreModel baseStore = source.getStore();
		shippingConfig.setSupportedCountries(baseStore.getDeliveryCountries().stream().filter(Objects::nonNull)
				.map(CountryModel::getIsocode).filter(Objects::nonNull).distinct().sorted().collect(Collectors.toList()));
		target.setShippingConfig(shippingConfig);
	}

	protected void populateCustomerInteractionConfig(final AbstractOrderModel source, final KlarnaPaymentRequestPayloadDTO target)
	{
		final KlarnaCustomerInteractionConfigDTO customerInteractionConfig = new KlarnaCustomerInteractionConfigDTO();
		customerInteractionConfig.setMethod("HANDOVER");
		final BaseSiteModel baseSite = source.getSite();
		final String oneStepPSPCallbackRelativeUrl = siteConfigService.getProperty("klarna.expcheckout.onestep.psp.callback.url");
		if (StringUtils.isNotEmpty(oneStepPSPCallbackRelativeUrl))
		{
			customerInteractionConfig
					.setReturnUrl(siteBaseUrlResolutionService.getWebsiteUrlForSite(baseSite, true, oneStepPSPCallbackRelativeUrl));
		}
		target.setCustomerInteractionConfig(customerInteractionConfig);
	}

	protected void populateCollectCustomerProfile(final AbstractOrderModel source, final KlarnaPaymentRequestPayloadDTO target)
	{
		final List<String> collectCustomerProfileList = new ArrayList<>();
		collectCustomerProfileList.add("profile:email");
		collectCustomerProfileList.add("profile:phone");
		collectCustomerProfileList.add("profile:billing_address");
		target.setCollectCustomerProfile(collectCustomerProfileList);
	}

	protected void populateAttachment(final AbstractOrderModel source, final KlarnaPaymentRequestPayloadDTO target)
	{
		final PaymentsAttachment attachment = klarnaAttachmentConverter.convert(source);
		if (attachment != null && attachment.getBody() != null)
		{
			target.setAdditionalData(klarnaServicesUtil.convertRequestDtoToString(klarnaAttachmentConverter.convert(source)));
		}
	}
}
