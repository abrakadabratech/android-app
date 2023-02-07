package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.chat.GiverChatModel

class ChatAdapter(val context: Context, val i: ArrayList<GiverChatModel>,val onclick: onChatClicked) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName = itemView.findViewById<TextView>(R.id.userName)
        val product = itemView.findViewById<TextView>(R.id.productName)
        val time = itemView.findViewById<TextView>(R.id.time)
        val message = itemView.findViewById<TextView>(R.id.message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.chat_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.userName.setText(i[position].receiverName)
        holder.time.setText("time")
        holder.product.setText(i[position].product)
        holder.message.setText("test message")

        holder.itemView.setOnClickListener {
            onclick.onChatClick(i[position])
//            context.startActivity(Intent(context, ChatDetailActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return i.size
    }

    interface onChatClicked{
        fun onChatClick(item: GiverChatModel)
    }
}