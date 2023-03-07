package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.NewNotificationAdapter
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatListModel
import com.oss.abraakadabraaapp.activities.newflow.chat.GiverChatModel
import com.oss.abraakadabraaapp.activities.newflow.chat.ReceivingChatsFragment
import com.oss.abraakadabraaapp.activities.newflow.customeview.WrapContentLinearLayoutManager
import com.oss.abraakadabraaapp.activities.newflow.ui.MyRequestDetailsActivity
import com.oss.abraakadabraaapp.databinding.ActivityNewNotificationBinding
import com.oss.abraakadabraaapp.databinding.ChatRowBinding
import com.oss.abraakadabraaapp.databinding.NotificationRowBinding
import com.oss.abraakadabraaapp.localdb.NotificationEntity
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_NOTIFICATIONS
import java.text.SimpleDateFormat
import java.util.*

class NewNotificationActivity : BaseActivity() {
    lateinit var firestoreUserAdapter: FirestoreRecyclerAdapter<NotificationEntity, UsersViewholder>
    private lateinit var binding:ActivityNewNotificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNotificationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postEvent(Constants.PAGE_NOTIFICATIONS,null)

        setUpRecyclerview()

        binding.rvNotification.layoutManager = LinearLayoutManager(this)
        //binding.rvNotification.adapter = NewNotificationAdapter(this,4)

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_NOTIFICATIONS)
            onBackPressed()
        }
    }

    private fun setUpRecyclerview() {
        var chatList = ArrayList<GiverChatModel>()

        val db = Firebase.firestore
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val docRef = db.collection("notifications").whereEqualTo("userId",currentUserId)

        docRef.get().addOnSuccessListener { snap ->
            if(snap.isEmpty){
                binding.nodata5.visibility = View.VISIBLE
            }else{
                binding.nodata5.visibility = View.GONE
            }
        }
        val options: FirestoreRecyclerOptions<NotificationEntity> =
            FirestoreRecyclerOptions.Builder<NotificationEntity>()
            .setQuery(docRef,NotificationEntity::class.java)
            .build()

        firestoreUserAdapter=object :FirestoreRecyclerAdapter<NotificationEntity,
                UsersViewholder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):UsersViewholder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val listItemBinding = NotificationRowBinding.inflate(layoutInflater, parent, false)
                return UsersViewholder(listItemBinding)
            }

            override fun onBindViewHolder(holder: UsersViewholder,
                                          position: Int, model: NotificationEntity) {

                val user=model
                if (!model.deleted){
                    holder.binding.rootlayout.setBackgroundColor(resources.getColor(R.color.bg_color))
                }else{
                    holder.binding.rootlayout.setBackgroundColor(resources.getColor(R.color.white))
                }
                holder.bind(model)
                holder.binding.userName.text = model.body
                holder.binding.productName.text = model.title
                holder.binding.message.text = getTime(model.timestamp?.toDate()?.toString()!!)
//                holder.binding.message.text = model.messages?.get(model.messages?.size?.minus(1)!!).toString()
                Glide.with(applicationContext).load(model.image)
                    .placeholder(resources.getDrawable(R.drawable.user))
                    .into(holder.binding.profilePic)

                holder.itemView.setOnClickListener {

//                    db.collection("notifications").("documentId",model.docId)
//                        .update()

                    db.collection("notifications").document(model.docId).update("deleted",true)

                    when(model.module){
                        Constants.productListing -> {
                            val intent = Intent(this@NewNotificationActivity, RequesterActivity::class.java)
                            intent.putExtra(Constants.productId, model.data)
                            startActivity(intent)
                        }
                        Constants.productRequestDetails -> {
                            val intent = Intent(this@NewNotificationActivity, MyRequestDetailsActivity::class.java)
                            intent.putExtra(Constants.productId, model.data)
                            startActivity(intent)
                        }
                        Constants.chatDetails -> {
                            val intent = Intent(this@NewNotificationActivity, ChatDetailActivity::class.java)
                            intent.putExtra(Constants.productId, model.data)
                            startActivity(intent)
                        }
                    }
                }
                var a= GsonBuilder().create().toJson(model)
                Log.d("TAG", "onBindViewHolder: "+a)
            }
        }
        val layoutManager = WrapContentLinearLayoutManager(this)
        binding.rvNotification.layoutManager = layoutManager
        binding.rvNotification.itemAnimator = null

//        val adapter = ChatAdapter(requireContext(),chatList,this)
        binding.rvNotification.adapter = firestoreUserAdapter
    }

    override fun onStart() {
        super.onStart()
        firestoreUserAdapter.startListening()
    }
    class UsersViewholder(val binding: NotificationRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(documentSnapshot: NotificationEntity) {

        }
    }

    fun getTime(unix:String):String{

        try {
            val sdf = SimpleDateFormat("MMM dd,yyyy HH:MM")
            val netDate = Date(unix)
            return sdf.format(netDate)
        } catch (e: Exception) {
            return e.toString()
        }
    }

    override fun onStop() {
        super.onStop()
        firestoreUserAdapter.stopListening()
    }
}