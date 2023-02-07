package com.oss.abraakadabraaapp.activities.newflow.customeview

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.oss.abraakadabraaapp.activities.newflow.chat.ChatDetailActivity

class WrapContentLinearLayoutManager(context: Context) : LinearLayoutManager(context) {
    //... constructor
    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        try {
            super.onLayoutChildren(recycler, state)
        } catch (e: IndexOutOfBoundsException) {
            Log.e("TAG", "meet a IOOBE in RecyclerView")
        }
    }
}