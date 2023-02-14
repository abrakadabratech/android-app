package com.oss.abraakadabraaapp.activities.newflow

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.gson.Gson
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.CatMainAdapter
import com.oss.abraakadabraaapp.activities.newflow.adapters.CategoryDialogAdapter
import com.oss.abraakadabraaapp.activities.newflow.adapters.ConditionDialogAdapter
import com.oss.abraakadabraaapp.activities.newflow.adapters.MyRequestedUsersAdapter
import com.oss.abraakadabraaapp.activities.newflow.model.AllCategoryResponse
import com.oss.abraakadabraaapp.activities.newflow.model.CatData
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.databinding.ActivityMyListingDetailsBinding
import com.oss.abraakadabraaapp.response.productRequestResponse.ListingResponse
import com.oss.abraakadabraaapp.response.productRequestResponse.RequestData
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_MYLISTING_DETAILS
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.AuthViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MyListingDetialActivity : BaseActivity() ,MyRequestedUsersAdapter.OnRequestClicks,CategoryDialogAdapter.CategoryDialogAdapterInterface,
    CatMainAdapter.MainCategoryAdapterInterface, ConditionDialogAdapter.ConditionAdapterInterface {
    lateinit var application: BaseActivity
    lateinit var product: RequestData
    private val mainViewModel: AuthViewModel by viewModel()
    var productDetails : ListingResponse? = null
    private lateinit var binding:ActivityMyListingDetailsBinding

    lateinit var alertDialog: AlertDialog
    lateinit var alertAdaper: CategoryDialogAdapter
    lateinit var condtionAdapter: ConditionDialogAdapter
    lateinit var mainCatAdapter: CatMainAdapter
    lateinit var mainAdapterList:ArrayList<UserCatData>
    var list = arrayListOf<CatData>()
    var listConditon = arrayListOf<CatData>()
    private var PROD_CATEGORY: String = ""
    private var PROD_CONDITION: String = ""
    private var PROD_USED_FOR: String = ""
    lateinit var userCatData: AllCategoryResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyListingDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        product =
            Gson().fromJson(intent.extras?.getString(Constants.PRODUCT, ""), RequestData::class.java)

        application = this
        application.postEvent(Constants.PAGE_MY_LISTING_DETAIL,null)

        userCatData = PreferencesManagement.getCategories(this)!!

        if (userCatData.data.size > 0){
            mainAdapterList = userCatData.data
            mainAdapterList[0].isSelect = true
        }

        //Cat adpater
        mainCatAdapter = CatMainAdapter(this,mainAdapterList,this)

        //Used for adapter
        alertAdaper = CategoryDialogAdapter(this,list, this,"")

        //Condition adapter
        condtionAdapter = ConditionDialogAdapter(this, listConditon, this)

//        tempData()

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_MYLISTING_DETAILS)
            onBackPressed()
        }

        binding.ivMenu.setOnClickListener {
            postClick(Constants.BUTTON_MENU_IN_DETAILS_PAGE)
            binding.editMenuDialog.visibility = View.VISIBLE
        }

        binding.editProduct.setOnClickListener {
            postClick(Constants.BUTTON_EDIT_PRODUCT)
            binding.productName.isEnabled = true
            binding.descriptionTxt.isEnabled = true
            binding.productName.requestFocus()
            binding.productName.setSelection(binding.productName.text.toString().length)

            val imm: InputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.productName, InputMethodManager.SHOW_IMPLICIT)

