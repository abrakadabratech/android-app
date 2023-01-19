package com.oss.abraakadabraaapp.activities.newflow.ui.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codersroute.flexiblewidgets.FlexibleSwitch
import com.codersroute.flexiblewidgets.FlexibleSwitch.OnStatusChangedListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.NewProductDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.NewSearchActivity
import com.oss.abraakadabraaapp.adapter.CategoryAdapter
import com.oss.abraakadabraaapp.adapter.LatestProductAdapter
import com.oss.abraakadabraaapp.databinding.NewReceiverFlowBinding
import com.oss.abraakadabraaapp.datasource.ProductsAdapter
import com.oss.abraakadabraaapp.datasource.ProductsViewModel
import com.oss.abraakadabraaapp.datasource.ProductsViewModelFactory
import com.oss.abraakadabraaapp.response.mainResponse.CategoryData
import com.oss.abraakadabraaapp.response.mainResponse.LatestProductData
import com.oss.abraakadabraaapp.retrofit.api.APIs
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.customView.MarginItemDecoration
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class NewReceiverFragment : Fragment(), CategoryAdapter.CategoryAdapterInterface,
    LatestProductAdapter.LatestProductAdapterInterface {
    lateinit var application: BaseActivity

    private var _binding: NewReceiverFlowBinding? = null
    private val binding get() = _binding!!

    private var categoryList: ArrayList<CategoryData> = ArrayList()
    private lateinit var categoryAdapter: CategoryAdapter
    private var pageStart = 1
    private var currentPage = pageStart
    private var noMoreData = false
    private var isLoading = false
    private var isLastPage = false

    private var latestProductList: ArrayList<LatestProductData> = ArrayList()
//    private lateinit var latestProductAdapter: LatestProductAdapter


    //Pagination
    lateinit var passengersViewModel: ProductsViewModel
    lateinit var passengersAdapter: ProductsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = NewReceiverFlowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        application = (activity as BaseActivity)
        application.postEvent(Constants.PAGE_RECEIVER,null)

        categoryAdapter = CategoryAdapter(categoryList, requireContext(), this, "")
//        latestProductAdapter = LatestProductAdapter(latestProductList, requireContext(), this)

        with(binding) {
//            sRLHome.setColorSchemeResources(R.color.theme_color)
            sRLHome.setOnRefreshListener {
                currentPage = pageStart
                latestProductList.clear()
                categoryList.clear()
                noMoreData = false
                currentPage = pageStart
                getHomeData()

            }
        }
        setupViewModel()

        setupView()

        setupList()


        clickEvents()

//        setUpRecyclerView()

        return root
    }
    private fun setupView() {
        passengersAdapter = ProductsAdapter()
        binding.rvLatestProduct.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = passengersAdapter
            setHasFixedSize(true)
        }
    }

    private fun setupList() {
        lifecycleScope.launch {
            passengersViewModel.products.collectLatest { pagedData ->
                passengersAdapter.submitData(pagedData)
            }
        }
    }

    private fun setupViewModel() {
        val userLocation = PreferencesManagement.getUserLocation(requireContext())
        application.generateAuthToken()
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(requireContext())!!
        map[RequestKeys.authorization] = token
        val factory = ProductsViewModelFactory(1, APIs(),map,800,userLocation!!.lat.toDouble(),
            userLocation.long.toDouble())
        passengersViewModel = ViewModelProvider(this, factory).get(ProductsViewModel::class.java)
    }
    private fun clickEvents() {
        binding.catFilter.setOnClickListener {
            application.postEvent(Constants.BUTTON_FILTER,null)

            showNearByFilterDialog()
//            startActivity(Intent(requireContext(),CategorySelectActivity::class.java))
        }
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRecyclerView()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCategoryClick(data: CategoryData) {
        startActivity(Intent(context, NewSearchActivity::class.java))
    }

    override fun onItemDetail(data: LatestProductData, position: Int) {
        startActivity(Intent(context, NewProductDetailActivity::class.java))
    }
    private fun getHomeData() {

        var catList: ArrayList<CategoryData> = ArrayList()

        var cat = CategoryData(
            "https://www.gstatic.com/webp/gallery/1.jpg",
            "Electronics",
            R.drawable.temp_one,
            "2021-11-22 19:14:06",
            1,
            ""
        )

        categoryList.add(cat)
        categoryList.add(CategoryData(
            "https://www.gstatic.com/webp/gallery/1.jpg",
            "Books",
            R.drawable.temp_two,
            "2021-11-22 19:14:06",
            1,
            ""
        ))
        categoryList.add(CategoryData(
            "https://www.gstatic.com/webp/gallery/1.jpg",
            "Furniture",
            R.drawable.temp_three,
            "2021-11-22 19:14:06",
            1,
            ""
        ))
        categoryList.add(CategoryData(
            "https://www.gstatic.com/webp/gallery/1.jpg",
            "Home Decor",
            R.drawable.temp_four,
            "2021-11-22 19:14:06",
            1,
            ""
        ))
        categoryList.add(CategoryData(
            "https://www.gstatic.com/webp/gallery/1.jpg",
            "Fashion",
            R.drawable.temp_five,
            "2021-11-22 19:14:06",
            1,
            ""
        ))
        categoryList.add(CategoryData(
            "https://www.gstatic.com/webp/gallery/1.jpg",
            "Electronics",
            R.drawable.temp_six,
            "2021-11-22 19:14:06",
            1,
            ""
        ))
        categoryList.add(CategoryData(
            "https://www.gstatic.com/webp/gallery/1.jpg",
            "More",
            R.drawable.temp_seven,
            "2021-11-22 19:14:06",
            1,
            ""
        ))

        var prod = LatestProductData(
            6.921098996597988,
            "Barely Used",
            240,
            "https://www.gstatic.com/webp/gallery/1.jpg",
            220,
            2,
            "Beautiful Toy",
            2
        )
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)
        latestProductList.add(prod)

        categoryAdapter.notifyDataSetChanged()
//        latestProductAdapter.notifyDataSetChanged()

        binding.sRLHome.isRefreshing = false


    }

    private fun setUpRecyclerView() {
//        val view = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

//        view.visibility = View.GONE
//        val navBar = requireActivity().findViewById<BottomNavigationView>(R.id.nav_view)

        val lm = GridLayoutManager(requireContext(), 2)
//        binding.rvHomeCategory.isNestedScrollingEnabled = false

        binding.rvHomeCategory.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = categoryAdapter
            recycledViewPool.setMaxRecycledViews(1, 0)
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
        }

        /*binding.rvLatestProduct.apply {
            layoutManager = lm
            addItemDecoration(
                MarginItemDecoration(18)
            )
            adapter = latestProductAdapter
//            isNestedScrollingEnabled = false
            recycledViewPool.setMaxRecycledViews(1, 0)
            setHasFixedSize(false)
        }*/

        getHomeData()
    }

    //Alert Dialog for show nearby filter
    private fun showNearByFilterDialog() {

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_nearby_filter, null)
        dialogBuilder.setView(dialogView)

        val nearToMeTxt = dialogView.findViewById<TextView>(R.id.nearToMeTxt)
        val nearToMeSwitch = dialogView.findViewById<FlexibleSwitch>(R.id.nearToMeSwitch)

        val newestFirstTxt = dialogView.findViewById<TextView>(R.id.newrstFirstTxt)
        val newestFirstSwitch = dialogView.findViewById<FlexibleSwitch>(R.id.newestFirstSwitch)

        val applyBtn = dialogView.findViewById<TextView>(R.id.applyBtn)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)

        nearToMeSwitch.addOnStatusChangedListener(OnStatusChangedListener {
            if (it) nearToMeTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            else nearToMeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
        })

        newestFirstSwitch.addOnStatusChangedListener(OnStatusChangedListener {
            if (it) newestFirstTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            else newestFirstTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
        })

        applyBtn.setOnClickListener {
            application.postEvent(Constants.BUTTON_FILTER_APPLY,null)
            Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show() }

        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()

        closeBtn.setOnClickListener {
            alertDialog.dismiss()
        }
        alertDialog.window?.setLayout(800, 700)

    }

}