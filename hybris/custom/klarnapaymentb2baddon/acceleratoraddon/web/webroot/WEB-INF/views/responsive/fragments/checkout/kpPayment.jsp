<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %> 
<%@ taglib prefix="address" tagdir="/WEB-INF/tags/responsive/address" %>
<%@ taglib prefix="klarnaaddress" tagdir="/WEB-INF/tags/addons/klarnapaymentb2baddon/responsive/address" %>

<spring:url value="/checkout/multi/klarna-payment-method/process" var="paymentProcess"/>
<c:if test="${is_klarna_active }">
<form:form action="${paymentProcess}" name="klarnaPaymentDetailsForm" id="klarnaPaymentDetailsForm" modelAttribute="klarnaPaymentDetailsForm" method="post">
 <hr/>
 <div class="headline">
     <spring:theme code="checkout.multi.paymentMethod.addPaymentDetails.billingAddress"/>
 </div>
 <c:if test="${cartData.deliveryItemsQuantity > 0}">
     <div id="kpUseDeliveryAddressData"
         data-titlecode="${deliveryAddress.titleCode}"
         data-firstname="${deliveryAddress.firstName}"
         data-lastname="${deliveryAddress.lastName}"
         data-line1="${deliveryAddress.line1}"
         data-line2="${deliveryAddress.line2}"
         data-town="${deliveryAddress.town}"
         data-postalcode="${deliveryAddress.postalCode}"
         data-countryisocode="${deliveryAddress.country.isocode}"
         data-regionisocode="${deliveryAddress.region.isocodeShort}"
         data-address-id="${deliveryAddress.id}"
     ></div>
     <formElement:formCheckbox
         path="useDeliveryAddress"
         idKey="kpUseDeliveryAddress"
         labelKey="checkout.multi.sop.useMyDeliveryAddress"
         tabindex="13"/>
 </c:if>
<input type="hidden" id="show_form" name="show_form" value="true"/>
 <klarnaaddress:kpBillAddressFormSelector supportedCountries="${countries}" regions="${regions}" tabindex="14"/>

 <p class="help-block"><spring:theme code="checkout.multi.paymentMethod.seeOrderSummaryForMoreInformation"/></p>							
<form:hidden path="paymentId" class="create_update_payment_id"/>							
</form:form>
</c:if>
<script>
ACC.klarnaaddress.selectUseDeliveryAddress();
</script>