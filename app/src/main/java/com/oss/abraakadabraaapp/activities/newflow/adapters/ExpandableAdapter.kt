package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.chat.GroupedChatListModel

class ExpandableAdapter(
    val context: Context,
    val i: List<GroupedChatListModel>, val currentUserId: String?
) :
    RecyclerView.Adapter<ExpandableAdapter.ViewHolder>() {
    private val TAG = "GivingChatsFragment"

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage = itemView.findViewById<ImageView>(R.id.profilePic)
        val arrow = itemView.findViewById<ImageView>(R.id.arrow)
        val product = itemView.findViewById<TextView>(R.id.userName)
        val postedByUser = itemView.findViewById<TextView>(R.id.postedByUser)
        val unreadCount = itemView.findViewById<TextView>(R.id.unreadCount)
        val sublist = itemView.findViewById<RecyclerView>(R.id.subRvChats)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.group_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.product.text = i[position].product_name
        holder.unreadCount.text = i[position].unread_messages.toString()
        Log.d(TAG, "onBindViewHolder: ${i[position].unread_messages}")
        Glide.with(context).load(i[position].product_url).into(holder.productImage)
        holder.postedByUser.text = "Posted By ${i[position].posted_by}"
        Log.e(TAG, "setUpRecyclerview: ${Gson().toJson(i[position].chats)}")
        holder.sublist.layoutManager = LinearLayoutManager(context)
        holder.sublist.adapter = SubAdapter(context,i[position].chats,currentUserId)

        if (i[position].isListShown){
            holder.sublist.visibility = View.VISIBLE
            holder.arrow.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.chat_up_arrow))
        }else{
            holder.sublist.visibility = View.GONE
            holder.arrow.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.chat_down_arrow))
        }

        holder.itemView.setOnClickListener {
            for(item in i.indices){
                if (item == position){
                    i[position].isListShown = !i[position].isListShown
                }
            }
            holder.sublist.layoutManager = LinearLayoutManager(context)
            holder.sublist.adapter = SubAdapter(context,i[position].chats,currentUserId)
            holder.sublist.visibility = View.VISIBLE
            notifyDataSetChanged()

        }
    }

    override fun getItemCount(): Int {
        return i.size
    }

}
