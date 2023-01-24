package com.oss.abraakadabraaapp.activities.newflow.ui.accounts

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.auth.AuthUserDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.*
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.databinding.FragmentAccountsBinding
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.API_TAG
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_ON_ACCOUNTS
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountsFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentAccountsBinding? = null

    lateinit var application: BaseActivity

    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentAccountsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        application = (activity as BaseActivity)
        application.postEvent(Constants.PAGE_ACCOUNTS,null)

        binding.ivBack.setOnClickListener{
            application.postClick(BUTTON_BACK_ON_ACCOUNTS)
            requireActivity().onBackPressed()
        }
        binding.myListingLayout.setOnClickListener(this)
        binding.myRequestLayout.setOnClickListener(this)
        binding.myChatsLayout.setOnClickListener(this)
        binding.myAchievementLayout.setOnClickListener(this)
        binding.payAsYouGo.setOnClickListener(this)
        binding.profileActivity.setOnClickListener(this)
        binding.userName.setOnClickListener(this)

        setUpObserver()
        application.generateAuthToken()

        authViewModel.getUser(Utility.getAuthentication(requireContext()))

        return root
    }

    override fun onResume() {
        super.onResume()
        setUpProfile()
    }
    private fun setUpObserver() {

        authViewModel.getUserSuccess.observe(requireActivity()) {
//            showToast(it.responseMessage.toString())
            PreferencesManagement.saveUserInfo(requireContext(),it)
            setUpProfile()
        }

        authViewModel.errorMessage.observe(requireActivity()) { if (it.isNotBlank()) Log.d(
            API_TAG,
            "setUpObserver: $it"
        ) }

        authViewModel.isLoading.observe(requireActivity()) { application.loader(it) }
    }

    private fun setUpProfile() {
        val it: GetUserResponse? = PreferencesManagement.getUserInfo(requireContext())
        if (it != null){
            with(binding){
                userName.text = it.data?.name
                Glide.with(this@AccountsFragment)
                    .load(it.data?.userAvatar)
                    .placeholder(resources.getDrawable(R.drawable.ic_profile))
                    .into(profilePic)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(viewId: View?) {
        when(viewId){
            binding.myListingLayout ->
            {
                application.postClick(Constants.BUTTON_MY_LISTING)
                startActivity(Intent(context, MyListingActivity::class.java))
            }
            binding.myRequestLayout -> {
                application.postClick(Constants.BUTTON_MY_REQUEST)
                startActivity(Intent(context, NewMyRequestActivity::class.java))
            }
            binding.myChatsLayout -> {
                application.postClick(Constants.BUTTON_MY_CHATS)
                startActivity(Intent(context, MyChatsActivity::class.java))
            }
            binding.myAchievementLayout -> {
                application.postClick(Constants.BUTTON_MY_ACHIEVEMENTS)
                startActivity(Intent(context, MyAchievementsActivity::class.java))
            }
            binding.payAsYouGo -> {
                application.postClick(Constants.BUTTON_PAY_AS_YOU_WISH)
                startActivity(Intent(context, MyPayAsYouGoActivity::class.java))
            }
            binding.profileActivity -> {
                application.postClick(Constants.BUTTON_EDIT_PROFILE)
                val intent = Intent(context, MyNewProfileActivity::class.java)
                startActivity(intent)
            }
            binding.userName ->{
                application.postClick(Constants.BUTTON_EDIT_PROFILE)
                startActivity(Intent(context, MyNewProfileActivity::class.java))
            }
        }
    }
}