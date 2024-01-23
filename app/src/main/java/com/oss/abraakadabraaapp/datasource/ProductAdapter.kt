package com.oss.abraakadabraaapp.datasource

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.datasource.products.Product
import java.util.Locale

class ProductAdapter(val onClick: OnProductClicked) :
    PagingDataAdapter<Product, RecyclerView.ViewHolder>(ProductDifferntiator) {

    private val ADS_AFTER: Int = 16 //frequency of ads in list
    private val ITEM_VIEW: Int = R.layout.item_product //regular item view layout
    private val AD_VIEW: Int = R.layout.list_ad  //ad view layout

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tv = itemView.rootView.findViewById<TextView>(R.id.tv_product_name)
        var tv_product_distance = itemView.rootView.findViewById<TextView>(R.id.tv_product_distance)
        var tv_product_location = itemView.rootView.findViewById<TextView>(R.id.tv_product_location)
        var iv = itemView.rootView.findViewById<ImageView>(R.id.iv_product)
        var my_product: ConstraintLayout =
            itemView.rootView.findViewById<ConstraintLayout>(R.id.iv_given)

        fun bind(item: Product?) {
            tv.text =
                item?.name?.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            Glide.with(itemView.context).load(item?.display_image)
                .placeholder(R.drawable.image_placeholder).into(iv)
            tv_product_distance.setText("${(item?.distance?.div(1000))} KM")
            tv_product_location.setText("${item?.condition}")

            if (item!!.isSelfProduct) {
                my_product.visibility = View.VISIBLE
                iv.background =
                    ContextCompat.getDrawable(itemView.context, R.color.transparent_blur)
            } else {
                my_product.visibility = View.GONE
            }

        }
    }

    /* override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         holder.bind(getItem(position))
         holder.itemView.setOnClickListener {
             Log.d("NewReceiverFragment", "bind: ${getItem(position)?.name}")
             onClick.onProductClicked(getItem(position),position)
         }
     }*/

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if(getItemViewType(position) == AD_VIEW){
            val qholder = holder as NativeAdViewHolder
            qholder.loadAds()
            return
        }
        val item = getItem(position)
        val qholder = holder as ViewHolder
        qholder.bind(item) // whatever you likes
        qholder.itemView.setOnClickListener {
            Log.d("NewReceiverFragment", "bind: ${getItem(position)?.name}")
            onClick.onProductClicked(getItem(position), position)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return /*ViewHolder(LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_product, parent, false))*/ when (viewType) {
            AD_VIEW -> NativeAdViewHolder(
                LayoutInflater.from(parent.context).inflate(AD_VIEW, parent, false)
            )

            ITEM_VIEW -> ViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_product, parent, false)
            )

            else -> ViewHolder(
                LayoutInflater
                    .from(parent.context)
                    .inflate(R.layout.item_product, parent, false)
            )
        }
    }

    companion object ProductDifferntiator : DiffUtil.ItemCallback<Product>() {

        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    interface OnProductClicked {
        fun onProductClicked(product: Product?, position: Int)
    }

    override fun getItemViewType(position: Int): Int {
        return if (isAdPosition(position)) AD_VIEW else ITEM_VIEW
    }

    private fun isAdPosition(position: Int): Boolean {
        return position > 0 && (position + 1) % ADS_AFTER == 0
    }
   /* override fun getItemCount(): Int {
        //disclaimer: copied from the stackoverflow link below but its working fine for me
        val s = super.getItemCount()
        var t: Int = s + s / ADS_AFTER
        if (s > 0) t++ // +1 when list is not empty
        // Utils.showLog("☺☺☺ total is $t, real are $s")
        return t
    }*/

    class NativeAdViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {

            loadAds()

        }

        fun loadAds() {
            val adLoader = AdLoader.Builder(itemView.context, "ca-app-pub-6795450348346297/5355595380")
                .forNativeAd {
                    val adView = itemView as NativeAdView
                    populateUnifiedNativeAdView(it, adView)
                }
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        // Methods in the NativeAdOptions.Builder class can be
                        // used here to specify individual options settings.
                        .build()
                )
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }

        private fun populateUnifiedNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
            // Set the media view.
            adView.mediaView = adView.findViewById(R.id.ad_media)

            // Set other ad assets.
            adView.headlineView = adView.findViewById(R.id.ad_headline)
            adView.bodyView = adView.findViewById(R.id.ad_body)
            adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
            adView.iconView = adView.findViewById(R.id.ad_app_icon)
            adView.priceView = adView.findViewById(R.id.ad_price)
            adView.starRatingView = adView.findViewById(R.id.ad_stars)
            adView.storeView = adView.findViewById(R.id.ad_store)
            adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

            // The headline and media content are guaranteed to be in every UnifiedNativeAd.
            (adView.headlineView as TextView).text = nativeAd.headline
            adView.mediaView?.setMediaContent(nativeAd.mediaContent)

            // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
            // check before trying to display them.
            if (nativeAd.body == null) {
                adView.bodyView?.visibility = View.INVISIBLE
            } else {
                adView.bodyView?.visibility = View.VISIBLE
                (adView.bodyView as TextView).text = nativeAd.body
            }

            if (nativeAd.callToAction == null) {
                adView.callToActionView?.visibility = View.INVISIBLE
            } else {
                adView.callToActionView?.visibility = View.VISIBLE
                (adView.callToActionView as Button).text = nativeAd.callToAction
            }

            if (nativeAd.icon == null) {
                adView.iconView?.visibility = View.GONE
            } else {
                (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon!!.drawable
                )
                adView.iconView?.visibility = View.VISIBLE
            }

            if (nativeAd.price == null) {
                adView.priceView?.visibility = View.INVISIBLE
            } else {
                adView.priceView?.visibility = View.VISIBLE
                (adView.priceView as TextView).text = nativeAd.price
            }

            if (nativeAd.store == null) {
                adView.storeView?.visibility = View.INVISIBLE
            } else {
                adView.storeView?.visibility = View.VISIBLE
                (adView.storeView as TextView).text = nativeAd.store
            }

            if (nativeAd.starRating == null) {
                adView.starRatingView?.visibility = View.INVISIBLE
            } else {
                (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
                adView.starRatingView?.visibility = View.VISIBLE
            }

            if (nativeAd.advertiser == null) {
                adView.advertiserView?.visibility = View.INVISIBLE
            } else {
                (adView.advertiserView as TextView).text = nativeAd.advertiser
                adView.advertiserView?.visibility = View.VISIBLE
            }

            // This method tells the Google Mobile Ads SDK that you have finished populating your
            // native ad view with this native ad.
            adView.setNativeAd(nativeAd)
        }

    }
}