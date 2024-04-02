<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="klarnapayment" tagdir="/WEB-INF/tags/addons/klarnapaymentaddon/responsive/" %>

<spring:url value="/klarna/signin/process-signin" var="processSigninURL"/>
<input type="hidden" id="processSigninURL" name="processSigninURL"  value="${processSigninURL}"/>
<form:form id="syncAccountForm" action="${processSigninURL}" >
	<div class="signin-container">
		<input type="hidden" id="profileStatus" name="profileStatus" value=""/>
		<table class="signin-table-50">
		    <tr>
		        <td><label>User Id</label></td>
		        <td><input class="signin-input" id="userId" type="text" value="${klarnaSigninResponse.userAccountProfile.userId}" disabled="disabled"/></td>
		    </tr>
		    <tr>
		        <td><label>First Name</label></td>
		        <td><input class="signin-input" id="givenName" type="text" value="${klarnaSigninResponse.userAccountProfile.givenName}" disabled="disabled"/></td>
		    </tr>
		    <tr>
		        <td><label>Last Name</label></td>
		        <td><input class="signin-input" id="familyName" type="text" value="${klarnaSigninResponse.userAccountProfile.familyName}" disabled="disabled"/></td>
		    </tr>
		    <tr>
		        <td><label>Email</label></td>
		        <td><input class="signin-input" id="email" type="text" value="${klarnaSigninResponse.userAccountProfile.email}" disabled="disabled"/></td>
		    </tr>
		    <tr>
		        <td><label>Phone</label></td>
		        <td><input class="signin-input" id="phone" type="text" value="${klarnaSigninResponse.userAccountProfile.phone}" disabled="disabled"/></td>
		    </tr>
		    <tr>
		    	<td>
		    	<input id="mergeAccountsCheck" type="checkbox"/>
		    	<label>Would you like to Sync the details?</label>
		    	</td>
		    </tr>                                            
		</table>
		<div class="btn-ctr">
		<button id="syncDetails" class="signin-submit not-allowed" type="button" class="btn btn-primary btn-block" disabled="disabled" onclick="syncData();" >Merge Accounts</button>
		</div>
	</div>
</form:form>

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
border:1px solid #ccc;border-radius:4px;box-sizing:border-box;}
.signin-submit{width:50%;background-color:#000000;color:white;padding:14px 20px;
margin:8px 0;border:none;border-radius:4px;cursor:pointer;}
.signin-container{border-radius:5px;background-color:#f2f2f2;padding:20px;}
.signin-table-50{width:50%;margin-left:auto;margin-right:auto;}
.signin-table-100{width:100%;margin-left:auto;margin-right:auto;}
.not-allowed{cursor:not-allowed !important;}
.btn-ctr{display:flex;justify-content:center;align-items:center;}
</style>