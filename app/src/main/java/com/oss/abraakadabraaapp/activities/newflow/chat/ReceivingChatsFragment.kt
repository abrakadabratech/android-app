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
import androidx.recyclerview.widget.LinearLayoutManager
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
import com.oss.abraakadabraaapp.activities.newflow.adapters.ExpandableAdapter
import com.oss.abraakadabraaapp.activities.newflow.customeview.WrapContentLinearLayoutManager
import com.oss.abraakadabraaapp.databinding.ChatRowBinding
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility.convertToTimestamp
import com.oss.abraakadabraaapp.utils.Utility.toDate
import java.text.SimpleDateFormat
import java.util.*


class ReceivingChatsFragment : Fragment(),ChatAdapter.onChatClicked {
    lateinit var application: BaseActivity
    private lateinit var rvChats: RecyclerView
    private lateinit var  nodata : TextView
    private lateinit var oldChatText: TextView
    private lateinit var oldChats: RecyclerView

    private val TAG = "ReceivingChatsFragment"
    private lateinit var firestoreUserAdapter: FirestoreRecyclerAdapter<ChatListModel, UsersViewholder>

    override fun onResume() {
        super.onResume()
        loadGroupedChats()
    }

    private fun loadGroupedChats() {
        val db = Firebase.firestore
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid


        val collectionRef = db.collection("chats").whereEqualTo("product_receiver", currentUserId)
            .orderBy("status", Query.Direction.ASCENDING)
            .orderBy("time_stamp", Query.Direction.DESCENDING)

        collectionRef
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty){
                    val docRef = db.collection("chats").whereEqualTo("product_receiver",currentUserId)
                    docRef.get().addOnSuccessListener { snap ->
                        if(snap.isEmpty){
                            nodata.visibility = View.VISIBLE
                            oldChats.visibility = View.GONE
                        }else{
                            nodata.visibility = View.GONE
                        }
                    }                }else{
                    nodata.visibility = View.GONE
                    var groupChats = mutableListOf<GroupedChatListModel>()
                    val groupedItems = mutableMapOf<String, List<ChatListModel>>()

                    for (document in querySnapshot.documents) {
                        val item = document.toObject(ChatListModel::class.java)

                        if (item != null) {
                            val category = item.product_id.toString()

                            if (groupedItems.containsKey(category)) {
                                groupedItems[category] = groupedItems[category]!! + item
                            } else {
                                groupedItems[category] = listOf(item)
                            }
                        }
                    }

                    // Now 'groupedItems' contains items grouped by category
                    // You can iterate through it and do whatever you need
                    Log.d(TAG, "setUpRecyclerview: ${Gson().toJson(groupedItems)}")
                    for ((category, items) in groupedItems) {
                        // Process each category and its items
                        if (items.size > 0) {
                            groupChats.add(
                                GroupedChatListModel(false,
                                    category,
                                    items[0].product.toString(),
                                    items[0].sender_name.toString(),
                                    items[0].product_image,
                                    items
                                )
                            )
                        }
                    }
                    val adapter = ExpandableAdapter(requireContext(),groupChats,currentUserId)
                    rvChats.adapter = adapter
                    rvChats.layoutManager = LinearLayoutManager(requireContext())
                    println("Category: $groupChats,")
                }

            }
            .addOnFailureListener { exception ->
                // Handle errors
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_receiving_chats, container, false)
        rvChats = view.findViewById(R.id.rvChats)
        nodata = view.findViewById(R.id.nodata3)
        oldChats = view.findViewById(R.id.oldChats)
        oldChatText = view.findViewById(R.id.oldChatsTxt)

        setUpRecyclerview()
        application = (activity as BaseActivity)

        application.postEvent(Constants.PAGE_RECEIVER_CHAT,null)

        return view
    }
    private fun setUpRecyclerview() {
        val db = Firebase.firestore
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val docRef = db.collection("chats").whereEqualTo("product_receiver",currentUserId)
        val options: FirestoreRecyclerOptions<ChatListModel> = FirestoreRecyclerOptions.Builder<ChatListModel>()
            .setQuery(docRef,ChatListModel::class.java)
            .build()
        val productInfoRef = db.collection("product_requests")

        firestoreUserAdapter=object :FirestoreRecyclerAdapter<ChatListModel, UsersViewholder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):UsersViewholder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val listItemBinding = ChatRowBinding.inflate(layoutInflater, parent, false)
                return UsersViewholder(listItemBinding)
            }

            override fun onBindViewHolder(holder: UsersViewholder, position: Int, model: ChatListModel) {
                holder.bind(model)
                holder.binding.productName.text = model.product
                holder.binding.message.text = model.last_message
                holder.binding.time.text = convertToTimestamp(model.time_stamp)
                holder.binding.userName.text = model.sender_name
                Glide.with(requireContext()).load(model.sender_avatar)
                    .placeholder(resources.getDrawable(R.drawable.ic_profile))
                    .into(holder.binding.profilePic)

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
        oldChats.layoutManager = layoutManager
        oldChats.adapter = firestoreUserAdapter
    }
    private fun navigateToChats(model: ChatListModel) {

        val intent = Intent(requireContext(),ChatDetailActivity::class.java)
        intent.putExtra(Constants.CHATS_DATA, Gson().toJson(model))
        intent.putExtra("data_from","fragment")
        intent.putExtra(Constants.DISPLAY_NAME,model.sender_name)
        intent.putExtra(Constants.DISPLAY_PIC,model.sender_avatar)
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