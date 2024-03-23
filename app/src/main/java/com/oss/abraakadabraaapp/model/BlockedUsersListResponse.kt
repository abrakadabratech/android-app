package com.oss.abraakadabraaapp.model

import com.google.gson.annotations.SerializedName

class BlockedUsersListResponse(
    val code: Long,
    val status: Long,
    val data: BlockedUserList,
)

data class BlockedUserList(
    val pageNumber: Long,
    val pageSize: Long,
    val blockedUsers: List<BlockedUser>,
)

data class BlockedUser(
    val uid: String,
    val name: String,
    @SerializedName("user_avatar")
    val userAvatar: String,
    val timestamp: String,
)
