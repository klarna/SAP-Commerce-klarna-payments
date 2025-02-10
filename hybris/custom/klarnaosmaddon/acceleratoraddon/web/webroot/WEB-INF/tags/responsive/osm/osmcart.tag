<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="price" required="true" type="java.lang.Double" %>


<c:if test="${osmConfigData.showInCartPage == true}">
<br>
<div class="kosm_prod">
	<fmt:formatNumber var="priceValue" type="number" groupingUsed="false" maxFractionDigits="0" value="${price*100}" />
	<klarna-placement id="osm-cart-strip" data-key="credit-promotion-auto-size" data-locale="${locale}-${osmCountry}" data-purchase-amount="${priceValue}" data-theme="${osmTheme}" ></klarna-placement>
	<div class="customStyle">
		${customStyleOSM}
	</div>
</div>
</c:if>