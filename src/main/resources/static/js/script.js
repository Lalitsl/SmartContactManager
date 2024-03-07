console.log("welcome to js file ");

const toggleSidebar = () => {
	if ($(".sidebar").is(":visible")) {
		// true (band karna h )
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
		$(".sidebar").css("transition", "2s");
		$(".sidebar").css("transition-timing-function", "linear");

	} else {
		// false (show karna h )
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}

}

// search function

const search = () => {
	let query = $("#search_input").val();

	if (query == "") {
		$(".search_result").hide();
	} else {
		// sending request to server
		let url = `http://localhost:8081/search/${query}`;

		fetch(url).then((Response) => {
			return Response.json();
		}).then((data) => {
			// show data 
			/* console.log(data); */
			let text = `<div class='list-group'>`;
			data.forEach((contact) => {
				text += `<a href='/user/contact/${contact.cId}' class='list-group-item list-group-action'>${contact.name}</a>`;
			});
			text += `</div>`;
			$(".search_result").html(text);
			$(".search_result").show();
		});



	}
};


// coding for payment getway

// first request to server to create order
const paymentStart = () => {
	console.log("payment startted........");
	let amount = $("#paymentFeild").val();
	console.log(amount);
	if (amount == '' || amount == null) {
		Swal.fire({
			icon: "error",
			title: "Amount is required !!!",
		});
		return;
	}

	//    we will use ajax to send request to server to create order - using jquery
	$.ajax(
		{
			url: '/user/createOrder',
			data: JSON.stringify({ amount: amount, info: 'order_request' }),
			contentType: 'application/json',
			type: 'POST',
			dataType: 'json',
			success: function(Response) {
				// invoked when success
				console.log(Response);
				if (Response.status == 'created') {
					// open payment form 
					let options = {
						key: 'rzp_test_SjOHuxyf14nY18',
						amount: Response.amount,
						currency: 'INR',
						name: 'smart contact manager',
						description: "donation money for smart contact manager organization",
						image: 'https://image.pngaaa.com/505/1052505-middle.png',
						order_id: Response.id,
						handler: function(response) {
							console.log(response.razorpay_payment_id)
							console.log(response.razorpay_order_id)
							console.log(response.razorpay_signature)
							console.log("payment successfull ...")
							/*  payment success message code start  */
							Swal.fire({
								position: "top-end",
								icon: "success",
								title: "Congratulation...... payment success",
								/*showConfirmButton: false,*/
								timer: 3000
							});
							/*  payment success message code end  */

						},
						prefill: {
							"name": "",
							"email": "",
							"contact": ""
						},
						"notes": {
							"address": "SMART CONTACT MANAGER TEAM CREATE PROJECT FOR LEARNING PURPOSE."
						},
						"theme": {
							"color": "#3399cc"
						}
					};

					let rzp = new Razorpay(options);
					rzp.on('payment.failed', function(response) {
						console.log(response.error.code);
						console.log(response.error.description);
						console.log(response.error.source);
						console.log(response.error.step);
						console.log(response.error.reason);
						console.log(response.error.metadata.order_id);
						console.log(response.error.metadata.payment_id);
						/*  payment failed message code start */
						Swal.fire({
							icon: "error",
							title: "Payment Failed !!!",
							text: "Something went wrong!",
						});
						/*  payment failed message code end */
					});

					rzp.open()


				}
			},
			error: function(error) {
				// invoked when error 
				console.log(error);
				Swal.fire("Something went wrong !!! ");
			}
		}
	)




};
