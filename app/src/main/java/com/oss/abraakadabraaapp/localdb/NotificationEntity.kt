package com.oss.abraakadabraaapp.localdb

import com.google.firebase.Timestamp


data class NotificationEntity(

    val title: String = "",
    val body: String = "",
    val deleted:Boolean = false,
    val data:String = "",
    val userId:String = "",
    var module:String = "",
    var timestamp: Timestamp? = null,
    var image : String = "",
    var docId: String = ""

){ constructor():this("","",false,"","","",null,"","")
}
