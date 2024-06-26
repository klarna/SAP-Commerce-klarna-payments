/**
 *
 */
package com.klarnapayment.controllers.pages.checkout;

import de.hybris.platform.acceleratorstorefrontcommons.annotations.RequireHardLogIn;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.pages.AbstractPageController;
import de.hybris.platform.commercefacades.i18n.I18NFacade;
import de.hybris.platform.commercefacades.user.data.AddressData;
import de.hybris.platform.core.model.order.CartModel;
import de.hybris.platform.order.CartService;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.klarna.api.model.ApiException;
import com.klarna.api.payments.model.PaymentsSession;
import com.klarna.payment.facades.KPPaymentCheckoutFacade;
import com.klarna.payment.facades.KPPaymentFacade;
import com.klarna.payment.model.KPPaymentInfoModel;
import com.klarna.payment.util.KlarnaDateFormatterUtil;
import com.klarna.payment.util.LogHelper;
import com.klarnapayment.forms.KPAddressForm;


/**
 * @author hybris
 *
 */
@Controller
@RequestMapping(value = "/klarna/payment")
public class KPPaymentController extends AbstractPageController
{
	private static final Logger LOG = Logger.getLogger(KPPaymentController.class);

	@Resource(name = "kpPaymentFacade")
	private KPPaymentFacade kpPaymentFacade;
	@Resource(name = "kpPaymentCheckoutFacade")
	private KPPaymentCheckoutFacade kpPaymentCheckoutFacade;
	@Resource(name = "i18NFacade")
	private I18NFacade i18NFacade;
	@Resource(name = "cartService")
	private CartService cartService;


	@RequestMapping(value = "/session", method = RequestMethod.GET, produces = "application/json")

	@RequireHardLogIn

	@ResponseBody
	public String createSession(final HttpSession httpSession) throws ApiException, IOException
	{
		LogHelper.debugLog(LOG, "Entering create Session Call");
		httpSession.removeAttribute("sessionId");
		final PaymentsSession creditSessionData = kpPaymentFacade.getORcreateORUpdateSession(httpSession, null, false, false);

		httpSession.setAttribute("clientToken", creditSessionData.getClientToken());

		return creditSessionData.getClientToken();
	}


	@RequestMapping(value = "/session-update", method = RequestMethod.POST, produces = "application/json")
	@RequireHardLogIn
	@ResponseBody
	public PaymentsSession updateSession(@RequestBody
	final KPAddressForm billingAddress, final HttpSession httpSession) throws JsonProcessingException
	{

		LogHelper.debugLog(LOG, "Entering update Session Call");
		PaymentsSession creditSessionData = null;
		try
		{
			creditSessionData = kpPaymentFacade.getORcreateORUpdateSession(httpSession, getBillingAddressData(billingAddress), true,
					true);

		}
		catch (final ApiException | IOException ex)
		{
			LOG.error("Couldn't authorize klarnaPayment :: " + ex.getMessage());

		}
		return creditSessionData;
	}

	@RequestMapping(value = "/auth-callback", method = RequestMethod.POST, produces = "application/json")
	@RequireHardLogIn
	@ResponseBody
	public String authorizeCallback(@RequestParam("authorizationToken")
	final String authorizationToken, @RequestParam("paymentOption")
	final String paymentOption, @RequestParam("finalizeRequired")
	final Boolean finalizeRequired)
	{
		LogHelper.debugLog(LOG, "Going to save Authorization");
		kpPaymentCheckoutFacade.saveAuthorization(authorizationToken, paymentOption, finalizeRequired);

		//Create payment transaction
		kpPaymentFacade.createPaymentTransaction();
		return "success";
	}

	protected AddressData getBillingAddressData(final KPAddressForm billingAddress)
	{
		final AddressData addressData = new AddressData();

		addressData.setBillingAddress(true);
		addressData.setFirstName(billingAddress.getGivenName());
		addressData.setLastName(billingAddress.getFamilyName());
		if (StringUtils.isNotEmpty(billingAddress.getTitle()))
		{
			addressData.setTitleCode(billingAddress.getTitle());
		}
		addressData.setTown(billingAddress.getCity());

		if (StringUtils.isNotEmpty(billingAddress.getCountry()))
		{

			addressData.setCountry(i18NFacade.getCountryForIsocode(billingAddress.getCountry()));
			if (StringUtils.isNotEmpty(billingAddress.getRegion()))
			{
				final String regionIso = billingAddress.getCountry() + "-" + billingAddress.getRegion();
				addressData.setRegion(i18NFacade.getRegion(billingAddress.getCountry(), regionIso));
			}

		}

		addressData.setLine1(billingAddress.getStreetAddress());
		addressData.setStreetname(billingAddress.getStreetAddress());
		addressData.setLine2(billingAddress.getStreetAddress2());
		addressData.setStreetnumber(billingAddress.getStreetAddress2());
		if (StringUtils.isNotEmpty(billingAddress.getHouseExtension()))
		{
			addressData.setBuilding(billingAddress.getHouseExtension());
		}
		addressData.setEmail(billingAddress.getEmail());
		addressData.setPostalCode(billingAddress.getPostalCode());
		addressData.setPhone(billingAddress.getPhone());
		if (StringUtils.isNotEmpty(billingAddress.getDateOfBirth()))
		{
			addressData.setDateOfBirth(KlarnaDateFormatterUtil.getFormattedDate(billingAddress.getDateOfBirth(),
					KlarnaDateFormatterUtil.DATE_FORMAT_DATE_PATTERN));
		}
		return addressData;
	}

	@RequestMapping(value = "/saveauth", method = RequestMethod.POST, produces = "application/json")
	@RequireHardLogIn
	@ResponseBody
	public String saveAuthToken(@RequestParam("authorizationToken")
	final String authorizationToken, @RequestParam("paymentOption")
	final String paymentOption, @RequestParam("finalizeRequired")
	final Boolean finalizeRequired)
	{
		LogHelper.debugLog(LOG, "Going to save Authorization");
		kpPaymentCheckoutFacade.saveAuthorization(authorizationToken, paymentOption, finalizeRequired);

		//Create payment transaction
		kpPaymentFacade.createPaymentTransaction();
		return "success";
	}

	@RequestMapping(value = "/cancelauth", method = RequestMethod.POST, produces = "application/json")
	@RequireHardLogIn
	@ResponseBody
	public String cancelAuthorization()
	{
		try
		{
			LogHelper.debugLog(LOG, "Going to Cancel Authorization");
			CartModel cart = cartService.getSessionCart();
			if (cart.getPaymentInfo() instanceof KPPaymentInfoModel)
			{
				KPPaymentInfoModel kpPaymentInfo = (KPPaymentInfoModel) cart.getPaymentInfo();
				String authToken = kpPaymentInfo.getAuthToken();
				if (authToken != null || !authToken.equals(""))
				{
					//Cancel Klarna Authorzation
					kpPaymentFacade.getKlarnaDeleteAuthorization(authToken);
					//Delete klarna paymenttransaction
					kpPaymentFacade.deletePaymentTransaction();
				}
			}
		}
		catch (Exception e)
		{
			LOG.error(e.getMessage());
		}

		return "success";
	}




}
