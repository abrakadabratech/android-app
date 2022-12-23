package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.model.CatData

class CategorySelectAdapter(val context: Context, val i: ArrayList<CatData>)
    : RecyclerView.Adapter<CategorySelectAdapter.ViewHolder>() {

    class ViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView){
        var cardName = itemView.findViewById<TextView>(R.id.catName)
        var checkBox = itemView.findViewById<ImageView>(R.id.checkBox)
        var cardLayout = itemView.findViewById<ConstraintLayout>(R.id.cardLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.category_item_row,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.cardName.text = i[position].name
        holder.cardLayout.setOnClickListener {
            if (i[position].isSelect){
                holder.checkBox.setBackgroundResource(R.drawable.ic_uncheck)
                i.get(position).isSelect = false
            }else {
                holder.checkBox.setBackgroundResource(R.drawable.ic_check)
                i.get(position).isSelect = true
            }
//            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return i.size
    }

}