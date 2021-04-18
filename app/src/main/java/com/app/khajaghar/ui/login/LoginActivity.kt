package com.app.khajaghar.ui.login

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.khajaghar.R
import com.app.khajaghar.data.local.PreferencesHelper
import com.app.khajaghar.data.local.Resource
import com.app.khajaghar.data.model.LoginRequest
import com.app.khajaghar.databinding.ActivityLoginBinding
import com.app.khajaghar.di.networkModule
import com.app.khajaghar.ui.home.HomeActivity
import com.app.khajaghar.ui.signup.SignUpActivity
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val preferencesHelper: PreferencesHelper by inject()
    private val viewModel: LoginViewModel by viewModel()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setListener()
        setObservers()
        if (preferencesHelper.token != "") {
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }
    }

    private fun setObservers() {
        viewModel.performLoginStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        if (resource.data != null) {
                            progressDialog.dismiss()
                            print("resource.data.code ${resource.data}")
                            preferencesHelper.token = resource.data.token
                            startActivity(Intent(applicationContext, HomeActivity::class.java))
                            finish()

//                                val userModel = resource.data.data?.userModel
//                                val placeModel = resource.data.data?.placeModel
//                                if (userModel != null) {
//                                    preferencesHelper.saveUser(
//                                            userId = userModel.userId,
//                                            name = userModel.name,
//                                            email = userModel.email,
//                                            mobile = preferencesHelper.mobile,
//                                            role = userModel.role,
//                                            oauthId = preferencesHelper.oauthId,
//                                            place = Gson().toJson(placeModel)
//                                    )
//                                }
                            unloadKoinModules(networkModule)
                            loadKoinModules(networkModule)
                            startActivity(Intent(applicationContext, HomeActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()

                        resource.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Logging in...")
                        progressDialog.show()
                    }
                    Resource.Status.EMPTY -> {
                        progressDialog.hide()
                    }
                }
            }
        })
    }


    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
    }

    private fun setListener() {
        binding.buttonLogin.setOnClickListener {
            val phoneNo = binding.editEmail.text.toString()
            val password = binding.editPassword.text.toString()
            if (phoneNo.isNotEmpty() && phoneNo.contains("@")) {
                viewModel.login(LoginRequest(email = phoneNo, password = password))

            } else {
                Toast.makeText(applicationContext, "Invalid Email!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnRegister.setOnClickListener {
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

}
