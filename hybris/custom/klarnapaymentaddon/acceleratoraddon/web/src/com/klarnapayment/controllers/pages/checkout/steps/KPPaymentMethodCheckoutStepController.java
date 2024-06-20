/*
 * [y] hybris Platform
 *
 * Copyright (c) 2017 SAP SE or an SAP affiliate company.  All rights reserved.
 *
 * This software is the confidential and proprietary information of SAP
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with SAP.
 */
package com.klarnapayment.controllers.pages.checkout.steps;


import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateQuoteCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.forms.AddressForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.cms2.model.pages.ContentPageModel;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.commercefacades.user.data.CountryData;
import de.hybris.platform.yacceleratorstorefront.controllers.pages.checkout.steps.PaymentMethodCheckoutStepController;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.klarna.api.model.ApiException;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.data.KlarnaConfigData;
import com.klarna.payment.facades.KPConfigFacade;
import com.klarna.payment.facades.KPCustomerFacade;
import com.klarna.payment.facades.KPPaymentCheckoutFacade;
import com.klarna.payment.facades.KPPaymentFacade;
import com.klarna.payment.facades.KlarnaExpCheckoutFacade;
import com.klarna.payment.util.KlarnaDateFormatterUtil;
import com.klarna.payment.util.LogHelper;
import com.klarnapayment.controllers.KlarnapaymentaddonControllerConstants;
import com.klarnapayment.forms.KlarnaPaymentDetailsForm;


@Controller
@RequestMapping(value = "/checkout/multi/klarna-payment-method")
public class KPPaymentMethodCheckoutStepController extends PaymentMethodCheckoutStepController
{
	private static final Logger LOG = Logger.getLogger(KPPaymentMethodCheckoutStepController.class);
	private static final String PAYMENT_OPTION = "paymentOption";
	private static final String KLARNA_LOGO = "klarna_logo";
	private static final String KLARNA_DISPLAYNAME = "klarna_displayname";
	private static final String IS_KLARNA_ACTIVE = "is_klarna_active";
	private static final String CART_DATA_ATTR = "cartData";
	private static final String PAYMENT_METHOD = "payment-method";
	private static final String KLARNA_CREDITSESSIONDATA = "creditSessionData";
	private static final String PAYMENT_SELECTED = "selected_payment";
	private static final String KLARNA_FORM_ERROR = "klarnaFormError";
	private static final String KLARNA_FORM = "klarnaPaymentDetailsForm";
	private static final String IS_KLARNA_EXP_CHECKOUT_SESSION = "isKlarnaExpCheckoutSession";
	private static final String IS_KLARNA_EXP_CHECKOUT = "is_klarna_exp_checkout";
	private static final String CLIENT_TOKEN = "clientToken";


	@Resource(name = "kpPaymentCheckoutFacade")
	private KPPaymentCheckoutFacade kpPaymentCheckoutFacade;
	@Resource(name = "kpConfigFacade")
	private KPConfigFacade kpConfigFacade;
	@Resource(name = "kpCustomerFacade")
	private KPCustomerFacade kpCustomerFacade;
	@Resource(name = "acceleratorCheckoutFacade")
	private AcceleratorCheckoutFacade checkoutFacade;
	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;
	@Resource(name = "kpPaymentFacade")
	private KPPaymentFacade kpPaymentFacade;
	@Resource(name = "klarnaExpCheckoutFacade")
	private KlarnaExpCheckoutFacade klarnaExpCheckoutFacade;

	@Override
	@ModelAttribute("billingCountries")
	public Collection<CountryData> getBillingCountries()
	{
		return checkoutFacade.getBillingCountries();
	}


