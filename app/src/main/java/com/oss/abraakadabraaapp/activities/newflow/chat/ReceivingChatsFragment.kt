package com.oss.abraakadabraaapp.activities.newflow.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.GsonBuilder
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.ChatAdapter
import com.oss.abraakadabraaapp.activities.newflow.customeview.WrapContentLinearLayoutManager
import com.oss.abraakadabraaapp.databinding.ChatRowBinding
import com.oss.abraakadabraaapp.utils.Constants
import java.text.SimpleDateFormat
import java.util.*


class ReceivingChatsFragment : Fragment(),ChatAdapter.onChatClicked {
    lateinit var application: BaseActivity
    lateinit var rvChats: RecyclerView
    lateinit var  nodata : TextView
    lateinit var firestoreUserAdapter: FirestoreRecyclerAdapter<ChatListModel, UsersViewholder>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_receiving_chats, container, false)
        rvChats = view.findViewById(R.id.rvChats)
        nodata = view.findViewById(R.id.nodata3)

        setUpRecyclerview()
        application = (activity as BaseActivity)

        application.postEvent(Constants.PAGE_RECEIVER_CHAT,null)

        return view
    }
    private fun setUpRecyclerview() {
        var chatList = ArrayList<GiverChatModel>()

        val db = Firebase.firestore
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val docRef = db.collection("chats").whereEqualTo("sender_id",currentUserId)

        docRef.get().addOnSuccessListener { snap ->
            if(snap.isEmpty){
                nodata.visibility = View.VISIBLE
            }else{
                nodata.visibility = View.GONE
            }
        }
        val options: FirestoreRecyclerOptions<ChatListModel> = FirestoreRecyclerOptions.Builder<ChatListModel>()
            .setQuery(docRef,ChatListModel::class.java)
            .build()

        firestoreUserAdapter=object :FirestoreRecyclerAdapter<ChatListModel, UsersViewholder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):UsersViewholder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val listItemBinding = ChatRowBinding.inflate(layoutInflater, parent, false)
                return UsersViewholder(listItemBinding)
            }

            override fun onBindViewHolder(holder: UsersViewholder, position: Int, model: ChatListModel) {
                val user=model
                holder.bind(model)
                holder.binding.userName.text = model.product
                holder.binding.productName.text = model.receiver_name
                holder.binding.message.text = model.last_message
                holder.binding.time.text = model.time_stamp
//                holder.binding.message.text = model.messages?.get(model.messages?.size?.minus(1)!!).toString()
                Glide.with(requireContext()).load(model.receiver_avatar)
                    .placeholder(resources.getDrawable(R.drawable.ic_profile))
                    .into(holder.binding.profilePic)
                holder.itemView.setOnClickListener {
                    val chat_room = hashMapOf(
                        "from" to model.from,
                        "sender_id" to model.sender_id,
                        "sender_name" to model.sender_name,
                        "sender_avatar" to model.sender_avatar,
                        "receiver_id" to model.receiver_id,
                        "receiver_name" to model.receiver_name,
                        "receiver_avatar" to model.receiver_avatar,
                        "product_id" to model.product_id,
                        "product" to model.product)

                    val intent = Intent(requireContext(),ChatDetailActivity::class.java)
                    intent.putExtra(Constants.CHATS_DATA,chat_room)
                    intent.putExtra("data_from","fragment")
                    startActivity(intent)
                }
                var a= GsonBuilder().create().toJson(model)
                Log.d("TAG", "onBindViewHolder: "+a)
            }
        }
        var layoutManager = WrapContentLinearLayoutManager(requireContext())
        rvChats.layoutManager = layoutManager
//        val adapter = ChatAdapter(requireContext(),chatList,this)
        rvChats.adapter = firestoreUserAdapter
    }

    override fun onChatClick(item: GiverChatModel) {
        val chat_room = hashMapOf(
            "receiver_id" to item.receiverId,
            "sender_id" to item.senderId,
            "product_id" to item.productId,
            "receiver_name" to item.receiverName,
            "product" to item.product
        )

        val intent = Intent(requireContext(),ChatDetailActivity::class.java)
        intent.putExtra("data_from","fragment")
        intent.putExtra(Constants.CHATS_DATA,chat_room)
        startActivity(intent)
    }
    class UsersViewholder(val binding: ChatRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(documentSnapshot: ChatListModel) {

        }
    }
    override fun onStart() {
        super.onStart()
        firestoreUserAdapter.startListening()
//        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        firestoreUserAdapter.stopListening()
        //      EventBus.getDefault().unregister(this)
    }
}