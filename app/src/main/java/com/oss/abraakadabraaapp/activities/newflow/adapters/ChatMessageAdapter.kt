package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import java.util.ArrayList

class ChatMessageAdapter (val context: Context, var list: List<String>) :
    RecyclerView.Adapter<ChatMessageAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var message = itemView.findViewById<TextView>(R.id.message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.chat_message_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.message.text = list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setList(list1: ArrayList<String>) {
        this.list = list1
    }
}