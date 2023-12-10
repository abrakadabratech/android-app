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
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import java.util.Locale

class CategorySelectAdapter(val context: Context, var i: ArrayList<UserCatData>,val onclick:OnCategoryClicked)
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
        if (i[position].isSelect) {
            holder.checkBox.setBackgroundResource(R.drawable.ic_check)
        } else {
            holder.checkBox.setBackgroundResource(R.drawable.ic_uncheck)
        }
        holder.itemView.setOnClickListener {
            onclick.onClick()
        }
        holder.cardName.text =
            i[position].title?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        holder.cardLayout.setOnClickListener {
            if (i[position].isSelect) {
                holder.checkBox.setBackgroundResource(R.drawable.ic_uncheck)
                i.get(position).isSelect = false
            } else {
                holder.checkBox.setBackgroundResource(R.drawable.ic_check)
                i.get(position).isSelect = true
            }
//            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return i.size
    }

    fun setList(list: java.util.ArrayList<UserCatData>) {
        i = list
    }

    interface OnCategoryClicked{
        fun onClick()
    }

}