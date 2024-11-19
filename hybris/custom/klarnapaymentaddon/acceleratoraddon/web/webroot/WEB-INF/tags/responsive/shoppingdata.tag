<%@ attribute name="product" required="true"
	type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ attribute name="cartData" required="true"
	type="de.hybris.platform.commercefacades.order.data.CartData"%>
<%@ attribute name="format" required="true" type="java.lang.String"%>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>


<c:if test="${product ne null}">
	<c:url var="entryProductUrl" value="${product.url}" />
	<c:set var="primaryImage"
		value="${ycommerce:productImage(product, format)}" />
	<c:if test="${not empty primaryImage}">
		<c:choose>
			<c:when
				test='${fn:startsWith(primaryImage.url, originalContextPath)}'>
				<c:url value="${primaryImage.url}" var="primaryImageUrl" context="/" />
			</c:when>
			<c:otherwise>
				<c:url value="${primaryImage.url}" var="primaryImageUrl"
					context="${originalContextPath}" />
			</c:otherwise>
		</c:choose>
	</c:if>
	<input type="hidden" id="primaryImageUrl${product.code}"
		name="primaryImageUrl${product.code}" value="${primaryImageUrl}" />
	<input type="hidden" id="entryProductUrl${product.code}"
		name="entryProductUrl${product.code}" value="${entryProductUrl}" />
</c:if>
<c:if test="${cartData ne null}">
	<input type="hidden" id="cartData" name="cartData" value="${cartData}" />
</c:if>
