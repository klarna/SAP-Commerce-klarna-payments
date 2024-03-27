<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="klarnapayment" tagdir="/WEB-INF/tags/addons/klarnapaymentaddon/responsive/" %>

<form:form action="${action}" method="post" modelAttribute="loginForm">
	<c:if test="${not empty KLARNA_SIGNIN_ERROR}">
		<span class="has-error"> <spring:theme code="${KLARNA_SIGNIN_ERROR}" />
		</span>
	</c:if>
	<form:label>User Id</form:label>
	<form:input id="userId" type="text" value="" path="${klarnaSigninResponse}" disabled="disabled"/>
	<form:label>First Name</form:label>
	<form:input id="givenName" type="text" value="" path="${klarnaSigninResponse}" disabled="disabled"/>
	<form:label>Last Name</form:label>
	<form:input id="familyName" type="text" value="" path="${klarnaSigninResponse}" disabled="disabled"/>
	<form:label>User Id</form:label>
	<form:input id="userId" type="text" value="" path="${klarnaSigninResponse}" disabled="disabled"/>
	<form:label>Email</form:label>
	<form:input id="email" type="text" value="" path="${klarnaSigninResponse}" disabled="disabled"/>
	<form:label>Phone</form:label>
	<form:input id="phone" type="text" value="" path="${klarnaSigninResponse}" disabled="disabled"/>
	<form:label>Would you like to Sync the details?</form:label>
	<form:input id="mergeAccountsCheck" type="checkbox" onchange="enableSubmit()"/>
	<button type="submit" class="btn btn-primary btn-block" disabled="disabled"></button>
</form:form>
<script>

</script>