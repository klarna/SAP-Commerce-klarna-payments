package com.klarna.payment.converter.populator;

import de.hybris.platform.commercefacades.customer.CustomerFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commerceservices.customer.CustomerAccountService;
import de.hybris.platform.converters.Populator;
import de.hybris.platform.core.model.order.AbstractOrderModel;
import de.hybris.platform.core.model.user.AddressModel;
import de.hybris.platform.servicelayer.dto.converter.ConversionException;
import de.hybris.platform.servicelayer.dto.converter.Converter;
import de.hybris.platform.servicelayer.model.ModelService;
import de.hybris.platform.servicelayer.user.UserService;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import com.klarna.api.payments.model.PaymentsAddress;
import com.klarna.api.payments.model.PaymentsCustomer;
import com.klarna.api.payments.model.PaymentsMerchantUrls;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.payment.data.KlarnaConfigData;
import com.klarna.payment.data.KlarnaMerchantURLs;
import com.klarna.payment.facades.KPConfigFacade;
import com.klarna.payment.facades.KPCustomerFacade;
import com.klarna.payment.util.KlarnaDateFormatterUtil;
import com.klarna.payment.util.LogHelper;


public class KPCreditSessionPopulator implements Populator<AbstractOrderModel, PaymentsSession>
{

	protected static final Logger LOG = Logger.getLogger(KPCreditSessionPopulator.class);


	private KPConfigFacade kpConfigFacade;


	private KPCreditSessionInitialPopulator kpCreditSessionInitialPopulator;
	private KPCustomerFacade kpCustomerFacade;
	private CustomerAccountService customerAccountService;
	private CustomerFacade customerFacade;
	private UserService userService;
	private Converter<AddressModel, AddressData> addressConverter;
	private ModelService modelService;


	@Override
	public void populate(final AbstractOrderModel source, final PaymentsSession target) throws ConversionException
	{
		LogHelper.debugLog(LOG, "inside full populator");
		Assert.notNull(source, "Parameter source cannot be null.");
		Assert.notNull(target, "Parameter target cannot be null.");
		final KlarnaConfigData klarnaConfig = kpConfigFacade.getKlarnaConfig();
		kpCreditSessionInitialPopulator.populate(source, target);
		addKlarnaOthers(source, target);
		addMerchantReferences(source, target, klarnaConfig);


	}

	private void addKlarnaOthers(final AbstractOrderModel source, final PaymentsSession target)
	{
		updatedAddressFromCartModel(source, target);
		target.setCustomer(getCustomer(source.getPaymentAddress()));
		target.setMerchantUrls(getMerchantUrl(source.getKpIdentifier()));

	}

	private void addMerchantReferences(final AbstractOrderModel source, final PaymentsSession target,
			final KlarnaConfigData klarnaConfig)
	{
		target.setMerchantReference1(source.getCode());
		if (klarnaConfig.getMerchantReference2() != null)
		{
			if (modelService.getAttributeValue(source, klarnaConfig.getMerchantReference2()) != null)
			{
				final String reference2 = modelService.getAttributeValue(source, klarnaConfig.getMerchantReference2()).toString();
				target.setMerchantReference2(reference2);
			}
		}

	}

	private void updatedAddressFromCartModel(final AbstractOrderModel source, final PaymentsSession target)
	{
		LogHelper.debugLog(LOG, "Entering updatedAddressFromCartModel ");
		if (source.getPaymentAddress() != null)
		{
			target.setBillingAddress(getKlarnaAddress(addressConverter.convert(source.getPaymentAddress())));
		}
		else
		{
			target.setBillingAddress(new PaymentsAddress());
		}
		if (source.getDeliveryAddress() != null)
		{
			target.setShippingAddress(getKlarnaAddress(addressConverter.convert(source.getDeliveryAddress())));
		}

	}

	private PaymentsCustomer getCustomer(final AddressModel addressModel)
	{
		LogHelper.debugLog(LOG, "Entering getCustomer ");
		PaymentsCustomer customer = null;
		final String dobStr = getDateOfBirth(addressModel);
		if (StringUtils.isNotEmpty(dobStr))
		{
			customer = new PaymentsCustomer();
			customer.setDateOfBirth(dobStr);
		}
		return customer;
	}


