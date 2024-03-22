<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>

<c:if test="${isKlarnaSignInEnabled}">
	<input type="hidden" id="currentLocale"			name="currentLocale"		value="${currentLocale}" >
	<input type="hidden" id="autoMergeAccounts"		name="autoMergeAccounts"	value="${klarnaSignInConfigData.autoMergeAccounts}"/>
	<input type="hidden" id="clientId"				name="clientId"				value="${klarnaSignInConfigData.clientId}"/>
	<input type="hidden" id="environment"			name="environment"			value="${klarnaSignInConfigData.environment}"/>
	<input type="hidden" id="country"				name="country"				value="${klarnaSignInConfigData.country}"/>
	<input type="hidden" id="scopeData"				name="scopeData"			value="${klarnaSignInConfigData.scopeData}"/>
	<input type="hidden" id="redirectUri"			name="redirectUri"			value="${klarnaSignInConfigData.redirectUri}"/>
	<input type="hidden" id="hideOverlay"			name="hideOverlay"			value="${klarnaSignInConfigData.hideOverlay}"/>
	<input type="hidden" id="buttonTheme"			name="buttonTheme"			value="${klarnaSignInConfigData.buttonTheme}"/>
	<input type="hidden" id="buttonShape"			name="buttonShape"			value="${klarnaSignInConfigData.buttonShape}"/>
	<input type="hidden" id="buttonLogoAlignment"	name="buttonLogoAlignment"	value="${klarnaSignInConfigData.buttonLogoAlignment}"/>
	
	<script src="${fn:escapeXml(klarnaSignInConfigData.scriptUrl)}" async></script> 
</c:if>