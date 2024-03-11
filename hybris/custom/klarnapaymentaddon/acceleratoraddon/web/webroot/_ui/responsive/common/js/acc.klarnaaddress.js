ACC.klarnaaddress = {

	spinner: $("<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />"),

	bindUseDeliveryAddress: function ()
	{
		$('#kpUseDeliveryAddress').on('change', function ()
		{
			
			if ($('#kpUseDeliveryAddress').is(":checked"))
			{
				var options = {'countryIsoCode': $('#kpUseDeliveryAddressData').data('countryisocode'), 'useDeliveryAddress': true};
				ACC.klarnaaddress.enableAddressForm();
				ACC.klarnaaddress.displayKlarnaAddressForm(options, ACC.klarnaaddress.useDeliveryAddressSelected);
				ACC.klarnaaddress.disableAddressForm();
			}
			else
			{
				ACC.klarnaaddress.clearAddressForm();
				ACC.klarnaaddress.enableAddressForm();
			}
		});

		if ($('#kpUseDeliveryAddress').is(":checked"))
		{
			var options = {'countryIsoCode': $('#kpUseDeliveryAddressData').data('countryisocode'), 'useDeliveryAddress': true};
			ACC.klarnaaddress.enableAddressForm();
			ACC.klarnaaddress.displayKlarnaAddressForm(options, ACC.klarnaaddress.useDeliveryAddressSelected);
			ACC.klarnaaddress.disableAddressForm();
		}
	},

	/*bindSubmitSilentOrderPostForm: function ()
	{
		$('.submit_silentOrderPostForm').click(function ()
		{
			ACC.common.blockFormAndShowProcessingMessage($(this));
			$('.billingAddressForm').filter(":hidden").remove();
			ACC.klarnaaddress.enableAddressForm();
			$('#silentOrderPostForm').submit();
		});
	},*/

	bindCycleFocusEvent: function ()
	{
		$('#lastInTheForm').blur(function ()
		{
			$('#klarnaPaymentDetailsForm[tabindex$="10"]').focus();
		})
	},

	isEmpty: function (obj)
	{
		if (typeof obj == 'undefined' || obj === null || obj === '') return true;
		return false;
	},

	disableAddressForm: function ()
	{
		$('#klarnaPaymentDetailsForm input[id^="address\\."]').prop('disabled', true);
		$('#klarnaPaymentDetailsForm select[id^="address\\."]').prop('disabled', true);
	},

	enableAddressForm: function ()
	{
		$('#klarnaPaymentDetailsForm input[id^="address\\."]').prop('disabled', false);
		$('#klarnaPaymentDetailsForm select[id^="address\\."]').prop('disabled', false);
	},

	clearAddressForm: function ()
	{
		$('#klarnaPaymentDetailsForm input[id^="address\\."]').val("");
		$('#klarnaPaymentDetailsForm select[id^="address\\."]').val("");
	},

	useDeliveryAddressSelected: function ()
	{
		if ($('#kpUseDeliveryAddress').is(":checked"))
		{
			$('#klarnaPaymentDetailsForm  select[name=billTo_country]').val($('#kpUseDeliveryAddressData').data('countryisocode'));
			
			ACC.klarnaaddress.disableAddressForm();
		}
		else
		{
			ACC.klarnaaddress.clearAddressForm();
			ACC.klarnaaddress.enableAddressForm();
		}
		if ($("#klaranAuthError").val() == 'error') {
			$('#klarna_container').empty();
			var createSessionUrl = $("#createSessionUrl").val();
			$("#klaranAuthError").val('');
			ACC.klarnaPayment.createSession(createSessionUrl);
		}
	},
	
	

	bindKlarnaAddressForm: function ()
	{
		$('#kpBillingCountrySelector :input').on("change", function ()
		{
			var countrySelection = $(this).val();
			var options = {
				'countryIsoCode': countrySelection,
				'useDeliveryAddress': false
			};
			ACC.klarnaaddress.displayKlarnaAddressForm(options);
		})
	},

	displayKlarnaAddressForm: function (options, callback)
	{
		
		$.ajax({ 
			url: ACC.config.encodedContextPath + '/checkout/multi/klarna-payment-method/kpBillingaddressform',
			async: true,
			data: options,
			dataType: "html",
			beforeSend: function ()
			{
				$('#kpBillingAddressForm').html(ACC.klarnaaddress.spinner);
			}
		}).done(function (data)
				{
					$("#kpBillingAddressForm").html(data);
					if (typeof callback == 'function')
					{
						callback.call();
					}
					//ACC.klarnaaddress.bindDateOfbirth();
				});
		
	},
	bindDateOfbirth: function(e) {
		
		var expirationTimeWrapperElement = $("#js-dateofbirth");
		var dateFormatForDatePicker = expirationTimeWrapperElement.data("date-format-for-date-picker");
		
		var maxDate = new Date();
		
		$("#dateOfBirth").datepicker({
			dateFormat: dateFormatForDatePicker,
			maxDate: maxDate,
			changeMonth: true,
            changeYear: true,
            yearRange: '1930:-',
			
		}).attr('readonly', 'readonly');

		$(document).on("click", ".js-open-datepicker-quote-expiration-time", function() {
			$("#dateOfBirth").datepicker('show');
		});
	},
	selectUseDeliveryAddress: function(){
		ACC.klarnaaddress.bindUseDeliveryAddress();
		ACC.klarnaaddress.bindKlarnaAddressForm();
		var isKarnaFormError = $('input[type=hidden][id=isklarnaFormError]').val();
		if (isKarnaFormError ==null|| isKarnaFormError==undefined) {
			$("#kpUseDeliveryAddress").click();
		}
	}
}

$(document).ready(function ()
{
	with (ACC.klarnaaddress)
	{
		bindUseDeliveryAddress()
		bindKlarnaAddressForm();
	}
});
