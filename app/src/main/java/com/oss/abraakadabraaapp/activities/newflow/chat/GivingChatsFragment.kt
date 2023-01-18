package com.oss.abraakadabraaapp.activities.newflow.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.utils.Constants

class GivingChatsFragment : Fragment() {
    lateinit var application: BaseActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_giving_chats, container, false)

        application = (activity as BaseActivity)

        application.postEvent(Constants.PAGE_GIVER_CHAT,null)
        return view
    }

}