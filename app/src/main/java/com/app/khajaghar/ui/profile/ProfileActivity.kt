package com.app.khajaghar.ui.profile

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.app.khajaghar.R
import com.app.khajaghar.data.local.PreferencesHelper
import com.app.khajaghar.data.local.Resource
import com.app.khajaghar.data.model.PlaceModel
import com.app.khajaghar.data.model.User
import com.app.khajaghar.databinding.ActivityProfileBinding
import com.app.khajaghar.databinding.BottomSheetVerifyOtpBinding
import com.app.khajaghar.ui.order.OrdersActivity
import com.github.drjacky.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.io.File

class ProfileActivity : AppCompatActivity() {

    private var file: File?=null
    private var filePath: String? = null
    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var progressDialog: ProgressDialog
    private var places: ArrayList<PlaceModel> = ArrayList()
    private var selectedPlace: PlaceModel? = null
    private lateinit var dialogBinding: BottomSheetVerifyOtpBinding
    private lateinit var countDownTimer: CountDownTimer
    private var otpVerified = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //TODO add mobile edit
        initView()
        setListener()
        setObservers()
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        progressDialog = ProgressDialog(this)
        binding.editEmail.setText(preferencesHelper.email)
        binding.editFirstName.setText(preferencesHelper.firstName)
        binding.editLastName.setText(preferencesHelper.lastName)
        print(preferencesHelper.photo)
        Picasso.get().load("http://65.0.89.70:5252/${preferencesHelper.photo}").placeholder(R.drawable.ic_drawer_user).into(binding.profileImage)
    }

    private fun setListener() {
        binding.profileImage.setOnClickListener {
            ImagePicker.with(this)
                    .crop()        //Crop image(Optional), Check Customization for more option
                    .compress(1024)   //Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080) //Final image resolution will be less than 1080 x 1080(Optional)
                    .start { resultCode, data ->
                        when (resultCode) {
                            Activity.RESULT_OK -> {
                                //Image Uri will not be null for RESULT_OK
                                val fileUri = data?.data
                                val file: File? = ImagePicker.getFile(data)
                                this.file = file
                                binding.profileImage.setImageURI(fileUri)

                            }
                            ImagePicker.RESULT_ERROR -> {
                                Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
        }

        binding.imageClose.setOnClickListener { onBackPressed() }
        binding.buttonUpdate.setOnClickListener {
            if (binding.editFirstName.text.toString().isNotEmpty()) {
                if (binding.editEmail.text.toString().isNotEmpty()) {

                    val updateUserRequest = User(
                            preferencesHelper.userId,
                            binding.editEmail.text.toString(),
                            binding.editFirstName.text.toString(),
                            binding.editLastName.text.toString(),
                            filePath
                    )
                    viewModel.updateUserDetails(updateUserRequest,file)

                } else {
                    Toast.makeText(applicationContext, "Email is blank", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(applicationContext, "Name is blank", Toast.LENGTH_SHORT).show()
            }
        }
        binding.textYourOrders.setOnClickListener {
            startActivity(Intent(applicationContext, OrdersActivity::class.java))
        }

        countDownTimer = object : CountDownTimer(10000, 1000) {

            override fun onTick(millisUntilFinished: Long) {
                dialogBinding.textResendOtp.text = "Resend OTP (" + millisUntilFinished / 1000 + ")"
            }

            override fun onFinish() {
                dialogBinding.textResendOtp.text = "Resend OTP"
                dialogBinding.textResendOtp.isEnabled = true
            }
        }

    }

    private fun setObservers() {
        viewModel.performFetchPlacesStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        progressDialog.setMessage("Getting places")
                        progressDialog.show()
                    }
                    Resource.Status.EMPTY -> {
                        progressDialog.dismiss()
                        //val snackbar = Snackbar.make(binding.root, "No Outlets in this college", Snackbar.LENGTH_LONG)
                        //snackbar.show()
                    }
                    Resource.Status.SUCCESS -> {
                        progressDialog.dismiss()
                        places.clear()
                        it.data?.let { it1 -> it1.data?.let { it2 -> places.addAll(it2) } }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        progressDialog.dismiss()
                        val snackbar = Snackbar.make(binding.root, "No Internet Connection", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                    Resource.Status.ERROR -> {
                        progressDialog.dismiss()
                        val snackbar = Snackbar.make(binding.root, "Something went wrong", Snackbar.LENGTH_LONG)
                        snackbar.show()
                    }
                }
            }
        })
        viewModel.performUpdateStatus.observe(this, Observer { resource ->
            if (resource != null) {
                when (resource.status) {
                    Resource.Status.SUCCESS -> {
                        binding.buttonUpdate.isEnabled = true
                        preferencesHelper.name = binding.editFirstName.text.toString()
                        preferencesHelper.email = binding.editEmail.text.toString()
                        preferencesHelper.place = Gson().toJson(selectedPlace)

                        if (otpVerified) {
                            preferencesHelper.mobile = preferencesHelper.tempMobile
                            preferencesHelper.oauthId = preferencesHelper.tempOauthId
                            otpVerified = false
                        }

                        progressDialog.dismiss()
                        if (resource.data != null) {
                            Toast.makeText(applicationContext, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                            preferencesHelper.clearCartPreferences()
                        } else {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        binding.buttonUpdate.isEnabled = true
                        progressDialog.dismiss()
                        Toast.makeText(applicationContext, "No Internet Connection", Toast.LENGTH_SHORT).show()
                    }
                    Resource.Status.ERROR -> {
                        binding.buttonUpdate.isEnabled = true
                        progressDialog.dismiss()
                        resource.message?.let {
                            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                        } ?: run {
                            Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                        }
                    }
                    Resource.Status.LOADING -> {
                        binding.buttonUpdate.isEnabled = false
                        progressDialog.setMessage("Updating profile...")
                        progressDialog.show()
                    }
                }
            }
        })
    }

}
