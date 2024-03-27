package com.oss.abraakadabraaapp.activities.newflow

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.databinding.ActivityUserProfileBinding
import com.oss.abraakadabraaapp.model.RecentProducts
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.DateTimeUtils
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class UserProfileActivity : BaseActivity() {

    private lateinit var binding:ActivityUserProfileBinding

    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpObserver()
        val user = intent.extras?.getString("UserProfile")
        authViewModel.viewProfile(user.toString())

    }

    private fun setUpObserver() {

        authViewModel.isLoading.observe(this) { loader(it) }

        authViewModel.userProfileSuccess.observe(this) {
            if (it.code == 200) {
                Log.d("TAG", "setUpObserver: $it")
                binding.username.text = it.data?.user?.name
                binding.totalProducts.text = "${it.data?.totalProductsPosted.toString()} Products Posted"
                binding.joinedAt.text = "Joined At ${DateTimeUtils.toDate(it.data?.user?.joinedAt!!)}"
                Glide.with(this).load(it.data?.user?.photo).placeholder(R.drawable.user).into(binding.profilePic2)
                binding.postedProductsRec.layoutManager = GridLayoutManager(this,2)
                binding.postedProductsRec.adapter = UserProductsAdapter(this,it.data?.recentProducts!!)
            }
        }
    }

    class UserProductsAdapter(val context: Context, val list:ArrayList<RecentProducts>) : RecyclerView.Adapter<UserProductsAdapter.ViewHolder>() {
        class ViewHolder(item: View):RecyclerView.ViewHolder(item) {
            val image = item.findViewById<ImageView>(R.id.iv_product)
            val name = item.findViewById<TextView>(R.id.tv_product_name)
            val price = item.findViewById<TextView>(R.id.tv_product_location)
            val postedOn = item.findViewById<TextView>(R.id.tv_product_distance)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
           return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_user_product,parent,false))
        }

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text = list[position].name
            holder.price.text = "Rs ${list[position].price.toString()}"
            holder.postedOn.text = DateTimeUtils.toDate(list[position].timestamp?.Seconds!!)
            Glide.with(context).load(list[position].displayImage).placeholder(R.drawable.user).into(holder.image)

            holder.itemView.setOnClickListener {
                val intent = Intent(context, NewProductDetailActivity::class.java)
                intent.putExtra(Constants.productId, list[position].id)
                context.startActivity(intent)
            }
        }
    }
}