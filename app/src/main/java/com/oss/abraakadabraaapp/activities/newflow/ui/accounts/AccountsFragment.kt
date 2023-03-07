package com.oss.abraakadabraaapp.activities.newflow.ui.accounts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.*
import com.oss.abraakadabraaapp.activities.newflow.apimodels.GetUserResponse
import com.oss.abraakadabraaapp.activities.newflow.apimodels.User_Stats
import com.oss.abraakadabraaapp.databinding.FragmentAccountsBinding
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.API_TAG
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_ON_ACCOUNTS
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountsFragment : Fragment(), View.OnClickListener {

    private lateinit var binding: FragmentAccountsBinding

    lateinit var application: BaseActivity

//    private val binding get() = _binding!!
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        binding = FragmentAccountsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        application = (activity as BaseActivity)
        application.postEvent(Constants.PAGE_ACCOUNTS,null)

        binding.ivBack.setOnClickListener{
            application.postClick(BUTTON_BACK_ON_ACCOUNTS)
            val activity: Activity? = activity
            if (activity != null) {
                requireActivity().onBackPressed()
            }
        }
        setUpProfile(PreferencesManagement.getUserInfo(requireContext())!!)
        binding.myListingLayout.setOnClickListener(this)
        binding.myRequestLayout.setOnClickListener(this)
        binding.myChatsLayout.setOnClickListener(this)
        binding.myAchievementLayout.setOnClickListener(this)
        binding.payAsYouGo.setOnClickListener(this)
        binding.profileActivity.setOnClickListener(this)
        binding.userName.setOnClickListener(this)

        setUpObserver()

        val db = Firebase.firestore
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

        val docRef = db.collection("user_stats")
            .document(currentUserId!!).addSnapshotListener { value, error ->
                val info = value?.toObject(User_Stats::class.java)
//                    application.showToast(info.toString())
                binding.giversCount.text = if(info?.given != 0)
                    info?.given.toString() + " Items" else "0 Items"
                binding.receiverCount.text = if(info?.received != 0)
                    info?.received.toString() + " Items" else "0 Items"
            }

        /*docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val info = document.toObject(User_Stats::class.java)
//                    application.showToast(info.toString())
                    binding.giversCount.text = if(info?.given != 0)
                        info?.given.toString() + " Items" else "0 Items"
                    binding.receiverCount.text = if(info?.received != 0)
                        info?.received.toString() + " Items" else "0 Items"
                    Log.d("TAG", "DocumentSnapshot data: ${document.data}")
                } else {
                    Log.d("TAG", "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d("TAG", "get failed with ", exception)
            }*/
        /*docRef.get().addOnSuccessListener {

        }*/
//        callUser()

        return root
    }

    override fun onStart() {
        super.onStart()
        callUser()

    }
    private fun callUser() {
        application.generateAuthToken()

        authViewModel.getUser(Utility.getAuthentication(requireContext()))
    }

    private fun setUpObserver() {
        authViewModel.getUserSuccess.observe(requireActivity()) {
           // application.showToast(it.data.toString())
//                PreferencesManagement.saveUserInfo(requireContext(),it)

            setUpProfileFromServer(it)
        }

        authViewModel.errorMessage.observe(requireActivity()) { if (it.isNotBlank()) Log.d(
            API_TAG,
            "setUpObserver: $it"
        ) }

        authViewModel.isLoading.observe(requireActivity()) { application.loader(it) }
    }

    private fun setUpProfileFromServer(getUserResponse: GetUserResponse?) {
//        application.showToast("${getUserResponse?.data?.userStats?.given} Received ${getUserResponse?.data?.userStats?.received} ")
        with(binding){
            userName.text = if(getUserResponse?.data?.name != null) getUserResponse.data?.name.toString() else "Set Ur Name"
//                giversCount.text = it.data?.userStats?.given.toString()
          /*  giversCount.text = if(getUserResponse?.data?.userStats?.given != null)
                getUserResponse.data?.userStats?.given.toString()+" Items" else "0 Items"
            receiverCount.text = if(getUserResponse?.data?.userStats?.received != null)
                getUserResponse.data?.userStats?.received.toString()+" Items" else "0 Items"*/
            Glide.with(this@AccountsFragment)
                .load(getUserResponse?.data?.userAvatar)
                .placeholder(resources.getDrawable(R.drawable.user))
                .into(profilePic)
        }
        PreferencesManagement.saveUserInfo(requireActivity(),getUserResponse)
    }

    private fun setUpProfile(getUserResponse: GetUserResponse) {
        val it: GetUserResponse? = PreferencesManagement.getUserInfo(requireContext())
        if (it != null){
            with(binding){
                userName.text = if(it.data?.name != null) it.data?.name.toString() else "Set Ur Name"
//                giversCount.text = it.data?.userStats?.given.toString()
//                giversCount.text = if(it.data?.userStats?.given != null) it.data?.userStats?.given.toString()+" Items" else "0 Items"
//                receiverCount.text = if(it.data?.userStats?.received != null) it.data?.userStats?.received.toString()+" Items" else "0 Items"
//                receiverCount.text = it.data?.userStats?.received.toString()
                Glide.with(this@AccountsFragment)
                    .load(it.data?.userAvatar)
                    .placeholder(resources.getDrawable(R.drawable.ic_profile))
                    .into(profilePic)
            }
        }
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
                intent.putExtra("from","fragment")
                startActivity(intent)
            }
            binding.userName ->{
                application.postClick(Constants.BUTTON_EDIT_PROFILE)
                val intent = Intent(context, MyNewProfileActivity::class.java)
                intent.putExtra("from","fragment")
                startActivity(intent)            }
        }
    }
}