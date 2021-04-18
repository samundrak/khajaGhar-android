package com.app.khajaghar.ui.webview

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.app.khajaghar.R
import com.app.khajaghar.data.local.PreferencesHelper
import com.app.khajaghar.databinding.ActivityWebViewBinding
import com.app.khajaghar.utils.AppConstants
import org.koin.android.ext.android.inject

class WebViewActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWebViewBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private var url:String? = null
    private var title:String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
        setListener()
    }

    private fun getArgs(){
        url = intent.getStringExtra(AppConstants.URL)
        title = intent.getStringExtra(AppConstants.NOTIFICATION_TITLE)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web_view)
        binding.textTitle.text = title
        binding.webView.settings.javaScriptEnabled = true
        if(!url.isNullOrEmpty()){
            binding.webView.loadUrl(url)
        }else{
            binding.webView.loadUrl("https://zinger.tech")
        }
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener {
            onBackPressed()
        }
    }

}
