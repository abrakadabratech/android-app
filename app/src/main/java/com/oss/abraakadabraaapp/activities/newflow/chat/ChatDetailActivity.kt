package com.oss.abraakadabraaapp.activities.newflow.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.databinding.ActivityChatDetailBinding
import com.oss.abraakadabraaapp.utils.Constants.UNDER_DEV


class ChatDetailActivity : BaseActivity() {
    private lateinit var binding:ActivityChatDetailBinding
    private lateinit var adapter:ChatMessageAdapter
    private var list:ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpRecycler()
        binding.optionMenu.setOnClickListener {
            binding.menuLayout.visibility = View.VISIBLE
        }
        binding.menuLayout.setOnClickListener {
            binding.menuLayout.visibility = View.GONE
        }
        binding.menuDialog.setOnClickListener {
            binding.menuLayout.visibility = View.VISIBLE
        }
        binding.blockUser.setOnClickListener{
            showToast(UNDER_DEV)
        }
        binding.reportUser.setOnClickListener{
            showToast(UNDER_DEV)
        }
        binding.deleteChat.setOnClickListener {
            showToast(UNDER_DEV)
        }

        binding.sendMessage.setOnClickListener {
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