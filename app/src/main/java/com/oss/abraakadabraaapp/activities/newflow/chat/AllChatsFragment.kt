package com.oss.abraakadabraaapp.activities.newflow.chat

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.ChatAdapter
import com.oss.abraakadabraaapp.utils.Constants

class AllChatsFragment : Fragment() ,ChatAdapter.onChatClicked{
    lateinit var application: BaseActivity
    private lateinit var rvChats:RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_all_chats, container, false)

        rvChats = view.findViewById(R.id.rvChats)
        application = activity as BaseActivity

        application.postEvent(Constants.PAGE_CHATS,null)
        setUpRecyclerview()

        return view
    }

    private fun setUpRecyclerview() {
        var chatList = ArrayList<GiverChatModel>()

        val db = Firebase.firestore
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val docRef = db.collection("chats")
//        docRef.addSnapshotListener()
        docRef.addSnapshotListener { snapshot, e ->
            for (doc in snapshot!!.documents) {
                docRef.document(doc.id).addSnapshotListener { value, error ->
                    if (value?.data?.get("sender_id").toString() == currentUserId){
                        Log.d("TAG - ", "setUpRecycler :: ${value?.data} ")
                        val model = GiverChatModel()
                        model.product = value?.data?.get("product").toString()
                        model.receiverId = value?.data?.get("receiver_id").toString()
                        model.productId = value?.data?.get("product_id").toString()
                        model.receiverName = value?.data?.get("receiver_name").toString()
                        model.senderId = value?.data?.get("sender_id").toString()

                        chatList.add(model)
                    }
                    /*var d = value?.data
                    var model = ChatModel()
                    model.senderId = d?.get("senderId").toString()
                    model.receiverId = d?.get("receiverId").toString()
                    model.text = d?.get("text").toString()
                    model.timestamp = d?.get("timestamp").toString()
                    Log.d("TAG - ", "Gson data ${Gson().toJson(model)}")

                    chatList.add(model)*/
                }
            }
//            adapter.setList(chatList)
//            adapter.notifyDataSetChanged()
            Log.d("TAG -  the size is: ${chatList.size} ", Gson().toJson(chatList))
            if (e != null) {
                Log.d("TAG - ", "error : setUpRecycler: ${Gson().toJson(e)}")
                return@addSnapshotListener
            }

        }

        rvChats.layoutManager = LinearLayoutManager(context)
        val adapter = ChatAdapter(requireContext(),chatList,this)
        rvChats.adapter = adapter
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
        intent.putExtra(Constants.CHATS_DATA,chat_room)
        startActivity(intent)
    }

}