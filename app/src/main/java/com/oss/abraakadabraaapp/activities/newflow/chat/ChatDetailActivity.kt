package com.oss.abraakadabraaapp.activities.newflow.chat

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.text.capitalize
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.WriteBatch
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.karumi.dexter.Dexter
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.MyPayAsYouGoActivity
import com.oss.abraakadabraaapp.activities.newflow.NewHomeActivity
import com.oss.abraakadabraaapp.activities.newflow.RequesterActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.ChatMessageAdapter
import com.oss.abraakadabraaapp.activities.newflow.customeview.WrapContentLinearLayoutManager
import com.oss.abraakadabraaapp.activities.newflow.model.NotificationDataModel
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
import com.oss.abraakadabraaapp.utils.Constants.DISPLAY_NAME
import com.oss.abraakadabraaapp.utils.Constants.DISPLAY_PIC
import com.oss.abraakadabraaapp.utils.Constants.PAGE_CHATS_DETAILS
import com.oss.abraakadabraaapp.utils.Constants.UNDER_DEV
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.utils.Utility.convertToTimestamp
import com.oss.abraakadabraaapp.utils.Utility.toDate
import com.oss.abraakadabraaapp.utils.Utility.toDateAndTime
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


class ChatDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityChatDetailBinding
    private lateinit var adapter: ChatMessageAdapter
    private var list: ArrayList<String> = ArrayList()
    var chatData: ChatListModel? = null
    var chatNode = ""
    private val TAG = "ChatDetailActivity"
    var firestoreUserAdapter: FirestoreRecyclerAdapter<ChatModel, UsersViewholder>? = null
    private val mainViewModel: AuthViewModel by viewModel()

    private lateinit var messageListener: ListenerRegistration
    private val firestore = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        postEvent(PAGE_CHATS_DETAILS, null)

