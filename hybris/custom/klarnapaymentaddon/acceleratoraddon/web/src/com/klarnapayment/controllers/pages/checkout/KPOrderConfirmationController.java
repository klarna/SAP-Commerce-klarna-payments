/**
 *
 */
package com.klarnapayment.controllers.pages.checkout;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.acceleratorstorefrontcommons.strategy.CartRestorationStrategy;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.order.CartService;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.klarna.api.model.ApiException;
import com.klarna.api.order_management.OrderManagementOrdersApi;
import com.klarna.api.order_management.model.OrderManagementOrder;
import com.klarna.payment.facades.KPCustomerFacade;
import com.klarna.payment.facades.KPOrderFacade;
import com.klarna.payment.facades.KPPaymentCheckoutFacade;
import com.klarna.payment.facades.KPPaymentFacade;
import com.klarnapayment.controllers.KlarnapaymentaddonControllerConstants;
import com.klarnapayment.utils.KlarnaPaymentHelper;


/**
 * @author hybris
 *
 */
@Controller
@RequestMapping(value = "/klarna/order")
public class KPOrderConfirmationController
{
	@Resource(name = "kpOrderFacade")
	private KPOrderFacade kpOrderFacade;
	@Resource(name = "acceleratorCheckoutFacade")
	private AcceleratorCheckoutFacade checkoutFacade;

	@Resource(name = "cartFacade")
	private CartFacade cartFacade;
	@Resource(name = "checkoutCustomerStrategy")
	private CheckoutCustomerStrategy checkoutCustomerStrategy;
	@Resource(name = "kpPaymentFacade")
	private KPPaymentFacade kpPaymentFacade;
	@Resource(name = "kpPaymentCheckoutFacade")
	private KPPaymentCheckoutFacade kpPaymentCheckoutFacade;

	@Resource(name = "sessionService")
	private SessionService sessionService;
	@Resource(name = "klarnaPaymentHelper")
	private KlarnaPaymentHelper klarnaPaymentHelper;
	@Resource(name = "kpCustomerFacade")
	private KPCustomerFacade kpCustomerFacade;

	@Resource(name = "cartService")
	private CartService cartService;

	@Resource(name = "cartRestorationStrategy")
	private CartRestorationStrategy cartRestorationStrategy;

	public static final String SESSIONID = "sessionId";
	public static final String CLIENTTOKEN = "clientToken";

	private static final String IS_KLARNA_EXP_CHECKOUT_SESSION = "isKlarnaExpCheckoutSession";
	private static final String CLIENT_TOKEN = "clientToken";

	Logger LOG = Logger.getLogger(KPOrderConfirmationController.class);

	/**
	 * @return the sessionService
	 */
	public SessionService getSessionService()
	{
		return sessionService;
	}

	/**
	 * @param sessionService
	 *           the sessionService to set
	 */
	public void setSessionService(final SessionService sessionService)
	{
		this.sessionService = sessionService;
	}

	public static final String REDIRECT_PREFIX = "redirect:";
	protected static final String REDIRECT_URL_CART = REDIRECT_PREFIX + "/cart";


	@RequestMapping(value = "/confirmation", method = RequestMethod.GET)
	public String orderConfirmation(@RequestParam(value = "kid")
	final String kid, final HttpSession httpSession, final RedirectAttributes redirectModel, final HttpServletRequest request,
			final HttpServletResponse response) throws ApiException, IOException
	{
		if (!validateKid(kid, checkoutFacade.getCheckoutCart()))
		{
			kpPaymentCheckoutFacade.removePaymentInfo();
			httpSession.removeAttribute(SESSIONID);
			httpSession.removeAttribute(CLIENTTOKEN);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					"checkout.klarna.cart.unsynchronization", new String[] {});
			return REDIRECT_PREFIX + KlarnapaymentaddonControllerConstants.Views.Pages.Checkout.KP_REDIRECT_CART;
		}
		final String klarnaOrderId = kpOrderFacade.getKlarnaIdByOrderByKid(kid);
		final OrderManagementOrdersApi klarnaOrder = kpPaymentFacade.getKlarnaOrderById();

