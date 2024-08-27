package com.oss.abraakadabraaapp.model

import com.google.gson.annotations.SerializedName

class ChatMetaDataModel(
    @SerializedName("chat_id" ) var chatId : String? = null,
    @SerializedName("data"    ) var data   : MainData?   = MainData()
)
data class ChatData (

    @SerializedName("date"                    ) var date                  : String?           = null,
    @SerializedName("check_warnings"          ) var checkWarnings         : Boolean?          = null,
    @SerializedName("product"                 ) var product               : String?           = null,
    @SerializedName("is_active"               ) var isActive              : Boolean?          = null,
    @SerializedName("product_image"           ) var productImage          : String?           = null,
    @SerializedName("product_receiver"        ) var productReceiver       : String?           = null,
    @SerializedName("receiver_id"             ) var receiverId            : String?           = null,
    @SerializedName("warnings"                ) var warnings              : ArrayList<String> = arrayListOf(),
    @SerializedName("sender_name"             ) var senderName            : String?           = null,
    @SerializedName("chat_closed_reason"      ) var chatClosedReason      : String?           = null,
    @SerializedName("sender_avatar"           ) var senderAvatar          : String?           = null,
    @SerializedName("receiver_avatar"         ) var receiverAvatar        : String?           = null,
    @SerializedName("sender_id"               ) var senderId              : String?           = null,
    @SerializedName("chat_closed"             ) var chatClosed            : Boolean?          = null,
    @SerializedName("user_report_evaluations" ) var userReportEvaluations : ArrayList<String> = arrayListOf(),
    @SerializedName("requestId"               ) var requestId             : String?           = null,
    @SerializedName("product_id"              ) var productId             : String?           = null,
    @SerializedName("receiver_name"           ) var receiverName          : String?           = null,
    @SerializedName("from"                    ) var from                  : String?           = null,
    @SerializedName("product_giver"           ) var productGiver          : String?           = null,
    @SerializedName("status"                  ) var status                : String?           = null,
    @SerializedName("time_stamp"              ) var timeStamp             : TimeStamp?        = TimeStamp(),
    @SerializedName("last_message"            ) var lastMessage           : String?           = null

)

data class MainData (

    @SerializedName("chat_data"    ) var chatData    : ChatData?    = ChatData(),
    @SerializedName("product_data" ) var productData : ProductData? = ProductData(),
    @SerializedName("request_data" ) var requestData : RequestData? = RequestData()


)

data class ProductData (

    @SerializedName("images"             ) var images            : ArrayList<String>  = arrayListOf(),
    @SerializedName("cost_saving"        ) var costSaving        : Int?               = null,
    @SerializedName("description"        ) var description       : String?            = null,
    @SerializedName("type"               ) var type              : String?            = null,
    @SerializedName("condition"          ) var condition         : String?            = null,
    @SerializedName("location_name"      ) var locationName      : String?            = null,
    @SerializedName("price"              ) var price             : Int?               = null,
    @SerializedName("energy_saving"      ) var energySaving      : Int?               = null,
    @SerializedName("name"               ) var name              : String?            = null,
    @SerializedName("posted_by"          ) var postedBy          : String?            = null,
    @SerializedName("used_for"           ) var usedFor           : String?            = null,
    @SerializedName("is_review"          ) var isReview          : Boolean?           = null,
    @SerializedName("currency"           ) var currency          : String?            = null,
    @SerializedName("category"           ) var category          : String?            = null,
    @SerializedName("brand"              ) var brand             : String?            = null,
    @SerializedName("display_image"      ) var displayImage      : String?            = null,
    @SerializedName("status"             ) var status            : String?            = null,
    @SerializedName("is_active"          ) var isActive          : Boolean?           = null,

)

data class RequestData (

    @SerializedName("productId"   ) var productId   : String?      = null,
    @SerializedName("message"     ) var message     : String?      = null,
    @SerializedName("userId"      ) var userId      : String?      = null,
    @SerializedName("isDelivered" ) var isDelivered : Boolean?     = null,
    @SerializedName("isReceived" ) var isReceived : Boolean?     = null,
    @SerializedName("status"      ) var status      : String?      = null,

)