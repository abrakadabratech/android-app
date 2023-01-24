package com.oss.abraakadabraaapp.activities.newflow.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.ChatAdapter
import com.oss.abraakadabraaapp.utils.Constants

class AllChatsFragment : Fragment() {
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
        rvChats.layoutManager = LinearLayoutManager(context)
        val adapter = ChatAdapter(requireContext(),3)
        rvChats.adapter = adapter
    }

}