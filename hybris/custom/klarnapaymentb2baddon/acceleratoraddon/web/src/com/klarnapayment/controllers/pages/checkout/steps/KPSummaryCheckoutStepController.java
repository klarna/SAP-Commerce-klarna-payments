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

import de.hybris.platform.acceleratorservices.enums.CheckoutPciOptionEnum;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.PreValidateQuoteCheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.checkout.steps.CheckoutStep;
import de.hybris.platform.acceleratorstorefrontcommons.constants.WebConstants;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.checkout.steps.AbstractCheckoutStepController;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.b2bacceleratoraddon.forms.PlaceOrderForm;
import de.hybris.platform.cms2.exceptions.CMSItemNotFoundException;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.b2bacceleratorfacades.api.cart.CheckoutFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderEntryData;
import de.hybris.platform.commercefacades.product.ProductOption;
import de.hybris.platform.commercefacades.product.data.ProductData;
import de.hybris.platform.commerceservices.order.CommerceCartModificationException;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.payment.AdapterException;
import de.hybris.platform.b2bacceleratorfacades.order.data.B2BReplenishmentRecurrenceEnum;

import java.io.IOException;
import de.hybris.platform.cronjob.enums.DayOfWeek;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.klarna.api.model.ApiException;
import com.klarna.api.payments.model.PaymentsOrder;
import com.klarna.payment.data.KPPaymentInfoData;
import com.klarna.payment.enums.KlarnaFraudStatusEnum;
import com.klarna.payment.facades.KPPaymentCheckoutFacade;
import com.klarna.payment.facades.KPPaymentFacade;
import com.klarna.payment.services.KPPaymentInfoService;
import com.klarna.payment.util.LogHelper;
import com.klarnapayment.controllers.Klarnapaymentb2baddonControllerConstants;


@Controller
@RequestMapping(value = "/checkout/multi/summary/klarna")
public class KPSummaryCheckoutStepController extends AbstractCheckoutStepController
{
	private static final Logger LOG = Logger.getLogger(KPSummaryCheckoutStepController.class);

	private static final String SUMMARY = "summary";
	public static final String AUTHORIZATION_FAILED = "checkout.error.authorization.failed";

	@Resource(name = "kpPaymentCheckoutFacade")
	private KPPaymentCheckoutFacade kpPaymentCheckoutFacade;
	@Resource(name = "kpPaymentFacade")
	private KPPaymentFacade kpPaymentFacade;
	/*
	 * @Resource(name = "checkoutFacade") private CheckoutFacade checkoutFacade;
	 */
	@Resource(name = "checkoutCustomerStrategy")
	private CheckoutCustomerStrategy checkoutCustomerStrategy;
	@Resource(name = "cartFacade")
	private CartFacade cartFacade;
	@Resource(name = "cartService")
	private CartService cartService;
	@Resource(name = "kpPaymentInfoService")
	private KPPaymentInfoService kpPaymentInfoService;


