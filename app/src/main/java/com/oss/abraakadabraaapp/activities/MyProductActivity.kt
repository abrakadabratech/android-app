package com.oss.abraakadabraaapp.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.adapter.MyProductAdapter
import com.oss.abraakadabraaapp.databinding.ActivityMyProductBinding
import com.oss.abraakadabraaapp.databinding.LoggedInUserToolbarBinding
import com.oss.abraakadabraaapp.databinding.NoDataLayoutBinding
import com.oss.abraakadabraaapp.model.EditProductData
import com.oss.abraakadabraaapp.response.mainResponse.ProductData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class MyProductActivity : BaseActivity(), MyProductAdapter.MyProductAdapterInterface {

    private lateinit var binding: ActivityMyProductBinding
    private lateinit var includeToolbar: LoggedInUserToolbarBinding
    private lateinit var includeNoData: NoDataLayoutBinding

    private val userData by lazy { PreferencesManagement.getUserData(this)!! }

    private val mainViewModel: MainViewModel by viewModel()

    private var pageStart = 1
    private var currentPage = pageStart

    private var isLoading = false
    private var isLastPage = false
    private var noMoreData = false

    private var myProductList: ArrayList<ProductData> = ArrayList()
    private val myProductAdapter = MyProductAdapter(myProductList, this, this)

    private var isEdit = false
    private var isRefresh = false
    private var position = 0

    private var productData: ProductData? = null

    private var launchAddProductActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                val intentData: String = data!!.getStringExtra(Constants.success)!!
                if (intentData == Constants.success) {
                    isRefresh = true
                    getProductDetail()
                }

                if (intentData == Constants.failure) {
                    myProductList.removeAt(position)
                    myProductAdapter.notifyItemChanged(position)
                }
            }
        }

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, MyProductActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyProductBinding.inflate(layoutInflater)
        includeToolbar = binding.includeToolbar
        includeNoData = binding.includeNoData
        val view = binding.root
        setContentView(view)

        setSupportActionBar(includeToolbar.toolbar)
        includeToolbar.tvToolbarTitle.text =
            applicationContext.resources.getString(R.string.my_product)

        includeToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        binding.srl.setOnRefreshListener {
            currentPage = pageStart
            isLoading = false
            isLastPage = false
            noMoreData = false
            myProductAdapter.clearData()
            getMyProductList()
        }

        setUpRecyclerView()
        setUpObserver()
        getMyProductList()
    }

    private fun setUpObserver() {

        mainViewModel.getProductDetailSuccess.observe(this) {

            if (it.data != null) {
                val productData = it.data

                val editProductData = EditProductData(
                    productData.id.toString(),
                    productData.title,
                    productData.genderFor,
                    productData.categoryId.toString(),
                    productData.brand,
                    productData.productAge,
                    productData.quality,
                    productData.fullAddress,
                    productData.lat,
                    productData.lng,
                    productData.description,
                    productData.images,
                )

                if (isEdit) {
                    isEdit = false
                    val intent = Intent(this@MyProductActivity, AddProductActivity::class.java)
                    intent.putExtra(Constants.editProduct, editProductData)
                    launchAddProductActivity.launch(intent)
                }
                if (isRefresh) {
                    isRefresh = false
                    if (productData.images.isNotEmpty()) {
                        myProductList[position].image = productData.images[0].image
                    }
                    myProductList[position].title = productData.title
                    myProductList[position].fullAddress = productData.fullAddress

                    myProductAdapter.notifyItemChanged(position)
                }
            }

        }

        mainViewModel.deleteProductSuccess.observe(this, {
            showToast(it.responseMessage)
            if (myProductList.isEmpty()) {
                getMyProductList()
            }
        })

        mainViewModel.getMyProductListSuccess.observe(this, {
            val data = it.data
            if (data.isNotEmpty()) {
                if (currentPage == pageStart) myProductList.clear()
                myProductList.addAll(data)
                myProductAdapter.notifyDataSetChanged()
                includeNoData.clNoData.visibility = View.GONE
                binding.rvMyProduct.visibility = View.VISIBLE
                currentPage += 1
            } else {
                noDataFound()
            }
            isLoading = false
            isLastPage = false
            binding.srl.isRefreshing = false
            binding.llProgress.visibility = View.GONE
        })

        mainViewModel.isLoading.observe(this, {
            if (isLoading && isLastPage) {
                binding.llProgress.visibility = View.VISIBLE
            } else {
                binding.llProgress.visibility = View.GONE
                if (!binding.srl.isRefreshing) loader(it)
            }
        })

        mainViewModel.errorMessage.observe(this, { if (it.isNotBlank()) showToast(it) })
    }

    private fun getMyProductList() {
        if (isNetworkAvailable()) {

            includeNoData.clNoData.visibility = View.GONE
            binding.rvMyProduct.visibility = View.VISIBLE

            val map = HashMap<String, String>()
            map[RequestKeys.userId] = userData.id.toString()
            map[RequestKeys.pageNumber] = "$currentPage"

            mainViewModel.getMyProductList(
                Utility.getHeaders(this@MyProductActivity),
                map
            )
        } else {
            showSnackBar(
                binding.clMyProduct,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }

    private fun setUpRecyclerView() {

        binding.rvMyProduct.apply {
            isNestedScrollingEnabled = false
            setHasFixedSize(false)
            layoutManager =
                GridLayoutManager(this@MyProductActivity, 2)
            adapter = myProductAdapter
        }

        binding.sv.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, oldScrollY: Int ->

            val lastChild =
                binding.sv.getChildAt(binding.sv.childCount - 1)

            if (lastChild != null) {
                if ((scrollY >= (lastChild.measuredHeight - binding.sv.measuredHeight)) && scrollY > oldScrollY && !isLoading && !isLastPage) {
                    if (!noMoreData) {
                        isLoading = true
                        isLastPage = true
                        getMyProductList()
                    }
                }
            }
        }
    }

    private fun noDataFound() {
        if (!isLoading && !isLastPage) {
            includeNoData.clNoData.visibility = View.VISIBLE
            includeNoData.tvNoData.text =
                applicationContext.resources.getString(R.string.my_product_no_data_message)
            binding.rvMyProduct.visibility = View.GONE
            includeNoData.ivNoData.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.home_toolbar_app_logo
                )
            )
        }
    }

    private fun getProductDetail() {
        if (isNetworkAvailable()) {

            val map = HashMap<String, String>()
            map[RequestKeys.userId] = userData.id.toString()
            map[RequestKeys.productId] = productData?.id.toString()

            mainViewModel.getProductDetail(
                Utility.getHeaders(this@MyProductActivity),
                map
            )
        } else {
            showSnackBar(
                binding.clMyProduct,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }
    }

    override fun onItemDetail(data: ProductData, position: Int) {
        productData = data
        this.position = position
        val intent = Intent(this@MyProductActivity, ProductDetailActivity::class.java)
        intent.putExtra(Constants.productId, data.id.toString())
        launchProductDetailActivity.launch(intent)
    }

    private var launchProductDetailActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == Activity.RESULT_OK) {
                val intentData: String = data!!.getStringExtra(Constants.success)!!
                if (intentData == Constants.success) {
                    isRefresh = true
                    getProductDetail()
                }

                if (intentData == Constants.failure) {
                    myProductList.removeAt(position)
                    myProductAdapter.notifyItemChanged(position)
                }
            }
        }

    override fun onItemEdit(data: ProductData, position: Int) {
        productData = data
        isEdit = true
        this.position = position
        getProductDetail()
    }

    override fun onItemDelete(data: ProductData, position: Int) {
        productData = data
        this.position = position
        deleteDialog(data)
    }

    private fun deleteDialog(data: ProductData) {

        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder
            .setTitle(R.string.delete_str)
            .setMessage(R.string.delete_product_message)
            .setCancelable(false)

        dialogBuilder.setPositiveButton(resources.getString(R.string.yes_string), null)

        dialogBuilder.setNegativeButton(resources.getString(R.string.no_string), null)

        val alertDialog = dialogBuilder.create()

        alertDialog.setOnShowListener {

            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {

                val map = HashMap<String, String>()
                map[RequestKeys.userId] = userData.id.toString()
                map[RequestKeys.productId] = data.id.toString()

                mainViewModel.deleteProduct(Utility.getHeaders(this), map)

                myProductList.removeAt(position)
                myProductAdapter.notifyDataSetChanged()

                alertDialog.dismiss()
            }
            alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.show()
    }
}