	@RequestMapping(value = "/process", method = RequestMethod.POST)
	@RequireHardLogIn
	public String process(final Model model, @Valid
	final KlarnaPaymentDetailsForm kpPaymentDetailsForm, final BindingResult bindingResult, final HttpSession httpSession,
			final RedirectAttributes redirectModel) throws CMSItemNotFoundException
	{
		LogHelper.debugLog(LOG, "Going to process the payment .. ");
		setupAddPaymentPage(model);

		if (BooleanUtils.isTrue(getSessionService().getAttribute(IS_KLARNA_EXP_CHECKOUT_SESSION)))
		{
			model.addAttribute(IS_KLARNA_EXP_CHECKOUT, Boolean.TRUE);
			LogHelper.debugLog(LOG, "This is a Klarna Express Checkout Session. Proceed to order review.");
		}
		else
		{
			redirectModel.addFlashAttribute(PAYMENT_SELECTED, kpPaymentDetailsForm.getPaymentId());

			AddressData addressData;

			if (kpPaymentDetailsForm.isUseDeliveryAddress())
			{
				LogHelper.debugLog(LOG, "using delivery address as payment address. ");
				addressData = getCheckoutFacade().getCheckoutCart().getDeliveryAddress();

				if (addressData == null)
				{
					GlobalMessages.addErrorMessage(model,
							"checkout.multi.paymentMethod.createSubscription.billingAddress.noneSelectedMsg");
					return KlarnapaymentaddonControllerConstants.Views.Pages.MultiStepCheckout.AddPaymentMethod;
				}

				if (addressData.getEmail() == null)
				{
					if (kpPaymentDetailsForm.getBillTo_email() != null)
					{
						addressData.setEmail(kpPaymentDetailsForm.getBillTo_email());
					}
					else
					{
						addressData.setEmail(kpCustomerFacade.getCurrentCustomerEmail());
					}
				}
				if (StringUtils.isNotEmpty(kpPaymentDetailsForm.getDateOfBirth()))
				{
					addressData.setDateOfBirth(KlarnaDateFormatterUtil.getFormattedDate(kpPaymentDetailsForm.getDateOfBirth(),
							KlarnaDateFormatterUtil.DATE_FORMAT_DATE_PATTERN));
				}
				addressData.setBillingAddress(true); // mark this as billing address
			}
			else
			{
				final String result = validateProcessError(kpPaymentDetailsForm, bindingResult, model, redirectModel);
				if (!StringUtils.isBlank(result))
				{
					return result;
				}

				addressData = new AddressData();
				fillInAddressData(addressData, kpPaymentDetailsForm);
			}
			getAddressVerificationFacade().verifyAddressData(addressData);
			LogHelper.debugLog(LOG, "Going for kpPaymentCheckoutFacade.processPayment ..  ");
			kpPaymentCheckoutFacade.processPayment(addressData, (String) httpSession.getAttribute("sessionId"));
		}

		setCheckoutStepLinksForModel(model, getCheckoutStep());
		CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute(CART_DATA_ATTR, cartData);
		model.addAttribute("test", "test");
		return getCheckoutStep().nextStep();
	}


