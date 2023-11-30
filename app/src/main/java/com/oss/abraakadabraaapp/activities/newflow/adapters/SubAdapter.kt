package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatListModel
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Utility.toDate

class SubAdapter(val context: Context, val i: List<ChatListModel>,val currentUserId: String?) :
    RecyclerView.Adapter<SubAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName = itemView.findViewById<TextView>(R.id.userName)
        val cancelledTxt = itemView.findViewById<TextView>(R.id.cancelledTxt)
        val product = itemView.findViewById<TextView>(R.id.productName)
        val time = itemView.findViewById<TextView>(R.id.time)
        val message = itemView.findViewById<TextView>(R.id.message)
        val profilePic = itemView.findViewById<ImageView>(R.id.profilePic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (i[position].status == "cancelled") {
            holder.cancelledTxt.setTextColor(ContextCompat.getColor(context,R.color.status_declined))
        } else {
            holder.cancelledTxt.text = "Accepted"
            holder.cancelledTxt.setTextColor(ContextCompat.getColor(context,R.color.status_accepted))
        }
        if (currentUserId == i[position].product_giver){
            holder.userName.text = i[position].receiver_name
        }else{
            holder.userName.text = i[position].sender_name
        }
        holder.time.text = toDate(i[position].time_stamp!!)
        holder.product.text = i[position].product
        holder.message.text = i[position].last_message.toString()
        holder.itemView.setOnClickListener {
            val intent =
                Intent(context, ChatDetailActivity::class.java)
            intent.putExtra(Constants.CHATS_DATA, Gson().toJson(i[position]))
            intent.putExtra("data_from", "fragment")
            intent.putExtra(Constants.DISPLAY_NAME, i[position].receiver_name)
            intent.putExtra(
                Constants.DISPLAY_PIC,
                i[position].receiver_avatar
            )

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return i.size
    }

}