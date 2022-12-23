package com.oss.abraakadabraaapp.activities.newflow.ui.menu

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.oss.abraakadabraaapp.activities.ContentManagementActivity
import com.oss.abraakadabraaapp.databinding.NewMenuScreenBinding
import com.oss.abraakadabraaapp.utils.Constants

class MenuFragment : Fragment() {

    private var _binding: NewMenuScreenBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = NewMenuScreenBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.supportLayout.setOnClickListener {
            startActivity(ContentManagementActivity.createIntent(
                requireContext() ,
                Constants.contactUs
            ))
        }
        binding.aboutUsLayout.setOnClickListener {
            startActivity(ContentManagementActivity.createIntent(
                requireContext(),
                Constants.aboutUs
            ))
        }
        binding.privacyPolicyLayout.setOnClickListener {
            startActivity(ContentManagementActivity.createIntent(
                requireContext(),
                Constants.privacyPolicy
            ))
        }
        binding.logoutLayout.setOnClickListener {
            var alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Logout")
            alertDialog.setMessage("Are you sure you want to logout ?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener{ dialog, id ->
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