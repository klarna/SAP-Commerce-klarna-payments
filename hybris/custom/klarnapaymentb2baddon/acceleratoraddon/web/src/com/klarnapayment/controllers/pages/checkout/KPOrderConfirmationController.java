/**
 *
 */
package com.klarnapayment.controllers.pages.checkout;

import de.hybris.platform.acceleratorfacades.order.AcceleratorCheckoutFacade;
import de.hybris.platform.acceleratorstorefrontcommons.controllers.util.GlobalMessages;
import de.hybris.platform.commercefacades.order.CartFacade;
import de.hybris.platform.commercefacades.order.data.CartData;
import de.hybris.platform.commercefacades.order.data.OrderData;
import de.hybris.platform.commerceservices.strategies.CheckoutCustomerStrategy;
import de.hybris.platform.order.InvalidCartException;
import de.hybris.platform.servicelayer.session.SessionService;

import java.io.IOException;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.klarnapayment.controllers.Klarnapaymentb2baddonControllerConstants;
import com.klarnapayment.utils.KlarnaPaymentHelper;


/**
 * @author hybris
 *
 */
@Controller
@RequestMapping(value = "/klarna/order")
public class KPOrderConfirmationController
{
	private static final Logger LOG = LoggerFactory.getLogger(KPOrderConfirmationController.class);
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

	public static final String SESSIONID = "sessionId";
	public static final String CLIENTTOKEN = "clientToken";

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
		LOG.debug("KPOrderConfirmationController.orderConfirmation() - kid={}", kid);

		if (!validateKid(kid, checkoutFacade.getCheckoutCart()))
		{
			LOG.warn("KID validation failed for kid={} — redirecting to cart", kid);
			kpPaymentCheckoutFacade.removePaymentInfo();
			httpSession.removeAttribute(SESSIONID);
			httpSession.removeAttribute(CLIENTTOKEN);
			GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
					"checkout.klarna.cart.unsynchronization", new String[] {});
			return REDIRECT_PREFIX + Klarnapaymentb2baddonControllerConstants.Views.Pages.Checkout.KP_REDIRECT_CART;
		}

		final String klarnaOrderId = kpOrderFacade.getKlarnaIdByOrderByKid(kid);
		LOG.debug("Fetching Klarna order for klarnaOrderId={}", klarnaOrderId);
		final OrderManagementOrdersApi klarnaOrder = kpPaymentFacade.getKlarnaOrderById();
		final OrderManagementOrder klarnaOrderData = klarnaOrder.fetch(klarnaOrderId);

		String hybrisOrderId = null;
		try
		{
			if (kpCustomerFacade.isAnonymousCheckout())
			{
				LOG.debug("Anonymous checkout — updating cart and anonymous cookie for klarnaOrderId={}", klarnaOrderId);
				kpPaymentCheckoutFacade.updateCart(klarnaOrderData);
				klarnaPaymentHelper.updateAnonymousCookie(kpPaymentCheckoutFacade.getUserGUID(), request, response);
			}
			if (!kpPaymentCheckoutFacade.isCartSynchronization(checkoutFacade.getCheckoutCart(), klarnaOrderData))
			{
				LOG.warn("Cart synchronization mismatch for klarnaOrderId={} — cancelling Klarna order and redirecting to cart",
						klarnaOrderId);
				kpPaymentCheckoutFacade.removePaymentInfo();
				klarnaOrder.cancelOrder(klarnaOrderId);
				httpSession.removeAttribute(SESSIONID);
				httpSession.removeAttribute(CLIENTTOKEN);
				GlobalMessages.addFlashMessage(redirectModel, GlobalMessages.CONF_MESSAGES_HOLDER,
						"checkout.klarna.cart.unsynchronization", new String[] {});
				return REDIRECT_PREFIX + Klarnapaymentb2baddonControllerConstants.Views.Pages.Checkout.KP_REDIRECT_CART;
			}

			LOG.debug("Updating payment info from Klarna order for klarnaOrderId={}", klarnaOrderId);
			kpPaymentCheckoutFacade.updatePaymentInfo(klarnaOrderData);

			hybrisOrderId = placeOrder(klarnaOrderId, klarnaOrder, redirectModel);
			LOG.info("Order placed successfully — klarnaOrderId={}, hybrisOrderId={}", klarnaOrderId, hybrisOrderId);
			httpSession.removeAttribute(SESSIONID);
			httpSession.removeAttribute(CLIENTTOKEN);
		}
		catch (Exception e)
		{
			LOG.error("Exception during order confirmation for klarnaOrderId={} — sending failure notification", klarnaOrderId, e);
			kpPaymentCheckoutFacade.sendFailedNotification(e.getMessage());
			httpSession.removeAttribute(SESSIONID);
			httpSession.removeAttribute(CLIENTTOKEN);
			throw e;
		}
		if (StringUtils.isEmpty(hybrisOrderId))
		{
			LOG.error("Place order returned no order ID for klarnaOrderId={} — redirecting to cart", klarnaOrderId);
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
		LOG.debug("placeOrder() - kpOrderId={}", kpOrderId);
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
			kpPaymentCheckoutFacade.doAutoCapture(orderData.getKpOrderId());
			//kpPaymentFacade.acknowledgeOrderNotify(kpOrderId, klarnaOrder, orderData);
		}
		catch (final InvalidCartException e)
		{
			LOG.error("InvalidCartException while placing order for kpOrderId={}", kpOrderId, e);
			redirectModel.addFlashAttribute("cartData", cloneCartData);
		}

		LOG.debug("Removing session cart after order placement for kpOrderId={}", kpOrderId);
		cartFacade.removeSessionCart();

		return hybrisOrderId;
	}

}
