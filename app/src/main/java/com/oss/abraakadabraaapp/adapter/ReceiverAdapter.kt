package com.oss.abraakadabraaapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ItemRequestReceiverBinding
import com.oss.abraakadabraaapp.response.mainResponse.ReceiverData
import com.oss.abraakadabraaapp.utils.ImageUtils

class ReceiverAdapter(
    private val data: ArrayList<ReceiverData>,
    var context: Context,
    private var callback: TakerAdapterInterface
) : RecyclerView.Adapter<ReceiverAdapter.TakerAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TakerAdapterVH {
        return TakerAdapterVH(
            LayoutInflater.from(context).inflate(R.layout.item_request_receiver, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TakerAdapterVH, position: Int) {

        val item = data[position]

        with(holder.binding) {
            tvProductTitle.text = item.title
            tvMessage.text = item.message
            tvTime.text = item.requestAt

            ImageUtils.setImage(
                context,
                ivProduct,
                item.image,
                progressBar,
                R.drawable.home_toolbar_app_logo
            )

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

        }

        holder.itemView.setOnClickListener {
            callback.onItemClick(item,position)
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

    class TakerAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemRequestReceiverBinding.bind(itemView)
    }

    interface TakerAdapterInterface {
        fun onItemClick(data: ReceiverData, position:Int)
    }

}