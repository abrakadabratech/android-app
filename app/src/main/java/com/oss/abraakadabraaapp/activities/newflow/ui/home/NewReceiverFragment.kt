package com.oss.abraakadabraaapp.activities.newflow.ui.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.codersroute.flexiblewidgets.FlexibleSwitch
import com.codersroute.flexiblewidgets.FlexibleSwitch.OnStatusChangedListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.newflow.NewProductDetailActivity
import com.oss.abraakadabraaapp.activities.newflow.NewSearchActivity
import com.oss.abraakadabraaapp.adapter.CategoryAdapter
import com.oss.abraakadabraaapp.adapter.LatestProductAdapter
import com.oss.abraakadabraaapp.databinding.NewReceiverFlowBinding
import com.oss.abraakadabraaapp.response.mainResponse.CategoryData
import com.oss.abraakadabraaapp.response.mainResponse.LatestProductData


class NewReceiverFragment : Fragment(), CategoryAdapter.CategoryAdapterInterface,
    LatestProductAdapter.LatestProductAdapterInterface {

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
    private lateinit var latestProductAdapter: LatestProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = NewReceiverFlowBinding.inflate(inflater, container, false)
        val root: View = binding.root


        categoryAdapter = CategoryAdapter(categoryList, requireContext(), this, "")
        latestProductAdapter = LatestProductAdapter(latestProductList, requireContext(), this)

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

        clickEvents()

//        setUpRecyclerView()

        return root
    }

    private fun clickEvents() {
        binding.catFilter.setOnClickListener {
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
        latestProductAdapter.notifyDataSetChanged()

        binding.sRLHome.isRefreshing = false
        /*val varl = "{\n" +
                "  \"status\": true,\n" +
                "  \"code\": 200,\n" +
                "  \"response_message\": \"Home Data\",\n" +
                "  \"developer_message\": \"\",\n" +
                "  \"data\": {\n" +
                "    \"products\": [\n" +
                "      {\n" +
                "        \"title\": \"Beautiful Toy\",\n" +
                "        \"user_id\": 240,\n" +
                "        \"full_address\": \", Chavarambakam, Andhra Pradesh\",\n" +
                "        \"product_id\": 220,\n" +
                "        \"distance\": 6.921098996597988,\n" +
                "        \"image\": \"https:\\/\\/abrakadabraapp.app\\/app\\/user_assets\\/products\\/product_image_151667237251240.jpg\",\n" +
                "        \"is_given\": 1\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"Bluetooth mouse chip\",\n" +
                "        \"user_id\": 240,\n" +
                "        \"full_address\": \", Elakatur, Andhra Pradesh\",\n" +
                "        \"product_id\": 219,\n" +
                "        \"distance\": 5.598131561363813,\n" +
                "        \"image\": \"https:\\/\\/abrakadabraapp.app\\/app\\/user_assets\\/products\\/product_image_691666876838240.jpg\",\n" +
                "        \"is_given\": 1\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"Airtel Remote\",\n" +
                "        \"user_id\": 245,\n" +
                "        \"full_address\": \", Chavarambakam, Andhra Pradesh\",\n" +
                "        \"product_id\": 218,\n" +
                "        \"distance\": 6.925684134114143,\n" +
                "        \"image\": \"https:\\/\\/abrakadabraapp.app\\/app\\/user_assets\\/products\\/product_image_581666876740245.jpg\",\n" +
                "        \"is_given\": 0\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"SIM Ejector\",\n" +
                "        \"user_id\": 245,\n" +
                "        \"full_address\": \", Chavarambakam, Andhra Pradesh\",\n" +
                "        \"product_id\": 217,\n" +
                "        \"distance\": 6.9155141665630095,\n" +
                "        \"image\": \"https:\\/\\/abrakadabraapp.app\\/app\\/user_assets\\/products\\/product_image_851665927843245.jpg\",\n" +
                "        \"is_given\": 1\n" +
                "      },\n" +
                "      {\n" +
                "        \"title\": \"Bluetooth mouse\",\n" +
                "        \"user_id\": 240,\n" +
                "        \"full_address\": \", Nindra, Andhra Pradesh\",\n" +
                "        \"product_id\": 216,\n" +
                "        \"distance\": 2.2610310994713423,\n" +
                "        \"image\": \"https:\\/\\/abrakadabraapp.app\\/app\\/user_assets\\/products\\/product_image_271665913971240.jpg\",\n" +
                "        \"is_given\": 0\n" +
                "      }\n" +
                "    ],\n" +
                "    \"category\": [\n" +
                "      {\n" +
                "        \"id\": 1,\n" +
                "        \"category_name\": \"Electronics\",\n" +
                "        \"category_image\": \"https:\\/\\/abrakadabraapp.app\\/app\\/user_assets\\/category\\/electronic.png\",\n" +
                "        \"created_at\": \"2021-11-22 19:14:06\",\n" +
                "        \"updated_at\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 2,\n" +
                "        \"category_name\": \"Clothing\",\n" +
                "        \"category_image\": \"https:\\/\\/abrakadabraapp.app\\/app\\/user_assets\\/category\\/cloth.png\",\n" +
                "        \"created_at\": \"2021-11-22 19:14:06\",\n" +
                "        \"updated_at\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 3,\n" +
                "        \"category_name\": \"Books\",\n" +
                "        \"category_image\": \"https:\\/\\/abrakadabraapp.app\\/app\\/user_assets\\/category\\/book 1.png\",\n" +
                "        \"created_at\": \"2021-11-22 19:14:06\",\n" +
                "        \"updated_at\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 4,\n" +
                "        \"category_name\": \"Sports\",\n" +
                "        \"category_image\": \"https:\\/\\/abrakadabraapp.app\\/app\\/user_assets\\/category\\/ball.png\",\n" +
                "        \"created_at\": \"2021-11-22 19:14:06\",\n" +
                "        \"updated_at\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 5,\n" +
                "        \"category_name\": \"Footwear\",\n" +
                "        \"category_image\": \"https:\\/\\/abrakadabraapp.app\\/app\\/user_assets\\/category\\/shose.png\",\n" +
                "        \"created_at\": \"2021-11-22 19:14:06\",\n" +
                "        \"updated_at\": null\n" +
                "      },\n" +
                "      {\n" +
                "        \"id\": 6,\n" +
                "        \"category_name\": \"Fashion\",\n" +
                "        \"category_image\": \"https:\\/\\/abrakadabraapp.app\\/app\\/user_assets\\/category\\/sunglasses 1 (1).png\",\n" +
                "        \"created_at\": \"2021-11-22 19:14:06\",\n" +
                "        \"updated_at\": null\n" +
                "      }\n" +
                "    ],\n" +
                "    \"notification_count\": 1\n" +
                "  }\n" +
                "}"
        var obj: JSONObject = JSONObject(varl)
        var status = obj.get("status")
        var code = obj.getInt("code")
        var response_message = obj.getString("response_message")
        var developer_message = obj.getString("developer_message")

        var data = obj.getJSONObject("data")
        var products = data.getJSONArray("products")

        var catArra:ArrayList<CategoryData> = ArrayList()
        var proArra:ArrayList<LatestProductData> = ArrayList()
        var category = data.getJSONArray("category")

//        for (i in 0..category.length()){
//            var index = products.getJSONObject(i)
//            var cat = CategoryData(categoryImage = index.getString("category_image")
//            , categoryName = index.getString("category_name")
//            , createdAt = index.getString("created_at")
//                ,updatedAt = index.getString("updated_at")
//                , image = "", id = index.getInt("id"))
//            catArra.add(cat)
//        }
        for (i in 0..products.length()){
            var index = products.getJSONObject(i)

            var product = LatestProductData(userId = index.getInt("user_id")
                , title = index.getString("title")
                , fullAddress = index.getString("full_address")
                , productId = index.getInt("product_id")
                , image = index.getString("image")
                , id = 9
                , isGiven = index.getInt("is_given")
                , distance = index.getDouble("distance"))

            proArra.add(product)
        }
        var notification_count = data.get("notification_count")
//        var
        var model = GetHomeDataResponse(status = obj.getBoolean("Status"),
            data = GetHomeDataResponse.Data(catArra,proArra,2),
            responseMessage = response_message,
        code = code, developerMessage = developer_message )
        val data1 = model.data
        val categoryData = data1.category
        val latestProductData = data1.products
//        notificationCount = data.notificationCount.toString()


        if (latestProductData.isNotEmpty()) {
            if (currentPage == pageStart) latestProductList.clear()
            latestProductList.addAll(latestProductData)
            latestProductAdapter.notifyDataSetChanged()
//            noDataBinding.clNoData.visibility = View.GONE

            val lastPosition = latestProductList.size - latestProductData.size

            if (latestProductList.size == latestProductData.size) {
                binding.rvLatestProduct.smoothScrollToPosition(latestProductList.size)
            } else {
                binding.rvLatestProduct.smoothScrollToPosition(lastPosition + 1)
            }

            currentPage += 1
        } else {
            Toast.makeText(context, "No Data", Toast.LENGTH_SHORT).show()
//            noDataFound()
        }

        if (categoryData.isNotEmpty()) {
            categoryList.clear()
            categoryList.addAll(categoryData)
            categoryAdapter.notifyDataSetChanged()
        }

        isLoading = false
        isLastPage = false
//        binding.llProgress.visibility = View.GONE
        binding.sRLHome.isRefreshing = false*/

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

        binding.rvLatestProduct.apply {
            layoutManager = lm
            adapter = latestProductAdapter
//            isNestedScrollingEnabled = false
            recycledViewPool.setMaxRecycledViews(1, 0)
            setHasFixedSize(false)
        }
        var firstVisibleInListview: Int
        val activity = requireView().context as AppCompatActivity
        val chipNavigationBar =
            activity.findViewById<BottomNavigationView>(com.oss.abraakadabraaapp.R.id.nav_view)

        binding.rvLatestProduct.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, oldScrollY: Int) {
                super.onScrolled(recyclerView, dx, oldScrollY)
                val total: Int = lm.itemCount
                val lastVisibleItemCount: Int = lm.findLastVisibleItemPosition()


//                if (total == lastVisibleItemCount) {
//
//                }



                if (!isLoading) {
                    if (total > 0) if (total - 1 == lastVisibleItemCount) {
                        if (!noMoreData) {
                            isLoading = true
                            isLastPage = true
                            //getHomeData()
                        }
                    }
                }
            }

        })

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

        nearToMeSwitch.addOnStatusChangedListener(OnStatusChangedListener {
            if (it) nearToMeTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            else nearToMeTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
        })

        newestFirstSwitch.addOnStatusChangedListener(OnStatusChangedListener {
            if (it) newestFirstTxt.setTextColor(resources.getColor(R.color.cat_select_color))
            else newestFirstTxt.setTextColor(resources.getColor(R.color.cat_unselect_color))
        })

        applyBtn.setOnClickListener { Toast.makeText(context, "Under Development", Toast.LENGTH_SHORT).show() }

        val alertDialog: AlertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialog.show()
        alertDialog.window?.setLayout(800, 700)

    }

}