package com.oss.abraakadabraaapp.activities.newflow.chat

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.ChatMessageAdapter
import com.oss.abraakadabraaapp.databinding.ActivityChatDetailBinding
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_CHAT_DETAILS
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_CHAT_BLOCK_USER
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_CHAT_DELETE
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_CHAT_OPTION_MENU
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_CHAT_REPORT_USER
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_CHAT_SEND_MESSAGE
import com.oss.abraakadabraaapp.utils.Constants.PAGE_CHATS_DETAILS
import com.oss.abraakadabraaapp.utils.Constants.UNDER_DEV


class ChatDetailActivity : BaseActivity() {
    private lateinit var binding:ActivityChatDetailBinding
    private lateinit var adapter: ChatMessageAdapter
    private var list:ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        postEvent(PAGE_CHATS_DETAILS,null)

        setUpRecycler()

        clickEvents()


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
        binding.blockUser.setOnClickListener{
            postClick(BUTTON_CHAT_BLOCK_USER)
            showToast(UNDER_DEV)
        }
        binding.reportUser.setOnClickListener{
            postClick(BUTTON_CHAT_REPORT_USER)
            showToast(UNDER_DEV)
        }
        binding.deleteChat.setOnClickListener {
            showToast(UNDER_DEV)
            postClick(BUTTON_CHAT_DELETE)

        }

        binding.sendMessage.setOnClickListener {
            postClick(BUTTON_CHAT_SEND_MESSAGE)
            list.add(binding.messageBox.text.toString().trim())
            adapter.setList(list)
            adapter.notifyDataSetChanged()
            binding.messageBox.setText("")
        }
    }

    private fun setUpRecycler() {
        adapter = ChatMessageAdapter(this,list)
        var layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true

        binding.rvChats.layoutManager = layoutManager
        binding.rvChats.adapter = adapter
//        val adapter = ChatMessageAdapter
    }
}