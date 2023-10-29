package com.oss.abraakadabraaapp.activities.newflow.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.ChatAdapter
import com.oss.abraakadabraaapp.activities.newflow.customeview.WrapContentLinearLayoutManager
import com.oss.abraakadabraaapp.databinding.ChatRowBinding
import com.oss.abraakadabraaapp.databinding.FragmentGivingChatsBinding
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import java.text.SimpleDateFormat
import java.util.*

class GivingChatsFragment : Fragment(), ChatAdapter.onChatClicked {
    lateinit var application: BaseActivity
    lateinit var binding: FragmentGivingChatsBinding
    private lateinit var nodata: TextView
    private lateinit var rvChats: RecyclerView
    private lateinit var firestoreUserAdapter: FirestoreRecyclerAdapter<ChatListModel, UsersViewholder>

    private val TAG = "GivingChatsFragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_giving_chats, container, false)

        rvChats = view.findViewById(R.id.rvChats)
        nodata = view.findViewById(R.id.nodata3)
        application = (activity as BaseActivity)

        application.postEvent(Constants.PAGE_GIVER_CHAT, null)
        setUpRecyclerview()
        return view
    }

    private fun setUpRecyclerview() {

        val db = Firebase.firestore
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val docRef = db.collection("chats").whereEqualTo("product_giver", currentUserId)
        docRef.get().addOnSuccessListener { snap ->
            if (snap.isEmpty) {
                nodata.visibility = View.VISIBLE
            } else {
                nodata.visibility = View.GONE
            }
        }
        val options: FirestoreRecyclerOptions<ChatListModel> =
            FirestoreRecyclerOptions.Builder<ChatListModel>()
                .setQuery(docRef, ChatListModel::class.java)
                .build()

        val productInfoRef = db.collection("product_requests")

        firestoreUserAdapter =
            object : FirestoreRecyclerAdapter<ChatListModel, UsersViewholder>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewholder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val listItemBinding = ChatRowBinding.inflate(layoutInflater, parent, false)
                    return UsersViewholder(listItemBinding)
                }

                override fun onBindViewHolder(
                    holder: UsersViewholder,
                    position: Int,
                    model: ChatListModel
                ) {
                    holder.bind(model)
                    Log.e(TAG, "onBindViewHolder: ${model.toString()}")

                    holder.binding.userName.text = model.receiver_name
                    Glide.with(requireContext()).load(model.receiver_avatar)
                        .placeholder(resources.getDrawable(R.drawable.ic_profile))
                        .into(holder.binding.profilePic)

                    holder.binding.productName.text = model.product
                    holder.binding.message.text = model.last_message
                    holder.binding.time.text = model.time_stamp

                    if(model.status == "cancelled"){
                        holder.binding.cancelledTxt.visibility = View.VISIBLE
                    }else{
                        holder.binding.cancelledTxt.visibility = View.GONE
                    }
                    holder.itemView.setOnClickListener {
                        if(model.status == "cancelled"){
                            Toast.makeText(context,"Product Cancelled",Toast.LENGTH_SHORT).show()
                        }else{
                            navigateToChats(model)
                        }

                    }

                }

            }


        val layoutManager = WrapContentLinearLayoutManager(requireContext())
        rvChats.layoutManager = layoutManager
        rvChats.adapter = firestoreUserAdapter
    }

    private fun navigateToChats(model: ChatListModel) {

        val intent =
            Intent(requireContext(), ChatDetailActivity::class.java)
        intent.putExtra(Constants.CHATS_DATA, Gson().toJson(model))
        intent.putExtra("data_from", "fragment")
        intent.putExtra(Constants.DISPLAY_NAME, model.receiver_name)
        intent.putExtra(
            Constants.DISPLAY_PIC,
            model.receiver_avatar
        )

        startActivity(intent)
    }

    override fun onChatClick(item: GiverChatModel) {
    }

    class UsersViewholder(val binding: ChatRowBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(documentSnapshot: ChatListModel) {

        }
    }

    override fun onStart() {
        super.onStart()
        firestoreUserAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        firestoreUserAdapter.stopListening()
    }
}