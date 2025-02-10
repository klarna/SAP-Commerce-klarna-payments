<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="klarnapayment" tagdir="/WEB-INF/tags/addons/klarnapaymentaddon/responsive/" %>

<template:page pageTitle="${pageTitle}">
	<div class="row">
		<div class="col-md-6">
			<cms:pageSlot position="BodyContent-klarna-signin" var="feature" element="div" class="login-left-content-slot">
				<cms:component component="${feature}"  element="div" class="login-left-content-component"/>
			</cms:pageSlot>
		</div>
	</div>
	<c:if test="${profileStatus eq 'CREATE_AFTER_CONSENT' }">
		<spring:url value="/klarna/signin/create-customer" var="processSigninURL"/>
	</c:if>
	<c:if test="${profileStatus eq 'MERGE_AFTER_CONSENT' }">
		<spring:url value="/klarna/signin/merge-account" var="processSigninURL"/>
	</c:if>
	<input type="hidden" id="processSigninURL" name="processSigninURL"  value="${processSigninURL}"/>
	
	<form:form id="syncAccountForm" action="${processSigninURL}" method="post" modelAttribute="klarnaSigninResponse">
		<div class="signin-container">
			
			<div class="form-group display-flex">
				<label class="control-label"><spring:theme code="klarna.signin.userid" /></label>
				<input class="form-control signin-input" id="userId" type="text" value="${klarnaSigninResponse.userAccountProfile.userId}" disabled="disabled"/>
			</div>
			
			<div class="form-group display-flex">
				<label class="control-label"><spring:theme code="address.firstName"/></label>
				<input class="form-control signin-input" id="givenName" type="text" value="${klarnaSigninResponse.userAccountProfile.givenName}" disabled="disabled"/>
			</div>
			
			<div class="form-group display-flex">
				<label class="control-label"><spring:theme code="address.surname"/></label>
				<input class="form-control signin-input" id="familyName" type="text" value="${klarnaSigninResponse.userAccountProfile.familyName}" disabled="disabled"/>
			</div>
			
			<div class="form-group display-flex">
				<label class="control-label"><spring:theme code="guest.email"/></label>
				<input class="form-control signin-input" id="email" type="text" value="${klarnaSigninResponse.userAccountProfile.email}" disabled="disabled"/>
			</div>
			
			<div class="form-group display-flex">
				<label class="control-label"><spring:theme code="address.phone" /></label>
				<input class="form-control signin-input" id="phone" type="text" value="${klarnaSigninResponse.userAccountProfile.phone}" disabled="disabled"/>
			</div>
			
			<c:if test="${profileStatus eq 'CREATE_AFTER_CONSENT' }">
				<spring:url value="/klarna/signin/create-customer" var="processSigninURL"/>
				<div class="form-group display-flex-plain">
					<input id="klarnaSignInAutoMerge" type="checkbox"/>
					<label><spring:theme code="klarna.signin.create.consent"/></label>
				</div>
				<div class="btn-ctr">
					<button id="klarnaSignInSubmit" class="signin-submit not-allowed" type="submit" disabled="disabled">
					<spring:theme code="klarna.signin.register"/>
					</button>
				</div>
			</c:if>
			<c:if test="${profileStatus eq 'MERGE_AFTER_CONSENT' }">
				<spring:url value="/klarna/signin/merge-account" var="processSigninURL"/>
				<div class="form-group display-flex-plain">
					<input id="klarnaSignInAutoMerge" type="checkbox"/>
					<label><spring:theme code="klarna.signin.merge.consent"/></label>
				</div>
				<div class="btn-ctr">
					<button id="klarnaSignInSubmit" class="signin-submit not-allowed" type="submit" disabled="disabled">
					<spring:theme code="klarna.signin.merge"/>
					</button>
				</div>
			</c:if>
			
		</div>
	</form:form>
</template:page>
<script>
window.onload = async function() {
  	var klarnaSignInAutoMerge		= document.getElementById('klarnaSignInAutoMerge');
	klarnaSignInAutoMerge.checked	= false;
	var klarnaSignInSubmit				= document.getElementById('klarnaSignInSubmit');
	klarnaSignInAutoMerge.onchange = function() {
		if(this.checked){
			klarnaSignInSubmit.disabled = false;
			klarnaSignInSubmit.classList.remove("not-allowed");
		}else{
			klarnaSignInSubmit.disabled = true;
			klarnaSignInSubmit.classList.add("not-allowed");
		}
	};
}
</script>
<style>
.signin-input{width:100%;padding:12px 20px;margin:8px 0;display:inline-block;
border:1px solid #ccc;box-sizing:border-box;}
.signin-submit{background-color:#47b6b1;color:white;padding:14px 20px;
margin:8px 0;border:none;cursor:pointer;}
.signin-container{padding:20px 0 0 32px;margin:auto;width:50%;}
.signin-table-50{width:50%;margin-left:auto;margin-right:auto;}
.signin-table-100{width:100%;margin-left:auto;margin-right:auto;}
.not-allowed{cursor:not-allowed !important;}
.btn-ctr{display:flex;justify-content:center;align-items:center;}
.display-flex{display:flex;flex-direction:column;}
.display-flex-plain{display:flex;}
</style>