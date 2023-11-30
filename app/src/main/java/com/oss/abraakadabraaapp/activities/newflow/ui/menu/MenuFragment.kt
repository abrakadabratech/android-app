package com.oss.abraakadabraaapp.activities.newflow.ui.menu

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.ContentManagementActivity
import com.oss.abraakadabraaapp.activities.auth.LoginActivity
import com.oss.abraakadabraaapp.activities.newflow.TermsAndConditionsActivity
import com.oss.abraakadabraaapp.databinding.NewMenuScreenBinding
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.SIGN_IN_METHOD_GOOGLE
import com.oss.abraakadabraaapp.utils.Constants.SIGN_IN_METHOD_PHONE
import com.oss.abraakadabraaapp.utils.Constants.aboutUs
import com.oss.abraakadabraaapp.utils.Constants.privacyPolicy
import com.oss.abraakadabraaapp.utils.Constants.termsConditions
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MenuFragment : Fragment() {
    lateinit var application: BaseActivity

    private var _binding: NewMenuScreenBinding? = null
    private lateinit var mGoogleSignInClient: GoogleSignInClient

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var mAuth: FirebaseAuth
    private val authViewModel: AuthViewModel by viewModel()
    private val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = NewMenuScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        Log.d(TAG, "MenuFragment onCreateView: called")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.your_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);
        mAuth = FirebaseAuth.getInstance()
        application = (activity as BaseActivity)
        application.postEvent(Constants.PAGE_MENU, null)

        binding.supportLayout.setOnClickListener {
            application.postClick(Constants.BUTTON_SUPPORT)


            startActivity(
                ContentManagementActivity.createIntent(
                    requireContext(),
                    Constants.contactUs
                )
            )
        }
        binding.aboutUsLayout.setOnClickListener {
            application.postClick(Constants.BUTTON_ABOUT_US)
            val intent = Intent(requireContext(), TermsAndConditionsActivity::class.java)
            intent.putExtra("FROM_KEY", aboutUs)
            startActivity(intent)
            /*startActivity(
                ContentManagementActivity.createIntent(
                    requireContext(),
                    Constants.aboutUs
                )
            )*/
        }
        binding.privacyPolicyLayout.setOnClickListener {
            application.postClick(Constants.BUTTON_PRIVACY_POLICY)
            val intent = Intent(requireContext(), TermsAndConditionsActivity::class.java)
            intent.putExtra("FROM_KEY", privacyPolicy)
            startActivity(intent)
            /*startActivity(
                ContentManagementActivity.createIntent(
                    requireContext(),
                    Constants.privacyPolicy
                )
            )*/
        }
        binding.termsAndConditions.setOnClickListener {
            val intent = Intent(requireContext(), TermsAndConditionsActivity::class.java)
            intent.putExtra("FROM_KEY", termsConditions)
            startActivity(intent)
        }
        binding.logoutLayout.setOnClickListener {
            application.postClick(Constants.BUTTON_LOGOUT)
            var alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Logout")
            alertDialog.setMessage("Are you sure you want to logout ?")

            alertDialog.setPositiveButton("Yes") { dialog, id ->

                signoutFromFirebase()

                dialog.dismiss()
            }
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener { dialog, id ->
                dialog.dismiss()
            })
            alertDialog.setCancelable(true)
            alertDialog.show()

        }

        setUpObserver()

        return root
    }

    private fun signoutFromFirebase() {
        val activity: Activity? = activity
        if (activity != null) {
            val mUser = FirebaseAuth.getInstance().currentUser
            if (mUser != null) {
                mUser.getIdToken(true)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            val idToken = it.result.token
                            val auth = "Bearer $idToken"
                            if (PreferencesManagement.saveAuthToken(requireActivity(), auth)) {

                                val map = HashMap<String, String>()
                                val token =
                                    PreferencesManagement.getAuthToken(requireContext())!!
                                map["Authorization"] = token
                                authViewModel.logoutUser(map)


                            } else {
                                application.showToast("Error generating the token!")
                            }

                        }
                    }
            } else {
                Firebase.auth.signOut()
                requireActivity().startActivity(
                    Intent(requireActivity(), LoginActivity::class.java)
                )
                requireActivity().finish()
            }
        }

    }

    private fun setUpObserver() {
        val activity: Activity? = activity
        if (activity != null) {
            authViewModel.logoutNewSuccess.observe(requireActivity()) {
                if (it.code == 200) {

                    PreferencesManagement.saveUserProfileFlag(requireContext(), false)
                    PreferencesManagement.saveUserFlag(requireContext(), false)
                    PreferencesManagement.saveUserInfo(requireContext(), null)
                    PreferencesManagement.saveUserName(requireContext(), "")
                    PreferencesManagement.saveUserEmail(requireContext(), "")

                    Firebase.auth.signOut()

                    if (PreferencesManagement.getSignInMethod(requireContext()) == SIGN_IN_METHOD_GOOGLE) {
                        mGoogleSignInClient.revokeAccess()
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    // Google access revoked
                                    signoutFromFirebase()
                                } else {
                                    // Handle error
                                    application.showToast("Error! Please try again.")
                                }
                            }
                    }
                    application.showToast(it.responseMessage.toString())
                    PreferencesManagement.saveSignInMethod(requireContext(), "")

                    activity.startActivity(
                        Intent(requireActivity(), LoginActivity::class.java)
                    )
                    activity.finish()
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

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}