<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="klarnaaddress" tagdir="/WEB-INF/tags/addons/klarnapaymentb2baddon/responsive/address" %>
	<form:form modelAttribute="klarnaPaymentDetailsForm">
		<klarnaaddress:kpBillingAddressFormElements regions="${regions}"
		                             country="${country}" tabindex="12"/>
	</form:form>
