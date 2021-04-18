package com.app.khajaghar.ui.home

import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.amulyakhare.textdrawable.TextDrawable
import com.app.khajaghar.data.model.MenuItemModel
import com.app.khajaghar.R
import com.app.khajaghar.data.local.PreferencesHelper
import com.app.khajaghar.data.model.NotificationTokenUpdate
import com.app.khajaghar.data.local.Resource
import com.app.khajaghar.data.model.*
import com.app.khajaghar.databinding.ActivityHomeBinding
import com.app.khajaghar.databinding.BottomSheetShiftListBinding
import com.app.khajaghar.databinding.HeaderLayoutBinding
import com.app.khajaghar.ui.cart.CartActivity
import com.app.khajaghar.ui.contactus.ContactUsActivity
import com.app.khajaghar.ui.contributors.ContributorsActivity
import com.app.khajaghar.ui.login.LoginActivity
import com.app.khajaghar.ui.order.OrdersActivity
import com.app.khajaghar.ui.profile.ProfileActivity
import com.app.khajaghar.ui.profile.ProfileViewModel
import com.app.khajaghar.ui.restaurant.FoodAdapter
import com.app.khajaghar.ui.search.SearchActivity
import com.app.khajaghar.utils.AppConstants
import com.app.khajaghar.utils.FcmUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import com.mikepenz.materialdrawer.Drawer
import com.mikepenz.materialdrawer.DrawerBuilder
import com.mikepenz.materialdrawer.model.DividerDrawerItem
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*
import kotlin.collections.ArrayList

class HomeActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by viewModel()
    private val preferencesHelper: PreferencesHelper by inject()
    private lateinit var headerLayout: HeaderLayoutBinding
    private lateinit var drawer: Drawer
    private lateinit var foodAdapter: FoodAdapter
    private lateinit var progressDialog: ProgressDialog
    var foodItemList: ArrayList<MenuItemModel> = ArrayList()
    private var cartList: ArrayList<MenuItemModel> = ArrayList()
    private lateinit var cartSnackBar: Snackbar
    private lateinit var errorSnackbar: Snackbar
    private lateinit var errorOrderSnackbar: Snackbar
    private var placeId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setupMaterialDrawer()
        setObservers()
        placeId = preferencesHelper.getPlace()?.id.toString()
        viewModel.getFood()
        cartSnackBar.setAction("View Orders") { startActivity(Intent(applicationContext, OrdersActivity::class.java)) }
        errorSnackbar.setAction("Try again") {
            viewModel.getFood()
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.getFood()
        }
        getFCMToken()
        FcmUtils.subscribeToTopic(AppConstants.NOTIFICATION_TOPIC_GLOBAL)
    }

    private fun initView() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        headerLayout = DataBindingUtil.inflate(LayoutInflater.from(applicationContext), R.layout.header_layout, null, false)
        cartSnackBar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        errorOrderSnackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_LONG)
        cartSnackBar.setBackgroundTint(ContextCompat.getColor(applicationContext, R.color.green))
        errorSnackbar = Snackbar.make(binding.root, "", Snackbar.LENGTH_INDEFINITE)
        val snackButton: Button = errorSnackbar.view.findViewById(R.id.snackbar_action)
        snackButton.setCompoundDrawables(null, null, null, null)
        snackButton.background = null
        snackButton.setTextColor(ContextCompat.getColor(applicationContext, R.color.accent))
        binding.imageMenu.setOnClickListener(this)
        binding.textSearch.setOnClickListener(this)
        progressDialog = ProgressDialog(this)
        setStatusBarHeight()
        setupShopRecyclerView()
    }

    private fun setStatusBarHeight() {
        binding.root.viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                val rectangle = Rect()
                val window = window
                window.decorView.getWindowVisibleDisplayFrame(rectangle)
                val statusBarHeight = rectangle.top
                val layoutParams = headerLayout.statusbarSpaceView.layoutParams
                layoutParams.height = statusBarHeight
                headerLayout.statusbarSpaceView.layoutParams = layoutParams
                Log.d("Home", "status bar height $statusBarHeight")
                binding.root.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun updateHeaderLayoutUI() {
        headerLayout.textCustomerName.text = preferencesHelper.name
        headerLayout.textEmail.text = preferencesHelper.email
        val letter = preferencesHelper.name?.get(0).toString()
        val textDrawable = TextDrawable.builder()
                .buildRound(letter, ContextCompat.getColor(this, R.color.accent))
        headerLayout.imageProfilePic.setImageDrawable(textDrawable)
        //binding.imageMenu.setImageDrawable(textDrawable);
    }

    private fun setupMaterialDrawer() {
        headerLayout.layoutHeader.setOnClickListener {
            startActivity(Intent(applicationContext, ProfileActivity::class.java))
        }
        var identifier = 0L
        val profileItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("My Profile")
                .withIcon(R.drawable.ic_drawer_user)
        val ordersItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Your Orders")
                .withIcon(R.drawable.ic_drawer_past_rides)
        val contactUsItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Contact Us")
                .withIcon(R.drawable.ic_drawer_mail)
        val signOutItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Sign out")
                .withIcon(R.drawable.ic_drawer_log_out)
        val contributorsItem = PrimaryDrawerItem().withIdentifier(++identifier).withName("Contributors")
                .withIcon(R.drawable.ic_drawer_info)
        drawer = DrawerBuilder()
                .withActivity(this)
                .withDisplayBelowStatusBar(false)
                .withHeader(headerLayout.root)
                .withTranslucentStatusBar(true)
                .withCloseOnClick(true)
                .withSelectedItem(-1)
                .addDrawerItems(
                        profileItem,
                        ordersItem,
                        DividerDrawerItem(),
                        signOutItem
                )
                .withOnDrawerItemClickListener { view, position, drawerItem ->
                    if (profileItem.identifier == drawerItem.identifier) {
                        startActivity(Intent(applicationContext, ProfileActivity::class.java))
                    }
                    if (ordersItem.identifier == drawerItem.identifier) {
                        startActivity(Intent(applicationContext, OrdersActivity::class.java))
                    }
                    if (contributorsItem.identifier == drawerItem.identifier) {
                        startActivity(Intent(applicationContext, ContributorsActivity::class.java))
                    }
                    if (contactUsItem.identifier == drawerItem.identifier) {
                        startActivity(Intent(applicationContext, ContactUsActivity::class.java))
                    }
                    if (signOutItem.identifier == drawerItem.identifier) {
                        MaterialAlertDialogBuilder(this@HomeActivity)
                                .setTitle("Confirm Sign Out")
                                .setMessage("Are you sure want to sign out?")
                                .setPositiveButton("Yes") { _, _ ->
                                    FcmUtils.unsubscribeFromTopic(AppConstants.NOTIFICATION_TOPIC_GLOBAL)
                                    FirebaseAuth.getInstance().signOut()
                                    preferencesHelper.clearPreferences()
                                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                                    finish()
                                }
                                .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                                .show()
                    }
                    true
                }
                .build()
    }

    var isError = false
    private fun setObservers() {
        viewModel.performFetchShopsStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        isError = false
                        if (!binding.swipeRefreshLayout.isRefreshing) {
                            binding.layoutStates.visibility = View.VISIBLE
                            binding.animationView.visibility = View.GONE
                        }
                        errorSnackbar.dismiss()
                        //progressDialog.setMessage("Getting Outlets")
                        //progressDialog.show()
                    }
                    Resource.Status.EMPTY -> {
                        isError = true
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("empty_animation.json")
                        binding.animationView.playAnimation()
                        //progressDialog.dismiss()
                        foodItemList.clear()
                        foodAdapter.notifyDataSetChanged()
                        errorSnackbar.setText("No Outlets in this place")
                        Handler().postDelayed({ errorSnackbar.show() }, 500)
                    }
                    Resource.Status.SUCCESS -> {
                        binding.swipeRefreshLayout.isRefreshing = false
                        isError = false
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.GONE
                        binding.animationView.cancelAnimation()
                        //progressDialog.dismiss()
                        errorSnackbar.dismiss()
                        foodItemList.clear()
                        foodItemList.addAll(it.data!!.toList())
                        foodAdapter.notifyDataSetChanged()
                        updateCartUI()
                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        isError = true
                        binding.swipeRefreshLayout.isRefreshing = false
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("no_internet_connection_animation.json")
                        binding.animationView.playAnimation()
                        //progressDialog.dismiss()
                        errorSnackbar.setText("No Internet Connection")
                        foodItemList.clear()
                        foodAdapter.notifyDataSetChanged()
                        Handler().postDelayed({ errorSnackbar.show() }, 500)
                    }
                    Resource.Status.ERROR -> {
                        isError = true
                        binding.swipeRefreshLayout.isRefreshing = false
                        //progressDialog.dismiss()
                        binding.layoutStates.visibility = View.GONE
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.loop(true)
                        binding.animationView.setAnimation("order_failed_animation.json")
                        binding.animationView.playAnimation()
                        errorSnackbar.setText("Something went wrong")
                        foodItemList.clear()
                        foodAdapter.notifyDataSetChanged()
                        Handler().postDelayed({ errorSnackbar.show() }, 500)
                    }
                }
            }
        })

        viewModel.performPlaceOrderStatus.observe(this, Observer {
            if (it != null) {
                if (it.status == Resource.Status.SUCCESS) {
                    isError = false
                    cartSnackBar.setText("Order place success")
                    Handler().postDelayed({ cartSnackBar.show() }, 500)
                }
                if (it.status == Resource.Status.ERROR) {
                    isError = true
                    errorOrderSnackbar.setText(it.message!!)
                    Handler().postDelayed({ errorOrderSnackbar.show() }, 500)
                }
            }
        })

        viewModel.performFetchProfileStatus.observe(this, Observer {
            if (it != null) {
                when (it.status) {
                    Resource.Status.LOADING -> {
                        isError = false

                    }
                    Resource.Status.EMPTY -> {
                        isError = false

                    }
                    Resource.Status.SUCCESS -> {
                        isError = false
                        it.data?.let { it1 ->
                            preferencesHelper.saveUser(userId = it1.userId, firstName = it1.firstName, lastName = it1.lastName, email = it1.email, name = "${it1.firstName} ${it1.lastName}", role = "", oauthId = "", place = "", photo = it1.photo)
                        }
                        updateGreetingMessage()

                    }
                    Resource.Status.OFFLINE_ERROR -> {
                        isError = true

                    }
                    Resource.Status.ERROR -> {
                        isError = true
                        errorSnackbar.setText("Cannot fetch profile")
                        Handler().postDelayed({ errorSnackbar.show() }, 500)
                    }
                }
            }
        })

    }

    private fun showConfirmOrderDialog(item: MenuItemModel) {
        val dialogBinding: BottomSheetShiftListBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.bottom_sheet_shift_list, null, false)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.foodItemName.text = "Ordering ${item.name}. Please choose shift"
        dialog.show()

        dialogBinding.buttonOrder.setOnClickListener {
            val shift = getShift(dialogBinding)
            if (shift != "") {
                dialog.hide()
                viewModel.placeOrder(item, shift)
                dialog.dismiss()
            } else {
                Toast.makeText(applicationContext, "Choose shift", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun getShift(dialogBinding: BottomSheetShiftListBinding): String {
        if (dialogBinding.morning.isChecked) {
            return "morning"
        }
        if (dialogBinding.day.isChecked) {
            return "day"
        }
        if (dialogBinding.night.isChecked) {
            return "night"
        }
        return ""
    }

    private fun setupShopRecyclerView() {
        foodAdapter = FoodAdapter(applicationContext, foodItemList, object : FoodAdapter.OnItemClickListener {
            override fun onItemClick(item: MenuItemModel?, position: Int) {}
            override fun onOrderClicked(item: MenuItemModel, position: Int) {
                showConfirmOrderDialog(item)
                //morning night day
            }

            override fun onQuantityAdd(position: Int) {
                println("quantity add clicked $position")
                if (cartList.size > 0) {
                    if (cartList[0].shopId == 2) {
                        foodItemList[position].quantity = foodItemList[position].quantity + 1
                        var k = 0
                        for (i in cartList.indices) {
                            if (cartList[i].id == foodItemList[position].id) {
                                cartList[i] = foodItemList[position]
                                k = 1
                                break
                            }
                        }
                        if (k == 0) {
                            cartList.add(foodItemList[position])
                        }
                        foodAdapter.notifyItemChanged(position)
                        updateCartUI()
                    }
                } else {
                    foodItemList[position].quantity = foodItemList[position].quantity + 1
                    cartList.add(foodItemList[position])
                    foodAdapter.notifyItemChanged(position)
                    updateCartUI()
                }
            }

            override fun onQuantitySub(position: Int) {
                println("quantity sub clicked $position")
                if (foodItemList[position].quantity - 1 >= 0) {
                    foodItemList[position].quantity = foodItemList[position].quantity - 1
                    for (i in cartList.indices) {
                        if (cartList[i].id == foodItemList[position].id) {
                            if (foodItemList[position].quantity == 0) {
                                cartList.removeAt(i)
                            } else {
                                cartList[i] = foodItemList[position]
                            }
                            break
                        }
                    }
                    foodAdapter.notifyItemChanged(position)
                    updateCartUI()
                }
            }
        }, false)
        binding.recyclerShops.layoutManager = LinearLayoutManager(this@HomeActivity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerShops.adapter = AlphaInAnimationAdapter(foodAdapter)
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.image_menu -> {
                drawer.openDrawer()
            }
            R.id.text_search -> {
                startActivity(Intent(applicationContext, SearchActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateGreetingMessage()
        viewModel.getUser()
        //Checking whether user has changed their place and refreshing shops accordingly
        if (placeId != preferencesHelper.getPlace()?.id.toString()) {
            placeId = preferencesHelper.getPlace()?.id.toString()
            viewModel.getFood()
        }
        cartList.clear()
        cartList.addAll(getCart())
        updateCartUI()
        updateHeaderLayoutUI()
        if (isError) {
            errorSnackbar.show()
        }
    }

    private fun updateCartUI() {
        var total = 0
        var totalItems = 0
        if (cartList.size > 0) {
            for (i in cartList.indices) {
                total += cartList[i].price * cartList[i].quantity
                totalItems += 1
            }
            if (totalItems == 1) {
                cartSnackBar!!.setText("₹$total | $totalItems item")
            } else {
                cartSnackBar!!.setText("₹$total | $totalItems items")
            }
            cartSnackBar!!.show()
        } else {
            cartSnackBar!!.dismiss()
        }
    }

    fun getCart(): ArrayList<MenuItemModel> {
        val items: ArrayList<MenuItemModel> = ArrayList()
        val temp = preferencesHelper.getCart()
        if (!temp.isNullOrEmpty()) {
            items.addAll(temp)
        }
        return items
    }

    private fun updateGreetingMessage() {
        val timeOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        var message = ""
        when (timeOfDay) {
            in 0..11 -> message = "Good Morning,\n"
            in 12..15 -> message = "Good Afternoon,\n"
            in 16..23 -> message = "Good Evening,\n"
        }
        var temp = preferencesHelper.name
        var tempList = temp?.split(" ")
        message += if (!tempList.isNullOrEmpty()) {
            tempList[0]
        } else {
            preferencesHelper.name
        }
        binding.textGreeting.text = message
    }


    override fun onBackPressed() {
        MaterialAlertDialogBuilder(this@HomeActivity)
                .setTitle("Exit app?")
                .setMessage("Are you sure want to exit the app?")
                .setPositiveButton("Yes") { dialog, which ->
                    super.onBackPressed()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, which -> dialog.dismiss() }
                .show()
    }

    private fun getFCMToken() {
        FirebaseInstanceId.getInstance().instanceId
                .addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w("FCM", "getInstanceId failed", task.exception)
                        return@OnCompleteListener
                    }
                    // Get new Instance ID token
                    val token = task.result?.token
                    if (preferencesHelper.fcmToken != token) {
                        preferencesHelper.fcmToken = token
                        preferencesHelper.fcmToken?.let {
                            profileViewModel.updateFcmToken(NotificationTokenUpdate(it, preferencesHelper.userId.toString()))
                        }
                    } else {
                        //FCM token is same. No need to update
                    }
                    val msg = "FCM TOKEN " + token
                    Log.d("FCM", msg)
                })
    }

}
