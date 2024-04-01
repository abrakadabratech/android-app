package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Context
import android.content.Intent
import android.graphics.Paint.Style
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.text.font.Typeface
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatListModel
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatModel
import com.oss.abraakadabraaapp.model.Chat
import com.oss.abraakadabraaapp.model.Product
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Utility.convertToTimestamp

class SubAdapter(
    val context: Context
) :
    PagingDataAdapter<Chat, SubAdapter.ViewHolder>(ProductDifferentiator) {
    private val TAG = "GivingChatsFragment"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName = itemView.findViewById<TextView>(R.id.userName)
        val cancelledTxt = itemView.findViewById<TextView>(R.id.cancelledTxt)
        val product = itemView.findViewById<TextView>(R.id.productName)
        val time = itemView.findViewById<TextView>(R.id.time)
        val message = itemView.findViewById<TextView>(R.id.message)
        val profilePic = itemView.findViewById<ImageView>(R.id.profilePic)
        val unreadCount = itemView.findViewById<TextView>(R.id.unreadCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)!!

        Glide.with(context).load(item.productImage).placeholder(R.drawable.ic_person).into(holder.profilePic)
        holder.userName.text = item.userName
        if (item.unseenMessages > 0){
            holder.unreadCount.visibility = View.VISIBLE
            holder.unreadCount.text = item.unseenMessages.toString()
        }else{
            holder.unreadCount.visibility = View.GONE
        }
        holder.message.text = item.lastMessage
//        getUnreadCount(holder.unreadCount,holder.message,chatNode[position])
       /* val item = getItem(position)!!
        if (i[position].status == "cancelled") {
            holder.cancelledTxt.setTextColor(ContextCompat.getColor(context,R.color.status_declined))
        } else {
            holder.cancelledTxt.text = "Accepted"
            holder.cancelledTxt.setTextColor(ContextCompat.getColor(context,R.color.status_accepted))
        }
        if (item.chatId == i[position].product_giver){
            holder.userName.text = i[position].receiver_name
        }else{
            holder.userName.text = i[position].sender_name
        }
        holder.time.text = convertToTimestamp(i[position].time_stamp)
        holder.product.text = i[position].product
        holder.message.text = i[position].last_message.toString()*/
        holder.itemView.setOnClickListener {
            val intent =
                Intent(context, ChatDetailActivity::class.java)
            intent.putExtra(Constants.CHATS_DATA, item.chatId)
//            intent.putExtra("data_from", "fragment")
//            intent.putExtra(Constants.DISPLAY_NAME, i[position].receiver_name)
//            intent.putExtra(
//                Constants.DISPLAY_PIC,
//                i[position].receiver_avatar
//            )
            context.startActivity(intent)
        }
    }

    companion object ProductDifferentiator : DiffUtil.ItemCallback<Chat>() {

        override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem.chatId == newItem.chatId
        }

        override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
            return oldItem == newItem
        }
    }

}