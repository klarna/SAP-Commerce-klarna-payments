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
	<input type="hidden" id="processSigninURL" name="processSigninURL"  value="${processSigninURL}"/>
	
	<form:form id="syncAccountForm" action="${processSigninURL}" method="post" modelAttribute="klarnaSigninResponse">
		<div class="signin-container">
		
			<input type="hidden" id="profileStatus" name="profileStatus" value=""/>
			
			<div class="form-group display-flex">
			<spring:theme code="klarna.signin.userid" />
			<spring:theme code="address.phone" />
			<!-- address.surname address.firstName address.phone -->
				<label class="control-label">User Id</label>
				<form:input class="form-control signin-input" id="userId" type="text" path="${klarnaSigninResponse.userAccountProfile.userId}" disabled="disabled"/>
			</div>
			
			<div class="form-group display-flex">
				<label class="control-label">First Name</label>
				<form:input class="form-control signin-input" id="userId" type="text" path="${klarnaSigninResponse.userAccountProfile.givenName}" disabled="disabled"/>
			</div>
			
			<div class="form-group display-flex">
				<label class="control-label">Last Name</label>
				<form:input class="form-control signin-input" id="userId" type="text" path="${klarnaSigninResponse.userAccountProfile.familyName}" disabled="disabled"/>
			</div>
			
			<div class="form-group display-flex">
				<label class="control-label">Email</label>
				<form:input class="form-control signin-input" id="userId" type="text" path="${klarnaSigninResponse.userAccountProfile.email}" disabled="disabled"/>
			</div>
			
			<div class="form-group display-flex">
				<label class="control-label">Phone</label>
				<form:input class="form-control signin-input" id="userId" type="text" path="${klarnaSigninResponse.userAccountProfile.phone}" disabled="disabled"/>
			</div>
			<c:if test="${profileStatus eq 'CREATE_AFTER_CONSENT' }">
				<spring:url value="/klarna/signin/createNewCustomer" var="processSigninURL"/>
				<div class="form-group">
					<input id="mergeAccountsCheck" type="checkbox"/>
					<label>Would you like to Create a New Account?</label>
				</div>
				<div class="btn-ctr">
					<button id="syncDetails" class="signin-submit not-allowed" type="button" disabled="disabled" onclick="syncData();" >
					Register
					</button>
				</div>
			</c:if>
			<c:if test="${profileStatus eq 'MERGE_AFTER_CONSENT' }">
				<spring:url value="/klarna/signin/updateCustomer" var="processSigninURL"/>
				<div class="form-group">
					<input id="mergeAccountsCheck" type="checkbox"/>
					<label>Would you like to Sync the details?</label>
				</div>
				<button id="syncDetails" class="signin-submit not-allowed" type="button" disabled="disabled" onclick="syncData();" >
				Merge Accounts
				</button>
			</c:if>
			
		</div>
	</form:form>
</template:page>
<script>
window.onload = async function() {
	var mergeAccountsCheck		= document.getElementById('mergeAccountsCheck');
	mergeAccountsCheck.checked	= false;
	var syncDetail				= document.getElementById('syncDetails');
	mergeAccountsCheck.onchange = function() {
		if(this.checked){
			syncDetails.disabled = false;
			syncDetails.classList.remove("not-allowed");
		}else{
			syncDetails.disabled = true;
			syncDetails.classList.add("not-allowed");
		}
	};
}
function syncData(){
	let urlString 	= window.location.href;
	let paramString = urlString.split('?')[1];
	let queryString = new URLSearchParams(paramString);
	for (let keyValue of queryString.entries()) {
	   console.log("Key is: " + keyValue[0]);
	   console.log("Value is: " + keyValue[1]);
	   document.getElementById("profileStatus").value  = keyValue[1];
	}
	var syncAccountForm				= document.getElementById('syncAccountForm');
	syncAccountForm.submit();
}
</script>
<style>
.signin-input{width:100%;padding:12px 20px;margin:8px 0;display:inline-block;
border:1px solid #ccc;box-sizing:border-box;}
.signin-submit{width:50%;background-color:#47b6b1;color:white;padding:14px 20px;
margin:8px 0;border:none;cursor:pointer;}
.signin-container{padding:20px 0 0 32px;margin:auto;width:50%;}
.signin-table-50{width:50%;margin-left:auto;margin-right:auto;}
.signin-table-100{width:100%;margin-left:auto;margin-right:auto;}
.not-allowed{cursor:not-allowed !important;}
.btn-ctr{display:flex;justify-content:center;align-items:center;}
.display-flex{display:flex;flex-direction:column;}
</style>