	private String validateProcessError(final KlarnaPaymentDetailsForm kpPaymentDetailsForm, final BindingResult bindingResult,
			final Model model, final RedirectAttributes redirectModel)
	{

		if (bindingResult.hasErrors())
		{
			setupKlarnaPaymentPage(model, kpPaymentDetailsForm);
			//model.add
			final List<FieldError> errors = bindingResult.getFieldErrors();

			for (final FieldError error : errors)
			{
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, error.getDefaultMessage());
				LogHelper.debugLog(LOG, "error.getField() :: " + error.getField() + "   " + error.getDefaultMessage());

			}
			redirectModel.addFlashAttribute(KLARNA_FORM_ERROR, Boolean.TRUE);
			redirectModel.addFlashAttribute(KLARNA_FORM, kpPaymentDetailsForm);
			return getCheckoutStep().currentStep();
		}
		return null;

	}

	protected void fillInAddressData(final AddressData addressData, final KlarnaPaymentDetailsForm paymentDetailsForm)
	{
		if (paymentDetailsForm != null)
		{
			LogHelper.debugLog(LOG, "inside fill in address ");
			addressData.setFirstName(paymentDetailsForm.getBillTo_email());
			if (StringUtils.isNotEmpty(paymentDetailsForm.getBillTo_titleCode()))
			{
				addressData.setTitleCode(paymentDetailsForm.getBillTo_titleCode());
			}
			addressData.setFirstName(paymentDetailsForm.getBillTo_firstName());
			addressData.setLastName(paymentDetailsForm.getBillTo_lastName());
			addressData.setLine1(paymentDetailsForm.getBillTo_street1());
			addressData.setLine2(paymentDetailsForm.getBillTo_street2());
			addressData.setTown(paymentDetailsForm.getBillTo_city());
			addressData.setPostalCode(paymentDetailsForm.getBillTo_postalCode());
			addressData.setEmail(paymentDetailsForm.getBillTo_email());
			addressData.setStreetname(paymentDetailsForm.getBillTo_streetName());
			addressData.setStreetnumber(paymentDetailsForm.getBillTo_streetNumber());
			addressData.setBuilding(paymentDetailsForm.getBillTo_houseExtension());
			if (StringUtils.isNotEmpty(paymentDetailsForm.getDateOfBirth()))
			{
				addressData.setDateOfBirth(KlarnaDateFormatterUtil.getFormattedDate(paymentDetailsForm.getDateOfBirth(),
						KlarnaDateFormatterUtil.DATE_FORMAT_DATE_PATTERN));
			}
			if (StringUtils.isNotEmpty(paymentDetailsForm.getBillTo_country()))
			{
				addressData.setCountry(getI18NFacade().getCountryForIsocode(paymentDetailsForm.getBillTo_country()));
			}
			if (StringUtils.isNotEmpty(paymentDetailsForm.getBillTo_country())
					&& StringUtils.isNotEmpty(paymentDetailsForm.getBillTo_state()))
			{
				final String regionIso = paymentDetailsForm.getBillTo_country() + "-" + paymentDetailsForm.getBillTo_state();
				addressData.setRegion(getI18NFacade().getRegion(paymentDetailsForm.getBillTo_country(), regionIso));

			}
			addressData.setBillingAddress(true);
		}
	}


	@RequestMapping(value = "/back", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String back(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().previousStep();
	}

	@RequestMapping(value = "/next", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	public String next(final RedirectAttributes redirectAttributes)
	{
		return getCheckoutStep().nextStep();
	}



	@Override
	protected void setupAddPaymentPage(final Model model) throws CMSItemNotFoundException
	{
		model.addAttribute("metaRobots", "noindex,nofollow");
		model.addAttribute("hasNoPaymentInfo", Boolean.valueOf(getCheckoutFlowFacade().hasNoPaymentInfo()));
		prepareDataForPage(model);
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.paymentMethod.breadcrumb"));
		final ContentPageModel contentPage = getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL);
		storeCmsPageInModel(model, contentPage);
		setUpMetaDataForContentPage(model, contentPage);
		setCheckoutStepLinksForModel(model, getCheckoutStep());
	}


	@Override
	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(PAYMENT_METHOD);
	}

	//@Override
	@RequestMapping(value = "/payments", method = RequestMethod.GET)
	@RequireHardLogIn
	@PreValidateQuoteCheckoutStep
	@PreValidateCheckoutStep(checkoutStep = PAYMENT_METHOD)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes, final HttpSession httpSession)
			throws CMSItemNotFoundException
	{
		final String returnString = super.enterStep(model, redirectAttributes);
		final KlarnaConfigData klarnaConfig = kpConfigFacade.getKlarnaConfig();
		PaymentsSession creditSessionData = null;
		boolean isKlarnaExpCheckout = false;
		if (klarnaConfig != null && klarnaConfig.getActive().booleanValue())
		{
			// If it is Klarna Express Checkout session, don't create Klarna session
			if (BooleanUtils.isTrue(getSessionService().getAttribute(IS_KLARNA_EXP_CHECKOUT_SESSION))
					&& (kpPaymentCheckoutFacade.isKlarnaPayment()))
			{
				// If billing address is missing, copy delivery address
				if (kpPaymentCheckoutFacade.getKPPaymentInfo().getBillingAddress() == null)
				{
					klarnaExpCheckoutFacade.addBillingAddress(getCheckoutFacade().getCheckoutCart().getDeliveryAddress());
				}
				isKlarnaExpCheckout = true;
				model.addAttribute(IS_KLARNA_EXP_CHECKOUT, Boolean.TRUE);
				LogHelper.debugLog(LOG, "This is a Klarna Express Checkout Session.");
			}
			if (!isKlarnaExpCheckout)
			{
				LogHelper.debugLog(LOG, "setting klarna parameters");
				try
				{
					creditSessionData = kpPaymentFacade.getORcreateORUpdateSession(httpSession, null, false, false);
					if (creditSessionData != null && creditSessionData.getPaymentMethodCategories() != null
							&& !creditSessionData.getPaymentMethodCategories().isEmpty())
					{
						model.addAttribute(IS_KLARNA_ACTIVE, kpConfigFacade.getKlarnaConfig().getActive());
						model.addAttribute(KLARNA_CREDITSESSIONDATA, creditSessionData);
						//httpSession.setAttribute("sessionId", creditSessionData.getSessionId());
						httpSession.setAttribute("clientToken", creditSessionData.getClientToken());

					}
					else
					{
						LOG.error("Couldn't get Klarna payment method categories");
					}
				}
				catch (final ApiException | IOException ex)
				{
					LOG.error("Couldn't create the Session :: " + ex.getMessage());

				}
			}
		}
		else
		{
			model.addAttribute(IS_KLARNA_ACTIVE, Boolean.FALSE);
		}
		if (!isKlarnaExpCheckout)
		{
			getSessionService().removeAttribute(IS_KLARNA_EXP_CHECKOUT_SESSION);
			getSessionService().removeAttribute(CLIENT_TOKEN);
		}
		return returnString;

	}

	@RequestMapping(value = "/klarnaForm", method = RequestMethod.GET)
	public String getKlarnaForm(final Model model) throws CMSItemNotFoundException
	{
		LogHelper.debugLog(LOG, "getting the klarna payment JSP");
		setupAddPaymentPage(model);

		final KlarnaPaymentDetailsForm klarnaPaymentDetailsForm = new KlarnaPaymentDetailsForm();
		final AddressForm klarnaAddressForm = new AddressForm();
		klarnaPaymentDetailsForm.setBillingAddress(klarnaAddressForm);
		setupKlarnaPaymentPage(model, klarnaPaymentDetailsForm);

		return KlarnapaymentaddonControllerConstants.Views.Fragments.Checkout.KPPayment;
	}

	/**
	 * @param model
	 * @param klarnaPaymentDetailsForm
	 */
	private void setupKlarnaPaymentPage(final Model model, final KlarnaPaymentDetailsForm klarnaPaymentDetailsForm)
	{
		final CartData cartData = getCheckoutFacade().getCheckoutCart();
		model.addAttribute(CART_DATA_ATTR, cartData);
		model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());

		//model.addAttribute(PAYMENT_OPTION, kpConfigFacade.getPaymentOption());
		//model.addAttribute(KLARNA_LOGO, kpConfigFacade.getLogo());
		//model.addAttribute(KLARNA_DISPLAYNAME, kpConfigFacade.getDisplayName());
		model.addAttribute(IS_KLARNA_ACTIVE, kpConfigFacade.getKlarnaConfig().getActive());

		model.addAttribute(KLARNA_FORM, klarnaPaymentDetailsForm);

	}

	@RequestMapping(value = "/kpBillingaddressform", method = RequestMethod.GET)
	public String getCountryAddressForm(@RequestParam("countryIsoCode")
	final String countryIsoCode, @RequestParam("useDeliveryAddress")
	final boolean useDeliveryAddress, final Model model)
	{
		LogHelper.debugLog(LOG, "getting the kpBillingaddressform ... ");
		model.addAttribute("country", countryIsoCode);
		model.addAttribute("supportedCountries", checkoutFacade.getDeliveryCountries());
		model.addAttribute("regions", i18NFacade.getRegionsForCountryIso(countryIsoCode));
		model.addAttribute("countries", checkoutFacade.getDeliveryCountries());

		final KlarnaPaymentDetailsForm kpBillingaddressform = new KlarnaPaymentDetailsForm();
		model.addAttribute(KLARNA_FORM, kpBillingaddressform);
		if (useDeliveryAddress)
		{
			final AddressData deliveryAddress = checkoutFacade.getCheckoutCart().getDeliveryAddress();

			if (deliveryAddress.getRegion() != null && StringUtils.isNotEmpty(deliveryAddress.getRegion().getIsocode()))
			{
				kpBillingaddressform.setBillTo_state(deliveryAddress.getRegion().getIsocodeShort());
			}
			kpBillingaddressform.setBillTo_titleCode(deliveryAddress.getTitleCode());
			kpBillingaddressform.setBillTo_firstName(deliveryAddress.getFirstName());
			kpBillingaddressform.setBillTo_lastName(deliveryAddress.getLastName());
			kpBillingaddressform.setBillTo_street1(deliveryAddress.getLine1());
			kpBillingaddressform.setBillTo_street2(deliveryAddress.getLine2());
			kpBillingaddressform.setBillTo_city(deliveryAddress.getTown());
			kpBillingaddressform.setBillTo_postalCode(deliveryAddress.getPostalCode());
			kpBillingaddressform.setBillTo_country(deliveryAddress.getCountry().getIsocode());
			kpBillingaddressform.setBillTo_phone(deliveryAddress.getPhone());
			if (deliveryAddress.getEmail() != null)
			{
				kpBillingaddressform.setBillTo_email(deliveryAddress.getEmail());
			}
			else
			{
				kpBillingaddressform.setBillTo_email(kpCustomerFacade.getCurrentCustomerEmail());
			}
		}
		return KlarnapaymentaddonControllerConstants.Views.Fragments.Checkout.KPBillingAddressForm;
	}

}
