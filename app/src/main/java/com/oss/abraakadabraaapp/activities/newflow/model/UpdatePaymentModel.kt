package com.oss.abraakadabraaapp.activities.newflow.model

import com.google.gson.annotations.SerializedName

data class UpdatePaymentModel(
    @SerializedName("code"             ) var code            : Int?    = null,
    @SerializedName("status"           ) var status          : Int?    = null,
    @SerializedName("data"             ) var data            : PaymentData?   = PaymentData()
)
data class PaymentData (
    @SerializedName("orderId"        ) var orderId       : String? = null,
    @SerializedName("transaction_id" ) var transactionId : String? = null,
    @SerializedName("payment_status" ) var paymentStatus : String? = null

)
