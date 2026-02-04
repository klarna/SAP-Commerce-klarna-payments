<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${klarnaConfig != null and klarnaConfig.active eq true}">
	
	<div id="klarnaDiv"
    	data-products='<c:out value="${klarnaConfig.products}" />' 
    	data-integrator='<c:out value="${klarnaConfig.integrator}" />' 
    	data-originators='<c:out value="${klarnaConfig.originators}" />'>
	</div>
		
	<input type="hidden" id="klarnaClientId" name="klarnaClientId"	value="${klarnaConfig.credential.clientId}"/>
	
	<c:set var="loadWebSDKv1" value="false" />
	<c:set var="loadWebSDKv2" value="false" />
	
	<c:choose>
	    <c:when test="${klarnaConfig.osmConfig != null}">
	        <c:set var="loadWebSDKv2" value="true" />
	        <input type="hidden" id="klarnaProducts" value='["PAYMENT","MESSAGING"]' />
	    </c:when>
	    <c:otherwise>
	        <input type="hidden" id="klarnaProducts" value='["PAYMENT"]' />
	    </c:otherwise>
	</c:choose>	
	
	<c:if test="${klarnaConfig.kecConfig != null}">
		<c:choose>
		    <c:when test="${(klarnaConfig.kecConfig.oneStepCheckout eq true) or  (klarnaConfig.integratedWithPSP eq true)}">
		        <c:set var="loadWebSDKv2" value="true" />
		    </c:when>
		    <c:otherwise>
		        <c:set var="loadWebSDKv1" value="true" />
		    </c:otherwise>
		</c:choose>	
		<input type="hidden" class="text" name="kecButtonTheme" id="kecButtonTheme" value="${klarnaConfig.kecConfig.buttonTheme}" >
		<input type="hidden" class="text" name="kecButtonShape" id="kecButtonShape" value="${klarnaConfig.kecConfig.buttonShape}" >
		<input type="hidden" class="text" name="klarnaLocale" id="klarnaLocale" value="${klarnaLocale}" >
		<input type="hidden" name="integratedWithPSP" id="integratedWithPSP" value="${klarnaConfig.integratedWithPSP}"/>
		<spring:url value="/klarna/express-checkout/create-authorize-payload" var="expCheckoutAuthorizePayloadUrl"/>
		<input type="hidden" name="expCheckoutAuthorizePayloadUrl" id="expCheckoutAuthorizePayloadUrl" value="${expCheckoutAuthorizePayloadUrl}"/>
		<spring:url value="/klarna/express-checkout/process-authorize-response" var="expCheckoutProcessAuthorizeResponseUrl"/>
		<input type="hidden" name="expCheckoutProcessAuthorizeResponseUrl" id="expCheckoutProcessAuthorizeResponseUrl" value="${expCheckoutProcessAuthorizeResponseUrl}"/>
		<spring:message code="klarna.expcheckout.error" var="klarnaExpCheckoutErrorMessage"/>
		<input type="hidden" id="klarnaExpCheckoutErrorMessage" name="klarnaExpCheckoutErrorMessage" value="${klarnaExpCheckoutErrorMessage}"/>
		<spring:url value="/klarna/express-checkout/create-payment-request" var="expCheckoutPaymentRequestUrl"/>
		<input type="hidden" name="expCheckoutPaymentRequestUrl" id="expCheckoutPaymentRequestUrl" value="${expCheckoutPaymentRequestUrl}"/>
		<spring:url value="/klarna/express-checkout/update-shipping-address" var="kecUpdateShippingAddressUrl"/>
		<input type="hidden" name="kecUpdateShippingAddressUrl" id="kecUpdateShippingAddressUrl" value="${kecUpdateShippingAddressUrl}"/>
		<spring:url value="/klarna/express-checkout/update-shipping-method" var="kecUpdateShippingMethodUrl"/>
		<input type="hidden" name="kecUpdateShippingMethodUrl" id="kecUpdateShippingMethodUrl" value="${kecUpdateShippingMethodUrl}"/>
		<spring:url value="/klarna/express-checkout/on-payment-complete" var="kecOnPaymentCompleteUrl"/>
		<input type="hidden" name="kecOnPaymentCompleteUrl" id="kecSaveInteropTokenUrl" value="${kecOnPaymentCompleteUrl}"/>
	</c:if>
	
	<c:if test="${klarnaConfig.siwkConfig != null}">
		<c:set var="loadWebSDKv2" value="true" />
		<spring:url value="/klarna/signin/initiate" var="initiateSignInResponseUrl"/>
		<input type="hidden" id="initiateSignInResponseUrl" name="initiateSignInResponseUrl"  value="${initiateSignInResponseUrl}"/>
		<spring:url value="/klarna/signin/consent" var="userConsentPageURL"/>
		<input type="hidden" id="userConsentPageURL" name="userConsentPageURL"  value="${userConsentPageURL}"/>
		<spring:url value="/klarna/signin/login" var="loginURL"/>
		<input type="hidden" id="loginURL" name="loginURL"  value="${loginURL}"/>
		<input type="hidden" id="klarnaLocale"		name="klarnaLocale"		value="${klarnaLocale}" >
		<input type="hidden" id="klarnaEnv"			name="klarnaEnv"			value="${klarnaConfig.environment}"/>
		<input type="hidden" id="klarnaCountry"				name="klarnaCountry"				value="${klarnaCountry}"/>
		<input type="hidden" id="siwkScopeData"				name="siwkScopeData"			value="${klarnaConfig.siwkConfig.scopeData}"/>
		<input type="hidden" id="siwkRedirectUri"			name="siwkRedirectUri"			value="${klarnaConfig.siwkConfig.redirectUri}"/>
		<input type="hidden" id="siwkButtonTheme"			name="siwkButtonTheme"			value="${klarnaConfig.siwkConfig.buttonTheme}"/>
		<input type="hidden" id="siwkButtonShape"			name="siwkButtonShape"			value="${klarnaConfig.siwkConfig.buttonShape}"/>
		<input type="hidden" id="siwkButtonLogoAlignment"	name="siwkButtonLogoAlignment"	value="${klarnaConfig.siwkConfig.buttonLogoAlignment}"/>
		<input type="hidden" id="showSIWKInLoginPage"			name="showSIWKInLoginPage"			value="${klarnaConfig.siwkConfig.showInLoginPage}" />
		<input type="hidden" id="showSIWKInRegisterPage"		name="showSIWKInRegisterPage"		value="${klarnaConfig.siwkConfig.showInRegisterPage}" />
		<input type="hidden" id="showSIWKInCheckoutLoginPage"	name="showSIWKInCheckoutLoginPage"	value="${klarnaConfig.siwkConfig.showInCheckoutLoginPage}" />
	</c:if>
	
	<c:if test="${loadWebSDKv1}">
		<script defer src="${fn:escapeXml(klarnaWebSDKv1Url)" data-client-id="${klarnaClientId}"></script>
		<div id="loadWebSDKv1Div" data-enabled="${isEnabled}"></div>
	</c:if>
	
	<c:if test="${loadWebSDKv2}">
		<script defer src="${fn:escapeXml(klarnaWebSDKv2Url)" ></script>
		<div id="loadWebSDKv2Div" data-enabled="${isEnabled}"></div>
	</c:if>
	
</c:if>	