//        showOnlineOrOffline()

        val adRequest = AdRequest.Builder().build();
        binding.adView.loadAd(adRequest)

        /*if (intent.hasExtra(Constants.productId)) {
            val bundle = Gson().fromJson(
                intent.extras?.getString(Constants.productId),
                NotificationDataModel::class.java
            )
            chatNode = bundle.chatNode.toString()
            val db = Firebase.firestore
            db.collection("notifications")
                .document(bundle.notificationDoc.toString())
                .update("deleted", true)

            loaddata()

        }*/
        if (intent.hasExtra(CHATS_DATA)) {
            binding.chatName.text = intent.extras?.getString(DISPLAY_NAME)
            Glide.with(this).load(intent.extras?.getString(DISPLAY_PIC))
                .placeholder(resources.getDrawable(R.drawable.ic_profile))
                .into(binding.profilePic)
            /*
            chatData =
                Gson().fromJson(intent.extras?.getString(CHATS_DATA, ""), ChatListModel::class.java)
            chatNode = chatData!!.product_id + Utility.setOneToOneChat(
                chatData!!.sender_id.toString(),
                chatData!!.receiver_id.toString()
            )*/

            chatNode = intent.extras?.getString(Constants.CHATS_DATA, "").toString()

            setUpRecycler(chatNode)
            loaddata()

            if (intent.extras?.getString("from","") == "posted_page"){
                sendMessage(intent.extras?.getString(Constants.MESSAGE,"").toString())
            }
        }

        LocalBroadcastManager.getInstance(this@ChatDetailActivity)
            .registerReceiver(mReceiver, IntentFilter(Constants.notificationReceived))

        Log.d("ok", "onCreate: $chatData")
        setUpObserver()
        var currentUserInfo = PreferencesManagement.getUserInfo(this)

        if (chatData != null) {
            binding.productName.text = chatData!!.product
        }

        messageListener = firestore.collection("chats")
            .document(chatNode)
            .collection("Messages")
            .addSnapshotListener { querySnapshot, _ ->
                querySnapshot?.let {
                    for (documentChange in it.documentChanges) {
                        val message = documentChange.document.toObject(ChatModel::class.java)

                        // Handle the message status (delivered/read)
                        handleDoubleTick(message)
                    }
                }
            }

        clickEvents()

        markAsRead()


    }

    private fun showOnlineOrOffline() {
        val db = Firebase.firestore
        val otherUser = if (currentUserId == chatData?.sender_id)  chatData?.receiver_id.toString() else  chatData?.sender_id.toString()
        db.collection("online_users").document(otherUser)
            .addSnapshotListener { value, error ->

                Log.d(TAG, "onResume: ${value}")
                Log.d(TAG, "onResume: ${error}")

                if (value?.data != null){
                    val isOnline: Boolean = value.data?.get("isOnline") as Boolean
                    val isTyping: Boolean = value.data?.get("isTyping") as Boolean
//            Log.d(TAG, "onResume: ${Gson().toJson(value)}")
                    Log.e(TAG, "onResume: $error")
                    if (isOnline) {
                        binding.onlineStatus.text = "Online"
                        binding.onlineStatus.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.teal_200
                            )
                        )
                    } else {
                        binding.onlineStatus.text = "Offline"
                        binding.onlineStatus.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.un_selected_color
                            )
                        )
                    }

                    if (isTyping) {
                        binding.onlineStatus.setTextColor(
                            ContextCompat.getColor(
                                this,
                                R.color.title_color
                            )
                        )
                        binding.onlineStatus.text = "Typing..."
                    }
                }
            }


    }

    private fun markAsRead() {
        val db = Firebase.firestore
        Log.d(TAG, "onCreate:before get true in chat node $chatNode")

        val user = FirebaseAuth.getInstance().currentUser?.uid
        val read = db.collection("chats").document(chatNode).collection("Messages")
        read.get()
            .addOnSuccessListener { snapshot ->
                for (doc in snapshot.documents) {
                    Log.w(TAG, "onCreate: documents: $doc")
                    if (user?.equals(doc.data?.get("from")) != true) {
                        read.document(doc.id).update("read", true).addOnSuccessListener {
                            Log.d(TAG, "onCreate: all messages read success")
                        }.addOnFailureListener {
                            Log.e(TAG, "onCreate: failed to mark as read")
                        }
                    }
                }
            }.addOnFailureListener {
                Log.d(TAG, "onCreate: error ${it.message}")
            }
    }

    override fun onBackPressed() {
        if (intent.hasExtra(Constants.hasNotificationData)) {
            startActivity(NewHomeActivity.createIntent(this@ChatDetailActivity))
        } else {
            super.onBackPressed()
        }
    }

    private var mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(Constants.notificationReceived, ignoreCase = true)) {
                if (intent.extras != null && intent.getStringExtra(Constants.notificationReceived) != null) {

                }
            }
        }
    }


    // Set user as offline when the app is in the background or closed
    fun setUserOffline() {
        if (FirebaseAuth.getInstance().currentUser?.uid != null) {
            val userRef =
                db.collection("online_users")
                    .document(FirebaseAuth.getInstance().currentUser?.uid.toString())

            userRef
                .update("isOnline", false, "lastOnlineTimestamp", FieldValue.serverTimestamp())
                .addOnSuccessListener {
                    // Update UI or perform other actions
                    Log.d("TAG:::>", "setUserOnline: false")

                }
        }

    }
    private fun loaddata() {
        val db = Firebase.firestore

        Log.d("Notification TAG", "onCreate: $chatNode")
        setUpRecycler(chatNode)
        val docRef = db.collection("chats")
            .document(chatNode).addSnapshotListener { value, error ->
                Log.d("Notification TAG", "onCreate: $value")

                chatData = value?.toObject(ChatListModel::class.java)
                if (currentUserId == chatData!!.sender_id) {
                    binding.chatName.text = chatData!!.receiver_name.toString()
                    Glide.with(applicationContext).load(chatData!!.receiver_avatar)
                        .placeholder(resources.getDrawable(R.drawable.ic_profile))
                        .into(binding.profilePic)
                    binding.markBtn.text = "Mark as Delivered"
                } else {
                    binding.chatName.text = chatData!!.sender_name.toString()
                    Glide.with(applicationContext).load(chatData!!.sender_avatar)
                        .placeholder(resources.getDrawable(R.drawable.ic_profile))
                        .into(binding.profilePic)
                    binding.markBtn.text = "Mark as Received"

                }
                binding.productName.text = chatData!!.product?.capitalize(Locale.ROOT)

                if (chatData!!.isUserBlocked){
                    binding.blockUserTxt.text = "Unblock User"
                }else{
                    binding.blockUserTxt.text = "Block User"
                }
                if (chatData!!.isChatClosed){

                }

//                if (chatData!!.status != "active" && currentUserId == chatData!!.sender_id){
//                    binding.markBtn.text = "Delivered"
//                    binding.markBtn.isEnabled = false
//                    binding.markBtn.setTextColor(ContextCompat.getColor(this,R.color.text_color))
//                    binding.markBtn.background =
//                        resources.getDrawable(R.drawable.rounded_rect_white_gray_stroke)
//                }else{
//                    binding.markBtn.text = "Received"
//                    binding.markBtn.isEnabled = false
//                    binding.markBtn.setTextColor(ContextCompat.getColor(this,R.color.text_color))
//                    binding.markBtn.background =
//                        resources.getDrawable(R.drawable.rounded_rect_white_gray_stroke)
//                }

//                    application.showToast(info.toString())
            }

    }

    private fun setUpObserver() {
        mainViewModel.reportChatSuccess.observe(this){
            if (it.code == 200){
                showToast(it.message.toString())
            }
        }
        mainViewModel.closeChatSessionSuccess.observe(this){
            if (it.code == 200){
                showToast(it.message.toString())
            }
        }

        mainViewModel.updateProductRequest.observe(this) {
//            Log.d("TAG - Product deails", "is it rue : ${productDetails.data.description}")

            if (it.code == 200) {
                showToast(it.responseMessage.toString())

                if (it.data.request_status == "received") {

                }
                loaddata()
            } else {
                Log.d("TAG -", "setUpObserver: fail")
            }
        }
        mainViewModel.chatSuccess.observe(this) {

            if (it.code == 200) {
//                showToast("Product reported")
            } else {
                showToast(it.responseMessage.toString())
            }

        }

        mainViewModel.userChatBlockSuccess.observe(this) {
            if (it.code == 200) {
                finish()
                showToast("User Blocked")
            }
        }
        mainViewModel.errorMessage.observe(this) {
//            if (it.isNotBlank()) showToast(it)
        }

        mainViewModel.isLoading.observe(this) {
            loader(it)
        }

    }

    private fun clickEvents() {
        binding.markBtn.setOnClickListener {
            if (currentUserId == chatData?.sender_id) {
                mainViewModel.updateProductRequest(
                    chatData?.requestId.toString(),
                    "received")
            } else {
                mainViewModel.updateProductRequest(
                    chatData?.requestId.toString(),
                    "delivered"
                )
            }
        }
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
            blockUser(chatData?.isUserBlocked!!)
        }
        binding.reportUser.setOnClickListener {
            postClick(BUTTON_CHAT_REPORT_USER)
            showToast(UNDER_DEV)
            val map = HashMap<String, String>()
            map["reason"] = "default"
            showToast("User Reported")
            mainViewModel.reportChat(chatNode,map)
        }
        binding.deleteChat.setOnClickListener {
            postClick(BUTTON_CHAT_DELETE)
            //Close chat
//            deleteChat()
            mainViewModel.closeChatSession(currentUserId.toString())
        }

        binding.sendMessage.setOnClickListener {
            postClick(BUTTON_CHAT_SEND_MESSAGE)
            if (chatData?.enabled == false) {
                Toast.makeText(this, "Product Cancelled", Toast.LENGTH_SHORT).show()
                binding.messageBox.setText("")
            } else {
                sendMessage(binding.messageBox.text.toString().trim())
                list.add(binding.messageBox.text.toString().trim())
                binding.messageBox.setText("")
            }

        }
        val handler = Handler(Looper.getMainLooper())

        binding.messageBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (s.toString().isNotEmpty()) {
                    // User started typing
                    setUserTypingStatus(true)
                    // Implement debounce mechanism
                    handler.removeCallbacksAndMessages(null)
                    handler.postDelayed({ setUserTypingStatus(false) }, 3000)
                }
            }
        })
    }

    private fun showBlockAlert() {

    }

    private fun setUserTypingStatus(isTyping: Boolean) {
        // Update the typing status in Firebase for the current user
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let { uid ->
            val db = Firebase.firestore
            val data = hashMapOf(
                "isTyping" to isTyping
            )
            val ref = db.collection("online_users").document(currentUserId!!)
            ref.update(data as Map<String, Any>)
                .addOnSuccessListener { documentReference ->
                    Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference}")
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding document", e)
                }
            val typingRef = FirebaseDatabase.getInstance().getReference("typing").child(uid)
            typingRef.setValue(isTyping)
        }
    }

    private fun blockUser(isBlock:Boolean) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val map = HashMap<String, String>()
        if (currentUserId == chatData?.sender_id) {
            map["block_user"] = chatData?.receiver_id.toString()
        } else {
            map["block_user"] = chatData?.sender_id.toString()
        }
        map["reason"] = "default"
        if (isBlock) {
            mainViewModel.userChatUnBlock(map)
        }else{
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle("Alert!")
            builder.setMessage("Are you sure you want block the user")
            builder.setPositiveButton("Yes") { dialog, _ ->
                dialog.cancel()
                mainViewModel.userChatBlock(map)
            }
            builder.setNegativeButton(getString(android.R.string.cancel)) { dialog, _ -> dialog.cancel() }
            builder.show()
        }
    }

    private fun deleteChat() {
        val db = Firebase.firestore
        val postsCollectionRef = db.collection("chats").document(chatNode)
            .collection("Messages")

        postsCollectionRef
            .get()
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result!!) {
                        val documentRef = postsCollectionRef.document(document.id)
                        documentRef.delete()
                            .addOnSuccessListener {
                                // Document in subcollection successfully deleted
                            }
                            .addOnFailureListener { e ->
                                // Handle errors while deleting documents in sub-collection
                            }
                    }
                } else {
                    // Handle errors while retrieving documents in subcollection
                }

                // Step 2: Delete the main document
                val userDocumentRef = db.collection("users").document(chatNode)
                userDocumentRef
                    .delete()
                    .addOnSuccessListener {
                        // Main document successfully deleted
                        showToast("Chat deleted")
                        finish()
                    }
                    .addOnFailureListener { e ->
                        // Handle errors while deleting the main document
                    }
            })

    }

    private fun sendMessage(message: String) {
        if (message == "") {
            showToast("Please enter some message")
        } else {
            val db = Firebase.firestore
            val sender_id = FirebaseAuth.getInstance().currentUser?.uid
            val calendar = Calendar.getInstance()
            val sdf = SimpleDateFormat("dd MMM yyyy")
            val currentDate: String = sdf.format(calendar.time)
            chatData!!.last_message = message
            val c = Calendar.getInstance().time
            chatData!!.time_stamp = Timestamp.now()

            chatNode = chatNode

            db.collection("chats")
                .document(chatNode)
                .set(chatData!!)
                .addOnSuccessListener {
                    Log.d("TAG - ", "sendToChat: chat room created")

                }
                .addOnFailureListener {

                }

            val chats = hashMapOf(
                "chatNode" to chatNode,
                "receiverId" to if(sender_id == chatData!!.receiver_id) chatData!!.sender_id else chatData?.receiver_id,
                "senderId" to sender_id,
                "text" to message,
                "from" to sender_id,
                "read" to false,
                "timestamp" to Timestamp.now()
            )

            db.collection("chats")
                .document(
                    chatNode

                )
                .collection("Messages")
                .add(chats)
                .addOnSuccessListener {
                    val notification_user =
                        if (sender_id == chatData!!.sender_id.toString())
                            chatData!!.receiver_id.toString()
                        else chatData!!.sender_id.toString()
                    val map = HashMap<String, String>()
                    map["receiverId"] = notification_user //chatData["receiver_id"].toString()
                    map["message"] = message
                    map["chatNode"] = chatNode
                    map["productId"] = chatData?.product_id.toString()
                    mainViewModel.sendNotification(map)
                    Log.d("TAG - ", "sendToChat: chat posted")
                }
                .addOnFailureListener {

                }
        }

    }

    private fun setUpRecycler(chatNode: String) {

        val db = Firebase.firestore
        val sender_id = FirebaseAuth.getInstance().currentUser?.uid

        val query = db.collection("chats")
            .document(
                chatNode
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
                    Log.d("Chat-->", "onBindViewHolder: ${user}")
                    holder.bind(model)
                    if (model.from == sender_id) {
                        holder.binding.toLayout.visibility = View.VISIBLE
                        holder.binding.fromLayout.visibility = View.GONE
                        holder.binding.toMessage.text = model.text
                        holder.binding.toMessageTime.text = toDateAndTime(model.timestamp)
                        holder.binding.toMessage.setTextIsSelectable(true)
                    } else {
                        holder.binding.fromLayout.visibility = View.VISIBLE
                        holder.binding.toLayout.visibility = View.GONE
                        holder.binding.fromMessage.text = model.text
                        holder.binding.fromMessageTime.text = toDateAndTime(model.timestamp)
                        holder.binding.fromMessage.setTextIsSelectable(true)
                    }

                    var id = getSnapshots().getSnapshot(position).id
                    markMessageAsRead(id)
                    Log.d(TAG, "onBindViewHolder doc id: $id")
                }
            }


        val layoutManager = WrapContentLinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true

        binding.rvChats.layoutManager = layoutManager
        binding.rvChats.adapter = firestoreUserAdapter

    }

    class UsersViewholder(val binding: ChatMessageRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(documentSnapshot: ChatModel) {

        }
    }

    private fun markMessageAsRead(messageId: String) {
        val firestore = FirebaseFirestore.getInstance()
        val messagesCollection = firestore.collection("chats").document(chatNode)
            .collection("Messages")
        val user = FirebaseAuth.getInstance().currentUser?.uid

        // Update the 'read' field to true
        messagesCollection.document(messageId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.get("from") != user) {
                    messagesCollection.document(messageId).update("read", true)
                        .addOnSuccessListener { }.addOnFailureListener { }
                }
                Log.d(TAG, "Message marked as read successfully")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error marking message as read: $e")
            }
    }

    override fun onStart() {
        super.onStart()
        setUserOnline()
        firestoreUserAdapter?.startListening()


//        EventBus.getDefault().register(this)
    }

    fun setUserOnline() {
        if (FirebaseAuth.getInstance().currentUser?.uid != null) {
            val userRef = db.collection("online_users")
                .document(FirebaseAuth.getInstance().currentUser?.uid.toString())

            userRef
                .update("isOnline", true)
                .addOnSuccessListener {
                    // Update UI or perform other actions
                    Log.d("TAG:::>", "setUserOnline: true")
                }
        }

    }


    override fun onStop() {
        super.onStop()
        setUserOffline()
        firestoreUserAdapter?.stopListening()
        //      EventBus.getDefault().unregister(this)
    }

    private fun handleDoubleTick(message: ChatModel) {
        // Check if the message is delivered and/or read
        if (message.delivered) {
            // Update UI to show delivered status (single tick)
            // ...
        }

        if (message.read) {
            // Update UI to show read status (double tick)
            // ...
        }
    }

    private fun updateRecycler() {
        val recyclerViewState = binding.rvChats.layoutManager?.onSaveInstanceState()
        binding.rvChats.adapter?.notifyDataSetChanged()
        binding.rvChats.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    override fun onResume() {
        super.onResume()
        showOnlineOrOffline()
    }
}