//            binding.productName.isFocusable = true
            binding.updateBtn.visibility = View.VISIBLE
            binding.editMenuDialog.visibility = View.GONE

        }

        binding.conditionTxt.setOnClickListener {
            //conditon popup
            showCategoryFilterDialog()
        }
        binding.usedForTxt.setOnClickListener {
            //used for popup
            showConditionDialog()
        }

        binding.categoryTxt.setOnClickListener {
            //category pop up
            showCategoryFilterDialog()
        }

        binding.locationName.setOnClickListener {
            //Location should be shown here!!!

        }
        binding.editMenuDialog.setOnClickListener {
            binding.editMenuDialog.visibility = View.GONE
        }

        binding.editCardview.setOnClickListener {
            binding.editMenuDialog.visibility = View.VISIBLE
        }

        binding.updateBtn.setOnClickListener {
            if (binding.productName.text.toString().isNotEmpty() &&
                    binding.descriptionTxt.text.toString().isNotEmpty()){
                val map = HashMap<String,String>()
                map["name"] = binding.productName.text.toString()
                map["description"] = binding.descriptionTxt.text.toString()
                postClick(Constants.BUTTON_UPDATE_PRODUCT)
                if (isNetworkAvailable()){
                    generateAuthToken()
                    mainViewModel.updateProduct(Utility.getAuthentication(this),product.id.toString(),map)
                }
            }else{
                showToast("Please enter Product Name and description")
            }

        }

        binding.shareProduct.setOnClickListener {
            postClick(Constants.BUTTON_SHARE_PRODUCT)
        }

        binding.deleteProduct.setOnClickListener {
            postClick(Constants.BUTTON_DELETE_PRODUCT)
            var alertDialog = AlertDialog.Builder(this)
            alertDialog.setTitle("Alert!")
            alertDialog.setMessage("Are you sure you want to delete your listing?")

            alertDialog.setPositiveButton("Yes", DialogInterface.OnClickListener{ dialog, id ->
                //cancel the request
                if (isNetworkAvailable()){
                    generateAuthToken()
                    mainViewModel.deleteProduct(Utility.getAuthentication(this), product.id.toString())
                }
                dialog.dismiss()
            })
            alertDialog.setNegativeButton("No", DialogInterface.OnClickListener{ dialog, id ->
                dialog.dismiss()
            })
            alertDialog.show()
        }
        binding.shareProduct.setOnClickListener {
            loadShareData()
        }
        setUpObserver()

        loaddata()

    }

    private fun loaddata() {
        val map = HashMap<String, String>()
        val token = PreferencesManagement.getAuthToken(this)!!
        map[RequestKeys.authorization] = token
        mainViewModel.getListingDetails(map, product.id!!)
    }

    private fun setUpObserver() {
        mainViewModel.updateProductSuccess.observe(this){
            if (it.code == 201){
                showToast(it.responseMessage.toString())
                binding.productName.setText( it.data?.name)
                binding.descriptionTxt.setText(it.data?.description)
                binding.productName.isEnabled = false
                binding.descriptionTxt.isEnabled = false
                binding.updateBtn.visibility = View.GONE
            }
        }
        mainViewModel.deleteProductSuccess.observe(this){
            if (it.code == 200){
               finish()
            }
        }
        mainViewModel.listingDetailsuccess.observe(this) {
//            Log.d("TAG - Product deails", "is it rue : ${productDetails.data.description}")

            if (it.code == 200) {
                productDetails = it!!
                setUpProductDetails(it)
            } else {
                Log.d("TAG -", "setUpObserver: fail")
            }
        }
        mainViewModel.reportProductSuccess.observe(this){
            if (it.code == 201){
                showToast("Product reported")
            }else{
                showToast(it.responseMessage.toString())
            }
        }
        mainViewModel.errorMessage.observe(this) { if (it.isNotBlank()) showToast(it) }
        mainViewModel.isLoading.observe(this) { loader(it) }

    }

    private fun setUpProductDetails(it: ListingResponse) {
        val imageList = ArrayList<SlideModel>()
        for (i in it.product?.images!!) {
            imageList.add(SlideModel(i, "", ScaleTypes.FIT))
        }
        val data = it.product!!
        binding.imageSlider.setImageList(imageList)

        binding.categoryTxt.text = data.category?.name?.capitalize()
        binding.conditionTxt.text = data.condition
        binding.productName.setText(data.name?.capitalize())
        binding.usedForTxt.text = data.usedFor
        binding.costSavingTxt.text = "Rs ${data.costSaving}"
        binding.textView23.text = "Rs ${data.energySaving}"
//        binding.responsesOne.text = (data.postedBy.toString())
//        binding.dateOfPostTxt.text = (it.data.createdAt.toString())
        binding.descriptionTxt.setText(data.description?.capitalize().toString())
        binding.locationName.setText(data.locationName.toString())
        binding.responsesOne.text = "${it.requests.size} Responses"
        binding.responsesTwo.text = "${it.requests.size} Responses"

        val adapter = MyRequestedUsersAdapter(this,it.requests,this)
        binding.rvRequestedUsers.layoutManager = LinearLayoutManager(this)
        binding.rvRequestedUsers.adapter = adapter

    }
    private fun loadShareData() {

        val i = Intent(Intent.ACTION_SEND)
        i.type = "text/plain"
        i.putExtra(Intent.EXTRA_SUBJECT, "Subject test")
        i.putExtra(Intent.EXTRA_TEXT, "Try this great app Abra Ka Dabra to share second hand products with others for free. App is available at the below link: https://play.google.com/store/apps/details?id=com.oss.abraakadabraaapp")
        startActivity(Intent.createChooser(i, "Share"))
    }

    override fun onClick(position:Int) {
        if (productDetails!=null){
            val intent = Intent(this,RequesterActivity::class.java)
            intent.putExtra(Constants.PRODUCT,Gson().toJson(productDetails))
            intent.putExtra("POSTION",position)
            startActivity(intent)

        }
    }

    private fun showCategoryFilterDialog() {

        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_catgory_dialog, null)
        dialogBuilder.setView(dialogView)

        val alertName = dialogView.findViewById<TextView>(R.id.alertName)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        alertName.text = "Categories"
        val catRecycler = dialogView.findViewById<RecyclerView>(R.id.catRecycler)

        catRecycler.layoutManager = LinearLayoutManager(this)