	private PaymentsMerchantUrls getMerchantUrl(final String kid)
	{
		LogHelper.debugLog(LOG, "Entering getMerchantUrl ");
		final KlarnaConfigData klarnaConfig = kpConfigFacade.getKlarnaConfig();
		final KlarnaMerchantURLs merchantUrl = klarnaConfig.getKlarnaMerchantURLs();
		final PaymentsMerchantUrls urls = new PaymentsMerchantUrls();

		String confirmationUrl = StringUtils.isNotBlank(merchantUrl.getConfirmationURL()) ? merchantUrl.getConfirmationURL()
				: StringUtils.EMPTY;
		String notificationUrl = StringUtils.isNotBlank(merchantUrl.getNotificationUpdateURL())
				? merchantUrl.getNotificationUpdateURL()
				: StringUtils.EMPTY;
		if (StringUtils.isNotBlank(kid) && StringUtils.isNotBlank(confirmationUrl))
		{
			confirmationUrl = confirmationUrl + "?kid=" + kid;
		}

		if (StringUtils.isNotBlank(kid) && StringUtils.isNotBlank(notificationUrl))
		{
			notificationUrl = notificationUrl + "?kid=" + kid;
		}
		urls.setConfirmation(confirmationUrl);
		urls.setNotification(notificationUrl);
		return urls;
	}

	private PaymentsAddress getKlarnaAddress(final AddressData addressData)
	{
		LogHelper.debugLog(LOG, "Entering getKlarnaAddress ");
		final PaymentsAddress klarnaAddress = new PaymentsAddress();

		klarnaAddress.setTitle(addressData.getTitle() != null ? addressData.getTitle().replace(".", "") : "");
		klarnaAddress.setGivenName(addressData.getFirstName());
		klarnaAddress.setFamilyName(addressData.getLastName());
		if (addressData.getLine1() != null)
		{
			klarnaAddress.setStreetAddress(addressData.getLine1());
		}
		else
		{
			klarnaAddress.setStreetAddress(addressData.getStreetname());
		}

		if (addressData.getLine1() != null)
		{
			klarnaAddress.setStreetAddress2(addressData.getLine2());
		}
		else
		{
			klarnaAddress.setStreetAddress2(addressData.getStreetnumber());
		}
		if (addressData.getBuilding() != null)
		{
			//klarnaAddress.setHouseExtension(addressData.getBuilding());
		}
		klarnaAddress.setPostalCode(addressData.getPostalCode());

		klarnaAddress.setCity(addressData.getTown());
		if (addressData.getCountry() != null)
		{
			klarnaAddress.setCountry(addressData.getCountry().getIsocode());

		}
		if (addressData.getRegion() != null)
		{
			klarnaAddress.setRegion(addressData.getRegion().getIsocodeShort());
		}
		String email = addressData.getEmail();
		if (email == null)
		{
			email = kpCustomerFacade.getCurrentCustomerEmail();
		}
		klarnaAddress.setEmail(email);
		klarnaAddress.setPhone(addressData.getPhone());

		return klarnaAddress;
	}

	private String getDateOfBirth(final AddressModel addressModel)
	{
		LogHelper.debugLog(LOG, "Entering getDateOfBirth ");

		if (addressModel != null && addressModel.getDateOfBirth() != null)
		{

			return KlarnaDateFormatterUtil.getFormattedDateString(addressModel.getDateOfBirth(),
					KlarnaDateFormatterUtil.DATE_FORMAT_YEAR_PATTERN);
		}

		return StringUtils.EMPTY;
	}




	public void setCustomerAccountService(final CustomerAccountService customerAccountService)
	{
		this.customerAccountService = customerAccountService;
	}

	public void setCustomerFacade(final CustomerFacade customerFacade)
	{
		this.customerFacade = customerFacade;
	}

	public void setUserService(final UserService userService)
	{
		this.userService = userService;
	}

	public void setAddressConverter(final Converter<AddressModel, AddressData> addressConverter)
	{
		this.addressConverter = addressConverter;
	}


	public void setKpCustomerFacade(final KPCustomerFacade kpCustomerFacade)
	{
		this.kpCustomerFacade = kpCustomerFacade;
	}

	/**
	 * @return the kpCreditSessionInitialPopulator
	 */
	public KPCreditSessionInitialPopulator getKpCreditSessionInitialPopulator()
	{
		return kpCreditSessionInitialPopulator;
	}


	/**
	 * @param kpCreditSessionInitialPopulator
	 *           the kpCreditSessionInitialPopulator to set
	 */
	public void setKpCreditSessionInitialPopulator(final KPCreditSessionInitialPopulator kpCreditSessionInitialPopulator)
	{
		this.kpCreditSessionInitialPopulator = kpCreditSessionInitialPopulator;
	}

	/**
	 * @param kpConfigFacade
	 *           the kpConfigFacade to set
	 */
	public void setKpConfigFacade(final KPConfigFacade kpConfigFacade)
	{
		this.kpConfigFacade = kpConfigFacade;
	}

	/**
	 * @param modelService
	 *           the modelService to set
	 */
	public void setModelService(final ModelService modelService)
	{
		this.modelService = modelService;
	}

}
