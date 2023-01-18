package com.oss.abraakadabraaapp.activities.newflow.ui.community

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.CommunityAdapter
import com.oss.abraakadabraaapp.databinding.FragmentCommunityBinding
import com.oss.abraakadabraaapp.utils.Constants

class CommunityFragment : Fragment() {

    private var _binding: FragmentCommunityBinding? = null
    lateinit var application: BaseActivity

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentCommunityBinding.inflate(inflater, container, false)
        val root: View = binding.root
        application = (activity as BaseActivity)
        application.postEvent(Constants.PAGE_COMMUNITY,null)

        setUpRecyclerView()

        return root
    }

    private fun setUpRecyclerView() {
        var adapter = CommunityAdapter(requireContext(),4)
        binding.rvCommunity.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCommunity.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}