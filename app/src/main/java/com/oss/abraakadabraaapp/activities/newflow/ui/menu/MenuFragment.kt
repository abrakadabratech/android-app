package com.oss.abraakadabraaapp.activities.newflow.ui.menu

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
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
import com.oss.abraakadabraaapp.databinding.NewMenuScreenBinding
import com.oss.abraakadabraaapp.utils.Constants

class MenuFragment : Fragment() {
    lateinit var application: BaseActivity

    private var _binding: NewMenuScreenBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var mAuth : FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = NewMenuScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root



        mAuth = FirebaseAuth.getInstance()
        application = (activity as BaseActivity)
        application.postEvent(Constants.PAGE_MENU,null)

        binding.supportLayout.setOnClickListener {
            application.postEvent(Constants.BUTTON_SUPPORT,null)
            startActivity(ContentManagementActivity.createIntent(
                requireContext() ,
                Constants.contactUs
            ))
        }
        binding.aboutUsLayout.setOnClickListener {
            application.postEvent(Constants.BUTTON_ABOUT_US,null)
            startActivity(ContentManagementActivity.createIntent(
                requireContext(),
                Constants.aboutUs
            ))
        }
        binding.privacyPolicyLayout.setOnClickListener {
            application.postEvent(Constants.BUTTON_PRIVACY_POLICY,null)
            startActivity(ContentManagementActivity.createIntent(
                requireContext(),
                Constants.privacyPolicy
            ))
        }
        binding.logoutLayout.setOnClickListener {
            application.postEvent(Constants.BUTTON_LOGOUT,null)
            var alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Logout")
            alertDialog.setMessage("Are you sure you want to logout ?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener{ dialog, id ->
                Firebase.auth.signOut()
                requireActivity().finish()
                requireActivity().startActivity(
                    Intent(requireActivity(),LoginActivity::class.java))

                dialog.dismiss()
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener{ dialog, id ->
                dialog.dismiss()
            })
            alertDialog.setCancelable(true)
            alertDialog.show()

        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}