package com.oss.abraakadabraaapp.activities.newflow

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.oss.abraakadabraaapp.activities.BaseActivity
import com.oss.abraakadabraaapp.activities.newflow.adapters.CategorySelectAdapter
import com.oss.abraakadabraaapp.activities.newflow.model.CatData
import com.oss.abraakadabraaapp.activities.newflow.model.UserCatData
import com.oss.abraakadabraaapp.databinding.ActivityCategorySelectBinding
import com.oss.abraakadabraaapp.response.mainResponse.CategoryData
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_APPLY_CATEGORY
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_BACK_IN_CATEGORY_SELECTION
import com.oss.abraakadabraaapp.utils.Constants.BUTTON_CATEGORY_CLEAR
import com.oss.abraakadabraaapp.utils.PreferencesManagement

class CategorySelectActivity : BaseActivity(),CategorySelectAdapter.OnCategoryClicked {
    lateinit var application: BaseActivity

    private lateinit var binding: ActivityCategorySelectBinding

    private lateinit var adapter:CategorySelectAdapter
    var list = ArrayList<UserCatData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategorySelectBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        list = PreferencesManagement.getCategories(this)!!.data
        application = (this as BaseActivity)
        application.postEvent(Constants.PAGE_CATEGORY_SELECTION,null)

        //tempLoadData()

        adapter = CategorySelectAdapter(this, list,this)
        binding.catRecycler.layoutManager = LinearLayoutManager(this)
        binding.catRecycler.adapter = adapter

        binding.clearBtn.setOnClickListener {
            postClick(BUTTON_CATEGORY_CLEAR)
           // list.clear()
            for (i in list){
                i.isSelect = false
            }
            Log.d("TAG - ", "onCreate cats: ${Gson().toJson(list)}")
            adapter.setList(list)
            adapter.notifyDataSetChanged()
        }
        binding.applyBtn.setOnClickListener {
            postClick(BUTTON_APPLY_CATEGORY)
            var str = ""
            for (i in list){
                if (i.isSelect){
                    str = "$str,${i.title}"
                }
            }
//            showToast(str)
            if (str == ""){
                showToast("Please select at least on category")
            }else{
                var cats = ArrayList<UserCatData>()
                for (i in list){
                    if (i.isSelect) cats.add(i)
                }
                val intent = Intent(this, NewSearchActivity::class.java)
                intent.putExtra("CATEGORIES",Gson().toJson(cats))
                intent.putExtra("from","category")

                startActivity(intent)
            }
//            startActivity(Intent(this, NewSearchActivity::class.java))
        }

        binding.ivBack.setOnClickListener {
            postClick(BUTTON_BACK_IN_CATEGORY_SELECTION)
            onBackPressed()
        }
    }

    override fun onClick() {
        var str = ""
        for (i in list){
            if (i.isSelect){
                str = "$str,${i.title}"
            }
        }
//        showToast(str)
        if (str == ""){
            showToast("Please select at least on category")
        }else{

        }
    }

}