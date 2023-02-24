package com.oss.abraakadabraaapp.activities.newflow.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.wallet.IsReadyToPayRequest.fromJson
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.ChatMessageAdapter
import com.oss.abraakadabraaapp.activities.newflow.customeview.WrapContentLinearLayoutManager
import com.oss.abraakadabraaapp.activities.newflow.ui.FeedbackActivity
import com.oss.abraakadabraaapp.databinding.ActivityChatDetailBinding
import com.oss.abraakadabraaapp.databinding.ChatMessageRowBinding
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_CHAT_DETAILS
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_CHAT_BLOCK_USER
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_CHAT_DELETE
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_CHAT_OPTION_MENU
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_CHAT_REPORT_USER
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_CHAT_SEND_MESSAGE
import com.oss.abraakadabraaapp.utils.Constants.CHATS_DATA
import com.oss.abraakadabraaapp.utils.Constants.PAGE_CHATS_DETAILS
import com.oss.abraakadabraaapp.utils.Constants.UNDER_DEV
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class ChatDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityChatDetailBinding
    private lateinit var adapter: ChatMessageAdapter
    private var list: ArrayList<String> = ArrayList()
    var chatData : ChatListModel? = null
    var data_from = ""
    var chatNode = ""

    lateinit var firestoreUserAdapter: FirestoreRecyclerAdapter<ChatModel, UsersViewholder>
    private val mainViewModel: AuthViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postEvent(PAGE_CHATS_DETAILS, null)

        if (intent.hasExtra(CHATS_DATA)) {
            chatData = Gson().fromJson(intent.extras?.getString(CHATS_DATA,""),ChatListModel::class.java)
        }
        if (intent.hasExtra("data_from")) {
            data_from = intent.getStringExtra("data_from")!!
        }
        if (intent.hasExtra(Constants.productId)){
            chatData
        }

        Log.d("ok", "onCreate: $chatData")
        setUpObserver()
        setUpRecycler()

        if (chatData != null) {
            if (data_from == "activity") {
                binding.chatName.text = chatData!!.receiver_name.toString()
                Glide.with(this).load(chatData!!.receiver_avatar)
                    .placeholder(resources.getDrawable(R.drawable.ic_profile))
                    .into(binding.profilePic)
            } else if(data_from == "fragment"){
                binding.chatName.text = chatData!!.sender_name
                Glide.with(this).load(chatData!!.sender_avatar)
                    .placeholder(resources.getDrawable(R.drawable.ic_profile))
                    .into(binding.profilePic)
            }
            binding.productName.text = chatData!!.product
        }

        clickEvents()

    }

    private fun setUpObserver() {
        mainViewModel.chatSuccess.observe(this) {

            if (it.code == 200) {
//                showToast("Product reported")
            } else {
                showToast(it.responseMessage.toString())
            }

        }

        mainViewModel.errorMessage.observe(this) {
            if (it.isNotBlank()) showToast(it)
        }

        mainViewModel.isLoading.observe(this) {
            loader(it)
        }

    }

    private fun clickEvents() {
        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_CHAT_DETAILS)
            onBackPressed()
        }
        binding.optionMenu.setOnClickListener {
            postClick(BUTTON_CHAT_OPTION_MENU)
            binding.menuLayout.visibility = View.VISIBLE
        }
        binding.menuLayout.setOnClickListener {
            binding.menuLayout.visibility = View.GONE
        }
        binding.menuDialog.setOnClickListener {
            binding.menuLayout.visibility = View.VISIBLE
        }
        binding.blockUser.setOnClickListener {
            postClick(BUTTON_CHAT_BLOCK_USER)
            showToast(UNDER_DEV)
        }
        binding.reportUser.setOnClickListener {
            postClick(BUTTON_CHAT_REPORT_USER)
            showToast(UNDER_DEV)
        }
        binding.deleteChat.setOnClickListener {
            showToast(UNDER_DEV)
            postClick(BUTTON_CHAT_DELETE)

        }

        binding.sendMessage.setOnClickListener {
            postClick(BUTTON_CHAT_SEND_MESSAGE)
            sendMessage(binding.messageBox.text.toString().trim())
            list.add(binding.messageBox.text.toString().trim())

            binding.messageBox.setText("")
        }
    }

    private fun sendMessage(message: String) {
        if (message == ""){
            showToast("Please enter some message")
        }else{
            val db = Firebase.firestore
            val sender_id = FirebaseAuth.getInstance().currentUser?.uid
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("dd MMM yyyy")
            val currentDate: String = sdf.format(calendar.time)
            chatData!!.last_message = message
            val c = Calendar.getInstance().time
            chatData!!.time_stamp = currentDate

            if (data_from == "activity"){

            }
            chatNode = chatData!!.product_id!! + setOneToOneChat(
                chatData!!.sender_id.toString(),
                chatData!!.receiver_id.toString()
            )

            db.collection("chats")
                .document(chatNode)
                .set(chatData!!)
                .addOnSuccessListener {
                    Log.d("TAG - ", "sendToChat: chat room created")

                }
                .addOnFailureListener {

                }

            val chats = hashMapOf(
                "chatNode" to setOneToOneChat(
                    chatData!!.sender_id.toString(),
                    chatData!!.receiver_id.toString()
                ),
                "receiverId" to chatData!!.receiver_id,
                "senderId" to sender_id,
                "text" to message,
                "from" to sender_id,
                "timestamp" to System.currentTimeMillis()
            )

            db.collection("chats")
                .document(
                    chatData!!.product_id + setOneToOneChat(
                        chatData!!.sender_id.toString(),
                        chatData!!.receiver_id.toString()
                    )
                )
                .collection("Messages")
                .add(chats)
                .addOnSuccessListener {
                    generateAuthToken()

                    val notification_user = if (sender_id == chatData!!.sender_id.toString()) chatData!!.receiver_id.toString() else chatData!!.sender_id.toString()
                    val map = HashMap<String, String>()
                    map["receiverId"] = notification_user //chatData["receiver_id"].toString()
                    map["message"] = message
                    map["chatNode"] = chatNode
                    mainViewModel.sendNotification(Utility.getAuthentication(this), map)
                    Log.d("TAG - ", "sendToChat: chat posted")
                    /*val intent = Intent(this,ChatDetailActivity::class.java)
                    intent.putExtra(Constants.CHATS_DATA,chat_room)
                    startActivity(intent)*/
                }
                .addOnFailureListener {

                }
        }

    }

    // what is pointer in C