	@RequestMapping(value = "/view", method = RequestMethod.GET)
	@RequireHardLogIn
	@Override
	@PreValidateQuoteCheckoutStep
	@PreValidateCheckoutStep(checkoutStep = SUMMARY)
	public String enterStep(final Model model, final RedirectAttributes redirectAttributes) throws CMSItemNotFoundException, // NOSONAR
			CommerceCartModificationException
	{
		LogHelper.debugLog(LOG, "inside KPSummaryCheckoutStepController. enter ");
		CartData cartData = getCheckoutFacade().getCheckoutCart();
		if (cartData.getEntries() != null && !cartData.getEntries().isEmpty())
		{
			for (final OrderEntryData entry : cartData.getEntries())
			{
				final String productCode = entry.getProduct().getCode();
				final ProductData product = getProductFacade().getProductForCodeAndOptions(productCode, Arrays.asList(
						ProductOption.BASIC, ProductOption.PRICE, ProductOption.VARIANT_MATRIX_BASE, ProductOption.PRICE_RANGE));
				entry.setProduct(product);
			}
		}
		KPPaymentInfoData kpPaymentInfo = kpPaymentCheckoutFacade.getKPPaymentInfo();
		cartData.setPaymentInfo(kpPaymentInfo);
		model.addAttribute("cartData", cartData);
		model.addAttribute("allItems", cartData.getEntries());
		model.addAttribute("deliveryAddress", cartData.getDeliveryAddress());
		model.addAttribute("deliveryMode", cartData.getDeliveryMode());
		model.addAttribute("paymentInfo", cartData.getKpPaymentInfo());
		
		model.addAttribute("nDays", getNumberRange(1, 30));
		model.addAttribute("nthDayOfMonth", getNumberRange(1, 31));
		model.addAttribute("nthWeek", getNumberRange(1, 12));
		model.addAttribute("daysOfWeek", getB2BCheckoutFacade().getDaysOfWeekForReplenishmentCheckoutSummary());

		// Only request the security code if the SubscriptionPciOption is set to Default.
		final boolean requestSecurityCode = CheckoutPciOptionEnum.DEFAULT
				.equals(getCheckoutFlowFacade().getSubscriptionPciOption());
		model.addAttribute("requestSecurityCode", Boolean.valueOf(requestSecurityCode));

		//model.addAttribute("placeOrderForm", new PlaceOrderForm());
		final PlaceOrderForm placeOrderForm = new PlaceOrderForm();
		placeOrderForm.setReplenishmentRecurrence(B2BReplenishmentRecurrenceEnum.MONTHLY);
		placeOrderForm.setnDays("14");
		final List<DayOfWeek> daysOfWeek = new ArrayList<DayOfWeek>();
		daysOfWeek.add(DayOfWeek.MONDAY);
		placeOrderForm.setnDaysOfWeek(daysOfWeek);
		model.addAttribute("placeOrderForm", placeOrderForm);

		storeCmsPageInModel(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		setUpMetaDataForContentPage(model, getContentPageForLabelOrId(MULTI_CHECKOUT_SUMMARY_CMS_PAGE_LABEL));
		model.addAttribute(WebConstants.BREADCRUMBS_KEY,
				getResourceBreadcrumbBuilder().getBreadcrumbs("checkout.multi.summary.breadcrumb"));
		model.addAttribute("metaRobots", "noindex,nofollow");
		setCheckoutStepLinksForModel(model, getCheckoutStep());
		return Klarnapaymentb2baddonControllerConstants.Views.Pages.MultiStepCheckout.CheckoutSummaryPage;
	}


	@RequestMapping(value = "/placeOrder")
	@PreValidateQuoteCheckoutStep
	@RequireHardLogIn
	public String placeOrder(@ModelAttribute("placeOrderForm")
	final PlaceOrderForm placeOrderForm, final Model model, final RedirectAttributes redirectModel, final HttpSession session)
			throws CMSItemNotFoundException, // NOSONAR
			InvalidCartException, CommerceCartModificationException, IOException

	{
		LogHelper.debugLog(LOG, "Entering Place Order");
		if (validateOrderForm(placeOrderForm, model))
		{
			return enterStep(model, redirectModel);
		}

		if (validateCart(redirectModel))
		{
			return REDIRECT_PREFIX + "/cart";
		}

		String token = kpPaymentFacade.getAuthToken();
		if (token.equals("1"))
		{
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER,
					"checkout.error.finalization.failed");
			return getCheckoutStep().previousStep();
		}
		if (token.equals("0"))
		{
			return getCheckoutStep().previousStep();
		}
		// authorize, if failure occurs don't allow to place the order
		boolean isPaymentUthorized = true;
		String redirectURL = "";
		try
		{
			LogHelper.debugLog(LOG, "Going for Authorization .. ");
			try
			{

				kpPaymentFacade.getKlarnaDeleteAuthorization(token);

				String sessionId = (String) session.getAttribute("sessionId");

				final PaymentsOrder authorizationResponse = kpPaymentFacade.getPaymentAuthorization(sessionId);

				if (KlarnaFraudStatusEnum.REJECTED.getValue().equalsIgnoreCase(authorizationResponse.getFraudStatus()))
				{
					isPaymentUthorized = false;
				}
				redirectURL = authorizationResponse.getRedirectUrl();

				kpPaymentCheckoutFacade.saveKlarnaOrderId(authorizationResponse);
				LogHelper.debugLog(LOG, " Saved Klaran Order ID .. ");
				// KLARNAPII-952 - moved payment transation creation to saveKlarnaOrderId method

			}
			catch (final ApiException ex)
			{
				LOG.error("Couldn't authorize klarnaPayment :: " + ex.getMessage());
				GlobalMessages.addErrorMessage(model, AUTHORIZATION_FAILED);
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.ERROR_MESSAGES_HOLDER, AUTHORIZATION_FAILED, new Object[]
				{ ex.getMessage() });
				return getCheckoutStep().previousStep();
			}


		}
		catch (final AdapterException ae)
		{
			// handle a case where a wrong paymentProvider configurations on the store see getCommerceCheckoutService().getPaymentProvider()
			LOG.error(ae.getMessage(), ae);
		}
		if (!isPaymentUthorized)
		{
			LogHelper.debugLog(LOG, " Payment Not authorized .. ");
			GlobalMessages.addErrorMessage(model, AUTHORIZATION_FAILED);

			return enterStep(model, redirectModel);
		}

