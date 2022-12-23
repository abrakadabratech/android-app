package com.oss.abraakadabraaapp.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import com.oss.abraakadabraaapp.R
import com.oss.abraakadabraaapp.databinding.ActivityContentManagementBinding
import com.oss.abraakadabraaapp.databinding.LoggedInUserToolbarBinding
import com.oss.abraakadabraaapp.response.commonResponse.ContentManagementResponse
import com.oss.abraakadabraaapp.retrofit.api.RequestKeys
import com.oss.abraakadabraaapp.utils.Constants
import com.oss.abraakadabraaapp.utils.PreferencesManagement
import com.oss.abraakadabraaapp.utils.Utility
import com.oss.abraakadabraaapp.viewModel.ContentManagementViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContentManagementActivity : BaseActivity() {

    private lateinit var binding: ActivityContentManagementBinding
    private lateinit var includeToolbar: LoggedInUserToolbarBinding

    private val userData by lazy { PreferencesManagement.getUserData(this)!! }

    private val contentManagementViewModel: ContentManagementViewModel by viewModel()

    private var type = ""

    companion object {
        fun createIntent(context: Context, type: String):Intent {
            val intent = Intent(context, ContentManagementActivity::class.java)
            intent.putExtra(Constants.hasContentManagement, Constants.hasContentManagement)
            intent.putExtra("type", type)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentManagementBinding.inflate(layoutInflater)
        includeToolbar = binding.includeToolbar
        val view = binding.root
        setContentView(view)

        actionBar?.hide()

//        setSupportActionBar(includeToolbar.toolbar)

        includeToolbar.ivBack.setOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra(Constants.hasContentManagement)) {
            type = intent.getStringExtra("type")!!

            when(type){
                Constants.contactUs->{
                    binding.tvContent.visibility = View.GONE
                    binding.clContactUsContent.visibility = View.VISIBLE
                    binding.clAboutUsContent.visibility = View.GONE
                    binding.clPrivacyPolicyContent.visibility = View.GONE
                }
                Constants.aboutUs -> {
                    binding.tvContent.visibility = View.GONE
                    binding.clContactUsContent.visibility = View.GONE
                    binding.clAboutUsContent.visibility = View.VISIBLE
                    binding.clPrivacyPolicyContent.visibility = View.GONE
                }
                Constants.privacyPolicy -> {
                    binding.tvContent.visibility = View.GONE
                    binding.clContactUsContent.visibility = View.GONE
                    binding.clAboutUsContent.visibility = View.GONE
                    binding.clPrivacyPolicyContent.visibility = View.VISIBLE
                }
            }
        }

        setUpObserver()
        initUI()

    }

    override fun onResume() {
        super.onResume()
        if (type != Constants.contactUs) {
            //getContentData()
        }
    }

    private fun getContentData() {
        if (isNetworkAvailable()) {

            val map = HashMap<String, String>()
            map[RequestKeys.userId] = userData.id.toString()
            map[RequestKeys.type] = type

            contentManagementViewModel.contentManagementSystem(Utility.getHeaders(this), map)

        } else {
            showSnackBar(
                binding.clContentManagement,
                applicationContext.resources.getString(R.string.no_internet_connection_found)
            )
        }

    }

    private fun initUI() {
        when (type) {
            Constants.privacyPolicy -> {
                includeToolbar.tvToolbarTitle.text =
                    applicationContext.resources.getString(R.string.privacy_policy)
            }
            Constants.contactUs -> {
                includeToolbar.tvToolbarTitle.text =
                    applicationContext.resources.getString(R.string.support)
            }
            Constants.aboutUs -> {
                includeToolbar.tvToolbarTitle.text =
                    applicationContext.resources.getString(R.string.about_us)
            }
        }

    }

    private fun setUpObserver() {
        contentManagementViewModel.isLoading.observe(this, { loader(it) })
        contentManagementViewModel.contentManagementSuccess.observe(this, {
            val data = it.data.content
            setUpUI(data)
        })
        contentManagementViewModel.errorMessage.observe(
            this,
            { if (it.isNotBlank()) showToast(it) })
    }

    private fun setUpUI(data: ContentManagementResponse.Data.Content) {
        binding.tvContent.text = HtmlCompat.fromHtml(data.value, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }
}