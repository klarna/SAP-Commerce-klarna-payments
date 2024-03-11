package com.klarna.payment.converter.populator;

import de.hybris.platform.acceleratorservices.config.SiteConfigService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;

import org.apache.commons.lang.StringUtils;

import com.klarna.payment.data.KlarnaConfigData;
import com.klarna.payment.data.KlarnaMerchantURLs;
import com.klarna.payment.exceptions.MissingMerchantURLException;
import com.klarna.payment.model.KlarnaPayConfigModel;


public class KPConfigPopulator implements Populator<KlarnaPayConfigModel, KlarnaConfigData>
{
	private static final String MERCHANT_CONFIRM_PAGE_URL_NOT_FIND = "Not find URL of merchant confirmation page";
	private static final String KP_MERCHANT_URL_CONFIRMATION = "klarnacheckout.merchant.url.confirmation";
	private static final String KP_MERCHANT_URL_NOTIFICATION = "klarnacheckout.merchant.url.notification";

	private SiteConfigService siteConfigService;

	/**
	 * Populate data from KlarnaConfigModel to KlarnaConfigData
	 */
	@Override
	public void populate(final KlarnaPayConfigModel source, final KlarnaConfigData target) throws ConversionException
	{
		target.setCode(source.getCode());
		target.setActive(source.getActive());
		target.setMerchantID(source.getMerchantID());
		target.setSharedSecret(source.getSharedSecret());
		target.setEndpointMode(source.getEndpointMode().getCode());
		target.setMerchantEmail(source.getMerchantEmail());
		target.setEndpointType(source.getEndpointType().getCode());
		//target.setEndPointUrl(source.getEndPointUrl());
		target.setAttachementRequired(source.getAttachementRequired());
		target.setProductUrlsRequired(source.getProductUrlsRequired());
		target.setPurchaseCurrency(source.getPurchaseCurrency().getIsocode());
		target.setMerchantReference2(source.getMerchantReference2());
		target.setAutoCapture(source.getAutoCapture());
		target.setVcnPrivateKey(source.getVcnPrivateKey());
		target.setVcnKeyID(source.getVcnKeyID());

		if (source.getIsVCNEnabled() != null)
		{
			target.setIsVCNEnabled(source.getIsVCNEnabled());
		}
		else
		{
			target.setIsVCNEnabled(Boolean.FALSE);
		}

		if (source.getRadiusborder() != null)
		{
			target.setRadiusborder(source.getRadiusborder().toString() + "px");
		}
		setColorTxtAttr(source, target);
		setColorAttr(source, target);
		createMerchantURLs(target);

	}

	private void setColorTxtAttr(final KlarnaPayConfigModel source, final KlarnaConfigData target)
	{
		if (source.getColorButtonText() != null)
		{
			target.setColorButtonText(source.getColorButtonText().getHexValue());
		}
		if (source.getColorText() != null)
		{
			target.setColorText(source.getColorText().getHexValue());
		}
		if (source.getColorTextSecondary() != null)
		{
			target.setColorTextSecondary(source.getColorTextSecondary().getHexValue());
		}
	}

	private void setColorAttr(final KlarnaPayConfigModel source, final KlarnaConfigData target)
	{
		if (source.getColorButton() != null)
		{
			target.setColorButton(source.getColorButton().getHexValue());
		}

		if (source.getColorCheckbox() != null)
		{
			target.setColorCheckbox(source.getColorCheckbox().getHexValue());
		}
		if (source.getColorCheckboxCheckMark() != null)
		{
			target.setColorCheckboxCheckMark(source.getColorCheckboxCheckMark().getHexValue());
		}
		if (source.getColorHeader() != null)
		{
			target.setColorHeader(source.getColorHeader().getHexValue());
		}
		if (source.getColorLink() != null)
		{
			target.setColorLink(source.getColorLink().getHexValue());
		}

		if (source.getColorBorder() != null)
		{
			target.setColorBorder(source.getColorBorder().getHexValue());
		}
		if (source.getColorBorderSelected() != null)
		{
			target.setColorBorderSelected(source.getColorBorderSelected().getHexValue());
		}

		if (source.getColorDetails() != null)
		{
			target.setColorDetails(source.getColorDetails().getHexValue());
		}

	}

	/**
	 * @param target
	 *           Read URL from project.properties
	 */
	private void createMerchantURLs(final KlarnaConfigData target)
	{
		final KlarnaMerchantURLs klarnaMerchantURLs = new KlarnaMerchantURLs();
		target.setKlarnaMerchantURLs(klarnaMerchantURLs);

		if (StringUtils.isEmpty(siteConfigService.getProperty(KP_MERCHANT_URL_CONFIRMATION)))
		{
			throw new MissingMerchantURLException(MERCHANT_CONFIRM_PAGE_URL_NOT_FIND);
		}
		klarnaMerchantURLs.setConfirmationURL(siteConfigService.getProperty(KP_MERCHANT_URL_CONFIRMATION));

		klarnaMerchantURLs.setNotificationUpdateURL(siteConfigService.getProperty(KP_MERCHANT_URL_NOTIFICATION));
	}

	/**
	 * @param siteConfigService
	 *           the siteConfigService to set
	 */
	public void setSiteConfigService(final SiteConfigService siteConfigService)
	{
		this.siteConfigService = siteConfigService;
	}


}
