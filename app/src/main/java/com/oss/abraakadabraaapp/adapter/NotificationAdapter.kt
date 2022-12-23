package com.oss.abraakadabraaapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.ProductDetailActivity
import com.oss.abraakadabraaapp.activities.RequestProductDetailActivity
import com.oss.abraakadabraaapp.databinding.ItemNotificationBinding
import com.oss.abraakadabraaapp.response.notificationResponse.NotificationResponse
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.ImageUtils


class NotificationAdapter(
    private val data: ArrayList<NotificationResponse.NotificationData>,
    var context: Context,
    private var callback: NotificationAdapterInterface
) : RecyclerView.Adapter<NotificationAdapter.NotificationAdapterVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationAdapterVH {
        return NotificationAdapterVH(
            LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NotificationAdapterVH, position: Int) {

        /*val item = data[position]

        with(holder.binding) {
//product id
            tvNotificationTitle.text = item.notificationTitle
            tvNotificationTime.text = item.createdAt

            if (item.userName.isNotEmpty()) {
                tvUserName.visibility = View.VISIBLE
                tvUserName.text = item.userName
            }

            //seenStatus 0-> unread 1-> read
            if (item.seenStatus == 1) {
                clMainContent.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            } else {
                clMainContent.setBackgroundColor(
                    ContextCompat.getColor(
                        context,
                        R.color.op_grey_28
                    )
                )
            }
            ImageUtils.setImage(
                context,
                ivImage,
                item.profileImage,
                null,
                R.drawable.home_toolbar_app_logo,
            )
        }

        holder.itemView.setOnClickListener {
            if(item.seenStatus != 1){
                callback.onItemClick(item,position)
            }else{
                when (item.module) {
                    Constants.pendingIntentRequest -> {
                        var intent = Intent(context, ProductDetailActivity::class.java)

                        if(item.role.toString() == Constants.giver){//role //1-> taker // 2->giver//22
                            intent = Intent(context, RequestProductDetailActivity::class.java)
                            intent.putExtra(Constants.productId,item.moduleId.toString())
                        }else{
                            intent.putExtra(Constants.productId, item.moduleData2.toString())//product id
                            intent.putExtra(Constants.titleStatus, item.moduleData.toString())
                        }
                        intent.putExtra(Constants.productStatus, item.moduleData.toString())
                        intent.putExtra(Constants.requestName, item.userName)
                        context.startActivity(intent)
                    }
                    Constants.productDetail -> {
                        val intent = Intent(context, ProductDetailActivity::class.java)
                        intent.putExtra(Constants.productId, item.moduleId.toString())
                        intent.putExtra(Constants.requestName, item.userName)
                        context.startActivity(intent)
                    }
                }
            }
        }*/

    }

    override fun getItemCount(): Int {
        return 4
    }

    override fun getItemViewType(position: Int): Int {
        return 1
    }

    class NotificationAdapterVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = ItemNotificationBinding.bind(itemView)
    }

    interface NotificationAdapterInterface {
        fun onItemClick(data:NotificationResponse.NotificationData,position:Int)
    }

}