package com.oss.abraakadabraaapp.activities.newflow.ui.home

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.NotificationActivity
import com.oss.abraakadabraaapp.activities.newflow.NewNotificationActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.SocialShareAdapter
import com.oss.abraakadabraaapp.databinding.FragmentHomeBinding
import org.greenrobot.eventbus.EventBus

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //setStatusBarGradiant(requireActivity())

        /*val nestedNavHostFragment =
            childFragmentManager.findFragmentById(R.id.fragmentContainer) as? NavHostFragment
        val navController = nestedNavHostFragment?.navController

        navController?.navigate(R.id.receiverFragment)

        val navHostFragment =
            childFragmentManager.findFragmentById(R.id.fragmentContainer)

        // ChildFragmentManager of the current NavHostFragment
        val navHostChildFragmentManager = navHostFragment?.childFragmentManager

        navHostChildFragmentManager?.addOnBackStackChangedListener {

            val backStackEntryCount = navHostChildFragmentManager!!.backStackEntryCount
            val fragments = navHostChildFragmentManager!!.fragments

            Toast.makeText(
                requireContext(),
                "HomeNavHost backStackEntryCount: $backStackEntryCount, fragments: $fragments",
                Toast.LENGTH_SHORT
            ).show()
        }
        */

        val fragmentManager: FragmentManager = requireFragmentManager()
        fragmentManager.beginTransaction()
            .replace(R.id.container, NewReceiverFragment::class.java, null)
            .setReorderingAllowed(true)
//            .addToBackStack("name") // name can be null
            .commit()

        binding.receiveBtn.setOnClickListener {
//            Toast.makeText(context, "Receiver", Toast.LENGTH_LONG).show()
            // Load receiver fragment
            binding.receiveBtn.background = resources.getDrawable(R.drawable.rounded_rect_shape)
            binding.receiveBtn.setTextColor(resources.getColor(R.color.new_action_bar_title_color))
            binding.giveBtn.setTextColor(resources.getColor(R.color.hyper_link_text_color))
            binding.giveBtn.background = null
            fragmentManager.beginTransaction()
                .replace(R.id.container, NewReceiverFragment::class.java, null)
                .setReorderingAllowed(true)
//                .addToBackStack("name") // name can be null
                .commit()
            EventBus.getDefault().post(1)
        }
        binding.giveBtn.setOnClickListener(View.OnClickListener {
//            Toast.makeText(context,"Giver",Toast.LENGTH_LONG).show()
            // Load receiver fragment
            EventBus.getDefault().post(0)

            binding.receiveBtn.background = null
            binding.giveBtn.background = resources.getDrawable(R.drawable.rounded_rect_shape)
            binding.receiveBtn.setTextColor(resources.getColor(R.color.hyper_link_text_color))
            binding.giveBtn.setTextColor(resources.getColor(R.color.new_action_bar_title_color))
            fragmentManager.beginTransaction()
                .replace(R.id.container, NewGiverFragment::class.java, null)
                .setReorderingAllowed(true)
//                .addToBackStack("name") // name can be null
                .commit()
        })
        binding.shareAKD.setOnClickListener {
            loadData()
        }
        binding.notifications.setOnClickListener {
            startActivity(Intent(requireActivity(), NewNotificationActivity::class.java))
        }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun setStatusBarGradiant(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window: Window = activity.window
            val background = ContextCompat.getDrawable(activity, R.drawable.gradient_theme)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            window.statusBarColor = ContextCompat.getColor(activity, android.R.color.transparent)
            window.navigationBarColor =
                ContextCompat.getColor(activity, android.R.color.transparent)
            window.setBackgroundDrawable(background)
        }
    }

    private fun loadData() {

        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "Subject test")
        i.putExtra(Intent.EXTRA_TEXT, "Try this great app Abra Ka Dabra to share second hand products with others for free. App is available at the below link: link")
        startActivity(Intent.createChooser(i, "Share"))

//        val modalBottomSheet = ModalBottomSheet()
//
//        modalBottomSheet.show(requireActivity().supportFragmentManager, ModalBottomSheet.TAG)

    }
}


class ModalBottomSheet : BottomSheetDialogFragment() {
    //    private lateinit var bottomBinding : ShareBottomSheetBinding
    private lateinit var rvSocialLinks: RecyclerView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.share_bottom_sheet, container, false)
        rvSocialLinks = view.findViewById(R.id.rv_social_links)
        var images: Array<Int> = arrayOf(
            R.drawable.fb_icon, R.drawable.linked_in_icon, R.drawable.twitter_icon,
            R.drawable.insta_icon, R.drawable.whatsapp_icon, R.drawable.ic_mail_icon
        )
        var adapter = SocialShareAdapter(requireActivity(), images)
        var layoutManager = GridLayoutManager(requireContext(), 3)

        rvSocialLinks.layoutManager = layoutManager
        rvSocialLinks.adapter = adapter

        return view
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }

    override fun getTheme(): Int {
        return R.style.BottomSheetDialogTheme
    }
}