//        alertAdaper.i = loadData()
//        alertAdaper.alerttype = "category"
//        alertAdaper = alertAdaper
        catRecycler.adapter = mainCatAdapter
        alertAdaper.notifyDataSetChanged()

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        closeBtn.setOnClickListener { alertDialog.dismiss() }
        if (PROD_CATEGORY == "") PROD_CATEGORY = mainAdapterList[0].id.toString()
        else PROD_CATEGORY = PROD_CATEGORY
        binding.categoryTxt.text = PROD_CATEGORY

//        binding.cateogoryTxt.error = null
        alertDialog.show()
//        binding.categorySelectedTxt.visibility = View.VISIBLE
    }

    private fun showConditionDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_catgory_dialog, null)
        dialogBuilder.setView(dialogView)
        val alertName = dialogView.findViewById<TextView>(R.id.alertName)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)
        alertName.text = "Condition"
        val catRecycler = dialogView.findViewById<RecyclerView>(R.id.catRecycler)
        catRecycler.layoutManager = LinearLayoutManager(this)

        // Condition adapter
        catRecycler.adapter = condtionAdapter
        condtionAdapter.notifyDataSetChanged()

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        closeBtn.setOnClickListener { alertDialog.dismiss() }
        if(PROD_CONDITION == "") PROD_CONDITION = listConditon[0].name.toString()
        else PROD_CONDITION = PROD_CONDITION

        binding.conditionTxt.text = PROD_CONDITION
//        binding.conditionTxt.error = null
        alertDialog.show()
//        binding.conditionSelectedTxt.visibility = View.VISIBLE
    }

    private fun showUsedForDialog() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        val inflater = this.layoutInflater
        val dialogView: View = inflater.inflate(R.layout.alert_catgory_dialog, null)
        dialogBuilder.setView(dialogView)

        val alertName = dialogView.findViewById<TextView>(R.id.alertName)
        val closeBtn = dialogView.findViewById<ImageView>(R.id.closeBtn)

        alertName.text = "Used For"
        val catRecycler = dialogView.findViewById<RecyclerView>(R.id.catRecycler)
        catRecycler.layoutManager = LinearLayoutManager(this)
//        alertAdaper.i = loadUsedForData()
//        alertAdaper.alerttype = "used_for"

        catRecycler.adapter = alertAdaper
        alertAdaper.notifyDataSetChanged()

        alertDialog = dialogBuilder.create()
        alertDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        closeBtn.setOnClickListener { alertDialog.dismiss() }
        if(PROD_USED_FOR == "") PROD_USED_FOR = list[0].name.toString()
        else PROD_USED_FOR = PROD_USED_FOR

        binding.usedForTxt.text = list[0].name
//        binding.usedForSelectedTxt.visibility = View.VISIBLE

//        binding.usedForTxt.error = null

        alertDialog.show()
    }
    override fun onMainItemClick(position: Int, isSelect: Boolean) {
        PROD_CATEGORY = mainAdapterList[position].id.toString()

        binding.categoryTxt.text = mainAdapterList[position].title

        for (i in 0 until mainAdapterList.size) mainAdapterList[i].isSelect = i == position

        mainCatAdapter.notifyDataSetChanged()
        alertDialog.dismiss()
    }

    override fun onConditionItemClick(position: Int, isSelect: Boolean) {
        for (i in 0 until listConditon.size) listConditon[i].isSelect = i == position
        PROD_CONDITION = listConditon[position].name.toString()

        binding.conditionTxt.text = listConditon[position].name
//        binding.conditionSelectedTxt.visibility = View.VISIBLE

        condtionAdapter.notifyDataSetChanged()
        alertDialog.dismiss()
    }
    override fun onItemClick(position: Int, isSelect: Boolean,alerttype:String) {
//        list.get(position).isSelect = isSelect
        for (i in 0 until list.size) list[i].isSelect = i == position
        PROD_USED_FOR = list[position].name.toString()

        binding.usedForTxt.text = list[position].name

        alertAdaper.notifyDataSetChanged()
        alertDialog.dismiss()
    }
}