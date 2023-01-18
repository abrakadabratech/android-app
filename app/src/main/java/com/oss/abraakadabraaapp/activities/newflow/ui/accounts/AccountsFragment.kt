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
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.auth.AuthUserDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.*
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.databinding.FragmentAccountsBinding
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
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

        binding.ivBack.setOnClickListener{  requireActivity().onBackPressed() }
        binding.myListingLayout.setOnClickListener(this)
        binding.myRequestLayout.setOnClickListener(this)
        binding.myChatsLayout.setOnClickListener(this)
        binding.myAchievementLayout.setOnClickListener(this)
        binding.payAsYouGo.setOnClickListener(this)
        binding.profileActivity.setOnClickListener(this)
        binding.userName.setOnClickListener(this)

        setUpObserver()
        application.generateAuthToken()

        val map = HashMap<String,String>()
        val token = PreferencesManagement.getAuthToken(requireActivity())!!
        Log.d(NewHomeActivity.TAG, "Token in Accounts fragment: $token")
        map[RequestKeys.authorization] = token
        authViewModel.getUser(map)

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

        authViewModel.errorMessage.observe(requireActivity()) { if (it.isNotBlank()) application.showToast(it) }

        authViewModel.isLoading.observe(requireActivity()) { application.loader(it) }
    }

    private fun setUpProfile() {
        val it: GetUserResponse? = PreferencesManagement.getUserInfo(requireContext())
        if (it != null){
            with(binding){
                userName.text = it.data?.name
                Glide.with(this@AccountsFragment)
                    .load(it.data?.userAvatar)
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
                application.postEvent(Constants.BUTTON_MY_LISTING,null)
                startActivity(Intent(context, MyListingActivity::class.java))
            }
            binding.myRequestLayout -> {
                application.postEvent(Constants.BUTTON_MY_REQUEST,null)
                startActivity(Intent(context, NewMyRequestActivity::class.java))
            }
            binding.myChatsLayout -> {
                application.postEvent(Constants.BUTTON_MY_CHATS,null)
                startActivity(Intent(context, MyChatsActivity::class.java))
            }
            binding.myAchievementLayout -> {
                application.postEvent(Constants.BUTTON_MY_ACHIEVEMENTS,null)
                startActivity(Intent(context, MyAchievementsActivity::class.java))
            }
            binding.payAsYouGo -> {
                application.postEvent(Constants.BUTTON_PAY_AS_YOU_WISH,null)
                startActivity(Intent(context, MyPayAsYouGoActivity::class.java))
            }
            binding.profileActivity -> {
                application.postEvent(Constants.BUTTON_EDIT_PROFILE,null)
                val intent = Intent(context, MyNewProfileActivity::class.java)
                startActivity(intent)
            }
            binding.userName ->{
                application.postEvent(Constants.BUTTON_EDIT_PROFILE,null)
                startActivity(Intent(context, MyNewProfileActivity::class.java))
            }
        }
    }
}