		final OrderManagementOrder klarnaOrderData = klarnaOrder.fetch(klarnaOrderId);
		String hybrisOrderId = null;
		try
		{
			if (kpCustomerFacade.isAnonymousCheckout())
			{
				kpPaymentCheckoutFacade.updateCart(klarnaOrderData);
				klarnaPaymentHelper.updateAnonymousCookie(kpPaymentCheckoutFacade.getUserGUID(), request, response);
			}
			if (!kpPaymentCheckoutFacade.isCartSynchronization(checkoutFacade.getCheckoutCart(), klarnaOrderData))
			{
				kpPaymentCheckoutFacade.removePaymentInfo();
				klarnaOrder.cancelOrder(klarnaOrderId);
				httpSession.removeAttribute(SESSIONID);
				httpSession.removeAttribute(CLIENTTOKEN);
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
						"checkout.klarna.cart.unsynchronization", new String[] {});
				return REDIRECT_PREFIX + KlarnapaymentaddonControllerConstants.Views.Pages.Checkout.KP_REDIRECT_CART;
			}

			//update kp payment info in cart
			kpPaymentCheckoutFacade.updatePaymentInfo(klarnaOrderData);

			hybrisOrderId = placeOrder(klarnaOrderId, klarnaOrder, redirectModel);
			httpSession.removeAttribute(SESSIONID);
			httpSession.removeAttribute(CLIENTTOKEN);
			if(hybrisOrderId != null)
			{
				LOG.debug("Before  restore --- session cart details " + cartService.getSessionCart() + " cartService.hasSessionCart() "
						+ cartService.hasSessionCart());
				cartRestorationStrategy.restoreCart(request);
				LOG.debug("After  restore --- session cart details " + cartService.getSessionCart() + " cartService.hasSessionCart() "
						+ cartService.hasSessionCart());
			}
		}
		catch (Exception e)
		{
			// Send order placed error Email
			kpPaymentCheckoutFacade.sendFailedNotification(e.getMessage());
			httpSession.removeAttribute(SESSIONID);
			httpSession.removeAttribute(CLIENTTOKEN);
			throw e;
		}
		getSessionService().removeAttribute(IS_KLARNA_EXP_CHECKOUT_SESSION);
		getSessionService().removeAttribute(CLIENT_TOKEN);
		if (StringUtils.isEmpty(hybrisOrderId))
		{
			kpPaymentCheckoutFacade.sendFailedNotification("Place Order Failed");
			return REDIRECT_URL_CART;
		}
		return REDIRECT_PREFIX + "/checkout/orderConfirmation/" + hybrisOrderId;
	}

	/**
	 * @param kid
	 * @param checkoutCart
	 */
	private boolean validateKid(final String kid, final CartData checkoutCart)
	{
		return (checkoutCart.getCode().equals(StringUtils.substringAfter(kid, "_")));
	}

	@SuppressWarnings("unused")
	private String placeOrder(final String kpOrderId, final OrderManagementOrdersApi klarnaOrder,
			final RedirectAttributes redirectModel) throws ApiException, IOException
	{
		OrderData orderData = new OrderData();
		String hybrisOrderId = "";
		final CartData cloneCartData = checkoutFacade.getCheckoutCart();
		try
		{
			//Check Delete Authorization

			//			  CartModel cart = cartService.getSessionCart(); KPPaymentInfoModel kpPaymentInfo = (KPPaymentInfoModel)
			//			  cart.getPaymentInfo(); String authorizationToken = kpPaymentInfo.getAuthToken();
			//			  kpPaymentFacade.getKlarnaDeleteAuthorization(authorizationToken);


			orderData = checkoutFacade.placeOrder();
			if (checkoutCustomerStrategy.isAnonymousCheckout())
			{
				if (StringUtils.isNotBlank(orderData.getGuid()))
				{
					hybrisOrderId = orderData.getGuid();
				}
			}
			else
			{
				if (StringUtils.isNotBlank(orderData.getCode()))
				{
					hybrisOrderId = orderData.getCode();
				}
			}
			//kpPaymentCheckoutFacade.doAutoCapture(orderData.getKpOrderId());
			//kpPaymentFacade.acknowledgeOrderNotify(kpOrderId, klarnaOrder, orderData);
		}
		catch (final InvalidCartException e)
		{
			redirectModel.addFlashAttribute("cartData", cloneCartData);

		}

		cartFacade.removeSessionCart();

		return hybrisOrderId;
	}

}
