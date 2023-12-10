package com.oss.abraakadabraaapp.activities.newflow.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.model.CatData
import java.util.Locale

class ConditionDialogAdapter(
val context: Context, var i: ArrayList<CatData>,
private var callback: ConditionAdapterInterface
) : RecyclerView.Adapter<ConditionDialogAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var cardName = itemView.findViewById<TextView>(R.id.catName)
        var checkBox = itemView.findViewById<ImageView>(R.id.checkBox)
        var cardLayout = itemView.findViewById<ConstraintLayout>(R.id.cardLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.category_dialog_item_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.cardName.text =
            i[position].name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        if (i[position].isSelect) {
            holder.checkBox.setBackgroundResource(R.drawable.ic_radio_select)
            holder.cardName.setTextColor(context.resources.getColor(R.color.cat_select_color))
            i.get(position).isSelect = true
        } else {
            holder.checkBox.setBackgroundResource(R.drawable.ic_radio_unselect)
            holder.cardName.setTextColor(context.resources.getColor(R.color.cat_unselect_color))
            i.get(position).isSelect = false
        }
        holder.cardLayout.setOnClickListener {
            callback.onConditionItemClick(position, !i[position].isSelect)
//            notifyDataSetChanged()
        }

    }

    override fun getItemCount(): Int {
        return i.size
    }

    interface ConditionAdapterInterface {
        fun onConditionItemClick(position: Int,isSelect:Boolean)
    }


}