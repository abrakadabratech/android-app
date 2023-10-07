package com.oss.abraakadabraaapp.localdb

import com.google.firebase.Timestamp
import com.oss.abraakadabraaapp.activities.newflow.model.NotificationDataModel


data class NotificationEntity(

    val title: String = "",
    val body: String = "",
    val deleted:Boolean = false,
    val data: NotificationDataModel? = null,
    val userId:String = "",
    var module:String = "",
    var timestamp: Timestamp? = null,
    var image: String = "",
    var docId: String = ""

){ constructor():this("","",false,null,"","",null,"","")
}
