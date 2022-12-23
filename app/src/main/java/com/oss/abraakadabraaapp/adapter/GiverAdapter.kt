package com.oss.abraakadabraaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ItemRequestGiverBinding
import com.oss.abraakadabraaapp.response.mainResponse.GiverData
import com.oss.abraakadabraaapp.utils.ImageUtils

class GiverAdapter(
    private val data: ArrayList<GiverData>,
    var context: Context,
    private var callback: GiverAdapterInterface
) : RecyclerView.Adapter<GiverAdapter.GiverAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiverAdapterVH {
        return GiverAdapterVH(
            LayoutInflater.from(context).inflate(R.layout.item_request_giver, parent, false)
        )
    }

    override fun onBindViewHolder(holder: GiverAdapterVH, position: Int) {

        val item = data[position]

        with(holder.binding) {
            tvProductTitle.text = item.title
            tvMessage.text = item.message
            tvTime.text = item.createdAt

            when (item.requestStatus) {
                0 -> {
                    ivOp.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.request_pending_op
                        )
                    )
                    ivCircle.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.request_pending_circle
                        )
                    )
                    image.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.request_pending
                        )
                    )
                    tvStatusText.text = context.resources.getText(R.string.pending)
                    tvStatusText.setTextColor(ContextCompat.getColor(context,R.color.request_pending_color))
                }
                1 -> {
                    ivOp.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.request_accept_op
                        )
                    )
                    ivCircle.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.request_accept_circle
                        )
                    )
                    image.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.request_accept
                        )
                    )
//                    tvStatusText.text = context.resources.getText(R.string.approved)
                    tvStatusText.text = context.resources.getText(R.string.accepted)
                    tvStatusText.setTextColor(ContextCompat.getColor(context,R.color.theme_color))
                }
                2 -> {
                    ivOp.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.request_reject_op
                        )
                    )
                    ivCircle.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.request_reject_circle
                        )
                    )
                    image.setImageDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.request_rejected
                        )
                    )
                    tvStatusText.text = context.resources.getText(R.string.rejected)
                    tvStatusText.setTextColor(ContextCompat.getColor(context,R.color.request_rejected_color))
                }
            }

            ImageUtils.setImage(
                context,
                ivProduct,
                item.image,
                progressBar,
                R.drawable.home_toolbar_app_logo
            )
        }

        holder.itemView.setOnClickListener {
            callback.onItemClick(item)
        }

    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    class GiverAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView){
        val binding = ItemRequestGiverBinding.bind(itemView)
    }

    interface GiverAdapterInterface {
        fun onItemClick(data: GiverData)
    }

}