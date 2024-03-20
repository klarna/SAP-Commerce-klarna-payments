<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %> 
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address" %>
<%@ taglib prefix="klarnaaddress" tagdir="/WEB-INF/tags/addons/klarnapaymentb2baddon/responsive/address" %>


<c:if test="${is_klarna_active}">
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
<spring:url value="/klarna/payment/session" var="createSession"/>
<spring:url value="/klarna/payment/session-update" var="updateSession"/>
<spring:url value="/klarna/payment/saveauth" var="saveAuth"/>
<spring:url value="/checkout/multi/klarna-payment-method/process" var="paymentProcess"/>
<spring:url value="/checkout/multi/klarna-payment-method/klarnaForm" var="klarnaForm"/>
<spring:url value="/klarna/payment/cancelauth" var="cancelAuth"/>

	<form:form>
		
		
		
		<c:choose>
		<c:when test="${klarnaFormError}">  
			<input type="hidden" id="isklarnaFormError"  value="${klarnaFormError}"/>
			<c:set var="isKlarnaSelected" value="true" />
			<c:set var="payId" value="${selected_payment}" />
		</c:when>
		<c:otherwise>
		<c:set var="isCardSelected" value="true" />
		</c:otherwise>
		
		</c:choose>
		<label class="kppayment-container"><input type="radio" name="paymentMethod" id="paymentMethod_card" value="card" ${isCardSelected ? 'checked="checked"' : ''}/> Card </label>
		<c:forEach var="paymentMethodCategory" items="${creditSessionData.paymentMethodCategories}" varStatus="loop">
			<label class="kppayment-container"><input type="radio" name="paymentMethod" id="paymentMethod_klarna_${loop.index}" value="${paymentMethodCategory.identifier}" ${isKlarnaSelected && paymentMethodCategory.identifier==payId ? 'checked="checked"' : ''}/> ${paymentMethodCategory.name} <img src="${paymentMethodCategory.assetUrls.standard}" alt="Klarna" title="Klarna"/></label>
		
		</c:forEach>
		<input type="hidden" id="createSessionUrl" value="${createSession}"/>
		<input type="hidden" id="clientToken"  value="${creditSessionData.clientToken}"/>
		<input type="hidden" id="updateSessionUrl"  value="${updateSession}"/>
		<input type="hidden" id="paymentProcessUrl" value="${paymentProcess}"/>
		<input type="hidden" id="saveAuthUrl" value="${saveAuth}"/>
		<input type="hidden" id="getKlarnaFormUrl" value="${klarnaForm}"/>
		<input type="hidden" id="paymentOption" value="${paymentOption}"/>
		<input type="hidden" id="klaranAuthError" value=""/>
		<input type="hidden" id="cancelAuthUrl" value="${cancelAuth}"/>
	
	</form:form>

	<div id="klarna_container"></div>
	<div id="klarna_billing"></div> 
</c:if>