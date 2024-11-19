ACC.klarnashopping = {
	bindAll: function (){
		this.sendShoppingData();
		
	},
	sendShoppingData: function(){
		window.mediator.subscribe('trackAddToCart', function notifyProfileTagAddToCart(data) {
		    if (data.productCode && data.quantity) {
		        try {
		            console.log("Add"+data.productCode,data.quantity,data.cartData,data);
		           ACC.klarnashopping.populateShoppingRequest(data.cartData,null,null,null);
		        } catch(err){
		            console.error(err);
		        }
		    }
		});

		window.mediator.subscribe('trackUpdateCart', function notifyProfileTagUpdateCart(data) {
		    if (data.productCode && data.initialCartQuantity && data.newCartQuantity) {
		        try {
					var currentCart	= $("#cartData").val();
		    		var primaryImageUrl	= $("#primaryImageUrl"+data.productCode).val();
		    		var entryProductUrl	= $("#entryProductUrl"+data.productCode).val();
					console.log("update"+data.productCode,data.newCartQuantity);
					ACC.klarnashopping.populateShoppingRequest(null,primaryImageUrl,entryProductUrl,currentCart);
		        } catch (err) {
		            console.error(err);
		        }
		    }
		});

		window.mediator.subscribe('trackRemoveFromCart', function notifyProfileTagRemoveFromCart(data) {
		    if (data.productCode) {
		        try {
					console.log("remove"+data.productCode);
					var currentCart	= $("#cartData").val();
		    		var primaryImageUrl	= $("#primaryImageUrl"+data.productCode).val();
		    		var entryProductUrl	= $("#entryProductUrl"+data.productCode).val();
					console.log("update"+data.productCode,data.newCartQuantity);
					ACC.klarnashopping.populateShoppingRequest(null,primaryImageUrl,entryProductUrl,currentCart);
		        } catch(err) {
		            console.error(err);
		        }
		    }
		});
	},
	populateShoppingRequest: function(newProduct, productCode, updatedQty, currentCart){
		
		let payload = 
		'{'+
			'"supplementary_purchase_data": {'
				'"content_type": "vnd.klarna.supplementary-data.v1",'+
				'"content": {'+
					'"acquiring_channel": "ECOMMERCE",'+
                    '"merchant_reference": "",'+
					'"line_items": [{'+
						'"name": '+newProduct.productName+' ,'+
						'"quantity": '+newProduct.quantity+' ,'+
						'"total_amount": '+newProduct.productPrice+','+
						'"total_tax_amount": 0,'+
						'"unit_price": '+newProduct.productPrice+','+
						'"product_url": "https://merchant.example/product/pencil-3270220049012.html",'+
						'"image_url": "https://merchant.example/images/pencil-3270220049012.png",'+
						'"product_identifier": '+newProduct.productCode+','+
						'"reference": '+newProduct.cartCode+','+
						'"shipping_reference": "shipping-recipient-1234"'+
					'}],'+
					'"shipping": [{'+
						'"recipient": {'+
						'"given_name": "John",'+
						'"family_name": "Doe",'+
						'"email": "john.doe@klarna.com",'+
						'"phone": "844-552-7621",'+
						'"attention": "string"'+
						'},'+
						'"shipping_reference": "DHL-123456"'+
					'}]'+
				'}'+
			'}'+
		'}';
	}
}
$(document).ready(function ()
{
	ACC.klarnashopping.bindAll();
});
