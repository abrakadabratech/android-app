package com.oss.abraakadabraaapp.activities.newflow.ui.menu

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.ContentManagementActivity
import com.oss.abraakadabraaapp.activities.auth.LoginActivity
import com.oss.abraakadabraaapp.activities.newflow.NewHomeActivity
import com.oss.abraakadabraaapp.databinding.NewMenuScreenBinding
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MenuFragment : Fragment() {
    lateinit var application: BaseActivity

    private var _binding: NewMenuScreenBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var mAuth: FirebaseAuth
    private val authViewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = NewMenuScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root



        mAuth = FirebaseAuth.getInstance()
        application = (activity as BaseActivity)
        application.postEvent(Constants.PAGE_MENU, null)

        binding.supportLayout.setOnClickListener {
            application.postEvent(Constants.BUTTON_SUPPORT, null)


            startActivity(
                ContentManagementActivity.createIntent(
                    requireContext(),
                    Constants.contactUs
                )
            )
        }
        binding.aboutUsLayout.setOnClickListener {
            application.postEvent(Constants.BUTTON_ABOUT_US, null)
            startActivity(
                ContentManagementActivity.createIntent(
                    requireContext(),
                    Constants.aboutUs
                )
            )
        }
        binding.privacyPolicyLayout.setOnClickListener {
            application.postEvent(Constants.BUTTON_PRIVACY_POLICY, null)
            startActivity(
                ContentManagementActivity.createIntent(
                    requireContext(),
                    Constants.privacyPolicy
                )
            )
        }
        binding.logoutLayout.setOnClickListener {
            application.postEvent(Constants.BUTTON_LOGOUT, null)
            var alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Logout")
            alertDialog.setMessage("Are you sure you want to logout ?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, id ->

                application.generateAuthToken()

                val map = HashMap<String, String>()
                val token = PreferencesManagement.getAuthToken(requireActivity())!!
                Log.d(NewHomeActivity.TAG, "Token in Accounts fragment: $token")
                map[RequestKeys.authorization] = token
                authViewModel.logoutUser(map)

                dialog.dismiss()
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
            })
            alertDialog.setCancelable(true)
            alertDialog.show()

        }

        setUpObserver()

        return root
    }

    private fun setUpObserver() {

        authViewModel.logoutNewSuccess.observe(requireActivity()) {
            if (it.code == 200) {
                application.showToast(it.responseMessage.toString())
                Firebase.auth.signOut()
                requireActivity().finish()
                requireActivity().startActivity(
                    Intent(requireActivity(), LoginActivity::class.java)
                )
            }
        }

        authViewModel.errorMessage.observe(requireActivity()) {
            if (it.isNotBlank()) Log.d(
                Constants.API_TAG,
                "setUpObserver: $it"
            )
        }

        authViewModel.isLoading.observe(requireActivity()) { application.loader(it) }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}