//    depends on project vacancies
    fun setOneToOneChat(uid1: String, uid2: String): String {
        return if (uid1 < uid2) {
            uid1 + uid2
        } else {
            uid2 + uid1
        }
    }

    private fun setUpRecycler() {
        var chatList = ArrayList<ChatModel>()

        val db = Firebase.firestore
        val sender_id = FirebaseAuth.getInstance().currentUser?.uid

        val query = db.collection("chats")
            .document(
                chatData!!.product_id + setOneToOneChat(
                    chatData!!.sender_id.toString(),
                    chatData!!.receiver_id.toString()
                )
            )
            .collection("Messages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
//            .whereEqualTo("chatNode",setOneToOneChat(chatData["sender_id"].toString(),chatData["receiver_id"].toString()))
//            .whereEqualTo("from",chatData["sender_id"])/*.orderBy("date", Query.Direction.DESCENDING)*/

        val options: FirestoreRecyclerOptions<ChatModel> =
            FirestoreRecyclerOptions.Builder<ChatModel>()
                .setQuery(query, ChatModel::class.java)
                .build()

        firestoreUserAdapter =
            object : FirestoreRecyclerAdapter<ChatModel, UsersViewholder>(options) {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewholder {
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val listItemBinding =
                        ChatMessageRowBinding.inflate(layoutInflater, parent, false)
                    return UsersViewholder(listItemBinding)
                }

                override fun onBindViewHolder(
                    holder: UsersViewholder,
                    position: Int,
                    model: ChatModel
                ) {
                    val user = model
                    holder.bind(model)
                    if (model.from == sender_id) {

                        holder.binding.toLayout.visibility = View.VISIBLE
                        holder.binding.fromLayout.visibility = View.GONE
                        holder.binding.toMessage.text = model.text
                    } else {
                        holder.binding.fromLayout.visibility = View.VISIBLE
                        holder.binding.toLayout.visibility = View.GONE
                        holder.binding.fromMessage.text = model.text
                    }
                    var a = GsonBuilder().create().toJson(model)
                    Log.d("TAG", "onBindViewHolder: " + a)
                }
            }

        val layoutManager = WrapContentLinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true

        binding.rvChats.layoutManager = layoutManager
        binding.rvChats.adapter = firestoreUserAdapter
//        val adapter = ChatMessageAdapter
    }

    class UsersViewholder(val binding: ChatMessageRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(documentSnapshot: ChatModel) {

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