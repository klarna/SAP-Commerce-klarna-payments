<%@ attribute name="cartData" required="true" type="de.hybris.platform.commercefacades.order.data.CartData" %>
<%@ attribute name="paymentInfo" required="true" type="com.klarna.payment.data.KPPaymentInfoData" %>
<%@ attribute name="showPaymentInfo" required="false" type="java.lang.Boolean" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<c:choose>
	<c:when test="${is_klarna_exp_checkout}"> 
		<input type="hidden" id="isKlarnaExpCheckout" value="${is_klarna_exp_checkout}"/>
	</c:when>
	<c:otherwise>
		<script type="text/javascript" id="klarna-credit-lib-x">
		    /* <![CDATA[ */
		    (function(w,d) {
		        var url = 'https://x.klarnacdn.net/kp/lib/v1/api.js';
		        n = d.createElement('script');
		        c = d.getElementById('klarna-credit-lib-x');
		        n.async = !0;
		        n.src = url + '?' + (new Date()).getTime();
		        c.parentNode.replaceChild(n, c);
		    })(this,document);
		    /*]]>*/
		</script>
	</c:otherwise>			
</c:choose>

<spring:htmlEscape defaultHtmlEscape="true" />
<spring:url value="/klarna/payment/saveauth" var="saveAuth"/>
<spring:message code='checkout.error.terms.not.accepted' var="errorMessage"/>

<input type="hidden" value="${paymentInfo.finalizeRequired}" class="text" name="finalizeRequired" id="finalizeRequired">
<input type="hidden" value="${paymentInfo.paymentOption}" class="text" name="paymentOption" id="paymentOption">
<input type="hidden" id="clientToken" name="clientToken" value="${clientToken}"/>
<input type="hidden" id="errorMessage" name="errorMessage" value="${errorMessage}"/>

<input type="hidden" id="saveAuthUrl" value="${saveAuth}"/>
<c:if test="${not empty paymentInfo && showPaymentInfo}">
    <ul class="checkout-order-summary-list">
        <li class="checkout-order-summary-list-heading">
            <div class="title"><spring:theme code="checkout.multi.payment" text="Payment:" /></div>
            <div class="address">
                <c:if test="${not empty paymentInfo.billingAddress}"> ${fn:escapeXml(paymentInfo.billingAddress.title)}</c:if>
                <c:if test="${not empty paymentInfo.billingAddress}">${fn:escapeXml(paymentInfo.billingAddress.line1)}, <c:if test="${not empty paymentInfo.billingAddress.line2}">${fn:escapeXml(paymentInfo.billingAddress.line2)},</c:if>
                ${fn:escapeXml(paymentInfo.billingAddress.town)}, ${fn:escapeXml(paymentInfo.billingAddress.region.name)}&nbsp;${fn:escapeXml(paymentInfo.billingAddress.postalCode)}, ${fn:escapeXml(paymentInfo.billingAddress.country.name)}</c:if>
                <br/><c:if test="${not empty paymentInfo.billingAddress.phone }">${fn:escapeXml(paymentInfo.billingAddress.phone)}</c:if>
            </div>
        </li>
    </ul>
</c:if>

