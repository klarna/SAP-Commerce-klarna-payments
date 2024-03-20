<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%@ attribute name="price" required="true" type="java.lang.Double" %>

<c:set var="datainline" value="false"/>
<c:if test="${isDataInlineEnabled}">
	<c:set var="datainline" value="true"/>
</c:if>

<c:if test="${isCartEnabled}">
<br>
<div class="kosm_prod">
	<fmt:formatNumber var="priceValue" type="number" groupingUsed="false" maxFractionDigits="0" value="${price*100}" />
	<klarna-placement id="osm-cart-strip" data-key="${cartPlacementTagId}" data-locale="${locale}-${osmCountry}" data-purchase-amount="${priceValue}" data-theme="${cartTheme}" ></klarna-placement>
	<div class="customStyle">
		${customStyle}
	</div>
</div>
</c:if>