		return REDIRECT_PREFIX + redirectURL;

	}


	/**
	 * Validates the order form before to filter out invalid order states
	 *
	 * @param model
	 *           A spring Model
	 * @return True if the order form is invalid and false if everything is valid.
	 */
	protected boolean validateOrderForm(final PlaceOrderForm placeOrderForm, final Model model)
	{
		LogHelper.debugLog(LOG, " Entering validateOrderForm .. ");
		final String securityCode = placeOrderForm.getSecurityCode();
		boolean invalid = false;

		if (getCheckoutFlowFacade().hasNoDeliveryAddress())
		{
			GlobalMessages.addErrorMessage(model, "checkout.deliveryAddress.notSelected");
			invalid = true;
		}
		if (getCheckoutFlowFacade().hasNoDeliveryMode())
		{
			GlobalMessages.addErrorMessage(model, "checkout.deliveryMethod.notSelected");
			invalid = true;
		}
		if (kpPaymentCheckoutFacade.hasNoPaymentInfo())
		{
			GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.notSelected");
			invalid = true;
		}
		else
		{
			// Only require the Security Code to be entered on the summary page if the SubscriptionPciOption is set to Default.
			if (CheckoutPciOptionEnum.DEFAULT.equals(getCheckoutFlowFacade().getSubscriptionPciOption())
					&& StringUtils.isBlank(securityCode))
			{
				GlobalMessages.addErrorMessage(model, "checkout.paymentMethod.noSecurityCode");
				invalid = true;
			}
		}

		final CartData cartData = getCheckoutFacade().getCheckoutCart();

		if (!getCheckoutFacade().containsTaxValues())
		{
			LOG.error(String.format(
					"Cart %s does not have any tax values, which means the tax cacluation was not properly done, placement of order can't continue",
					cartData.getCode()));
			GlobalMessages.addErrorMessage(model, "checkout.error.tax.missing");
			invalid = true;
		}

		if (!cartData.isCalculated())
		{
			LOG.error(
					String.format("Cart %s has a calculated flag of FALSE, placement of order can't continue", cartData.getCode()));
			GlobalMessages.addErrorMessage(model, "checkout.error.cart.notcalculated");
			invalid = true;
		}

		return invalid;
	}
	
	protected CheckoutFacade getB2BCheckoutFacade()
	{
		return (CheckoutFacade) this.getCheckoutFacade();
	}
	
	protected List<String> getNumberRange(final int startNumber, final int endNumber)
	{
		final List<String> numbers = new ArrayList<String>();
		for (int number = startNumber; number <= endNumber; number++)
		{
			numbers.add(String.valueOf(number));
		}
		return numbers;
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

	protected CheckoutStep getCheckoutStep()
	{
		return getCheckoutStep(SUMMARY);
	}


}
