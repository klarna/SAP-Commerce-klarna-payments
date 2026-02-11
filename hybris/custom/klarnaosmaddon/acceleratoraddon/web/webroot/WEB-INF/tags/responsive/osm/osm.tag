<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="price" required="true" type="java.lang.Double" %>

<c:if test="${osmConfigData != null}">
	<br>
	<fmt:formatNumber var="priceValue" type="number" groupingUsed="false" maxFractionDigits="0" value="${price*100}" />
	
	<input type="hidden" id="klarnaLocale"			name="klarnaLocale"		value="${klarnaLocale}" >
	<input type="hidden" id="datakey"				name="datakey"				value="credit-promotion-auto-size"/>
	<input type="hidden" id="purchaseAmount"			name="purchaseAmount"			value="${priceValue}"/>
	<input type="hidden" id="osmTheme"			name="osmTheme"			value="${osmTheme}"/>
	<input type="hidden" id="osmShowInPDP"			name="osmShowInPDP"			value="${osmConfigData.showInPDPPage}"/>
	<input type="hidden" id="osmShowInCartPage"			name="osmShowInCartPage"			value="${osmConfigData.showInCartPage}"/>
	
	<div id="kosm_div" class="kosm_div"></div>
	
	<div class="customStyle">
			${customStyleOSM}
	</div>
</c:if>
