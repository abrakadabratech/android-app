package com.oss.abraakadabraaapp.activities.newflow.chat

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.adapters.CommunityAdapter

class ChatAdapter(val context: Context, val i: Int) :
    RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.chat_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, ChatDetailActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
        return i
    }
}