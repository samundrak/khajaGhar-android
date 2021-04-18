package com.app.khajaghar.ui.signup

import android.app.Activity
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.khajaghar.R
import com.app.khajaghar.data.local.PreferencesHelper
import com.app.khajaghar.data.local.Resource
import com.app.khajaghar.data.model.User
import com.app.khajaghar.databinding.ActivitySignUpBinding
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.*


class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private val viewModel: SignUpViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var progressDialog: ProgressDialog
    private var number: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getArgs()
        initView()
        setListener()
        setObservers()
//        viewModel.getPlaces()
    }

    private fun getArgs() {
        number = preferencesHelper.mobile
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        progressDialog = ProgressDialog(this)
        progressDialog.setCancelable(false)
    }

    private fun setListener() {
        binding.imageClose.setOnClickListener { onBackPressed() }
        binding.buttonRegister.setOnClickListener {
            if (binding.editName.text.toString().isNotEmpty()) {
                //TODO email validation
                if (binding.editEmail.text.toString().isNotEmpty()) {
                    if (binding.editPasswordReenter.text.toString() == binding.editPassword.text.toString()) {
                        val firstSpace: Int = binding.editName.text.toString().indexOf(" ") // detect the first space character
                        val firstName: String
                        val lastName: String
                        if (firstSpace != -1) {
                            firstName = binding.editName.text.toString().substring(0, firstSpace)
                            lastName = binding.editName.text.toString().substring(firstSpace).trim()
                        } else {
                            firstName = binding.editName.text.toString()
                            lastName = ""
                        }

                        val updateUserRequest = User(
                                email = binding.editEmail.text.toString(),
                                password = binding.editPassword.text.toString(),
                                firstName = firstName,
                                lastName = lastName
                        )
                        viewModel.signUp(updateUserRequest)
                    } else {
                        Toast.makeText(applicationContext, "Password do not match", Toast.LENGTH_SHORT).show()

                    }

                } else {
                    Toast.makeText(applicationContext, "Email is blank", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Name is blank", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setObservers() {
        viewModel.performSignUpStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        preferencesHelper.name = binding.editName.text.toString()
                        preferencesHelper.email = binding.editEmail.text.toString()
                        progressDialog.dismiss()
                        if (resource.data != null) {
                            Toast.makeText(applicationContext, "Please login to continue", Toast.LENGTH_SHORT).show()
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
                }
            }
        })
    }

    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this@SignUpActivity)
                .setTitle("Cancel process?")
                .setMessage("Are you sure want to cancel the registration process?")
                .setPositiveButton("Yes") { dialog, which ->
                    super.onBackPressed()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                .show()
    }




}
