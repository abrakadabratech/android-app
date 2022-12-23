package com.oss.abraakadabraaapp.activities.newflow.ui.accounts

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.oss.abraakadabraaapp.activities.newflow.*
import com.oss.abraakadabraaapp.databinding.FragmentAccountsBinding

class AccountsFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentAccountsBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.myListingLayout.setOnClickListener(this)
        binding.myRequestLayout.setOnClickListener(this)
        binding.myChatsLayout.setOnClickListener(this)
        binding.myAchievementLayout.setOnClickListener(this)
        binding.payAsYouGo.setOnClickListener(this)
        binding.profileActivity.setOnClickListener(this)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(viewId: View?) {
        when(viewId){
            binding.myListingLayout -> startActivity(Intent(context, MyListingActivity::class.java))
            binding.myRequestLayout -> startActivity(Intent(context, NewMyRequestActivity::class.java))
            binding.myChatsLayout -> startActivity(Intent(context, MyChatsActivity::class.java))
            binding.myAchievementLayout -> startActivity(Intent(context, MyAchievementsActivity::class.java))
            binding.payAsYouGo -> startActivity(Intent(context, MyPayAsYouGoActivity::class.java))
            binding.profileActivity -> startActivity(Intent(context, MyNewProfileActivity::class.java))
        }
    }
}