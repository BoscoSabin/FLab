package com.f22lab.task.ui

import android.app.Dialog
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.f22lab.task.adapter.CartAdapter
import com.f22lab.task.controller.RoomController
import com.f22lab.task.data.AppData
import com.f22lab.task.dialog.DialogManager
import com.f22lab.task.interfaces.AdapterItemClickListener
import kotlinx.android.synthetic.main.activity_cart.*
import android.support.design.widget.Snackbar
import com.f22lab.task.R


class CartActivity : AppCompatActivity(), View.OnClickListener, AdapterItemClickListener, DialogManager.DialogListener {
    override fun onDialogClick(requestCode: Int, dialog: Dialog, any: Any, type: String) {
        val couponCode = (any as String).trim { it <= ' ' }
        calculateGrandAmount(couponCode)

    }

    override fun onItemClick(v: View, position: Int, data: Any, requestType: String) {
    }

    override fun onClick(view: View?) {
    }

    val TAG = "CartActivity"
    private val mRoomController = RoomController()
    private lateinit var mDialogManager: DialogManager

    private lateinit var mCartItem: List<AppData.FoodsData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        setupActionBar()
        setupRecyclerview()
        mDialogManager = DialogManager(this, this)


    }

    private fun setupActionBar() {
        setSupportActionBar(toolbarCart)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        setTitle("Your Cart")
    }

    private lateinit var mCartAdapter: CartAdapter

    private fun setupRecyclerview() {
        mCartItem = mRoomController.getItems()
        mCartAdapter = CartAdapter(applicationContext, this)
        val layoutManager = LinearLayoutManager(applicationContext)
        rvCartItem.layoutManager = layoutManager
        rvCartItem.adapter = mCartAdapter
        mCartAdapter.updateAdapter(mCartItem)

        calculateGrandAmount("")


    }

    private var deliveryCharge: Double = 30.0

    private fun calculateGrandAmount(couponCode: String) {
        var total = mCartItem.sumByDouble { it.itemPrice * it.quantity }
        tvTotalAmount.text = "$total"
        total = applyCouponCode(couponCode, total)
        tvDeliveryCharge.text = getString(R.string.price_format, deliveryCharge)
        tvGrandTotal.text = getString(R.string.price_format, (total + deliveryCharge))

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.offer_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_coupon -> {
                if (mCartAdapter.itemCount < 1) {
                    return false
                }
                mDialogManager.applyCouponDialog()
                return true
            }


        }
        return super.onOptionsItemSelected(item)
    }

    fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(llCartRootView, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    private fun applyCouponCode(couponCode: String, totalAmount: Double): Double {
        tvDiscount.visibility = View.GONE
        deliveryCharge = 30.0
        when (couponCode) {
            "F22LABS" -> {
                if (totalAmount > 400) {
                    tvDiscount.visibility = View.VISIBLE
                    tvDiscount.text = getString(R.string.feelab_applied)
                    return totalAmount - totalAmount * 20 / 100
                } else showSnackBar(getString(R.string.flaberrormessage))

            }
            "FREEDEL" -> {
                if (totalAmount > 100) {
                    tvDiscount.visibility = View.VISIBLE
                    deliveryCharge = 0.0
                    tvDiscount.text = getString(R.string.freedel_applied)
                } else showSnackBar(getString(R.string.feedel_error_message))

            }
            else -> {
                if (!couponCode.isEmpty())
                    showSnackBar(getString(R.string.invalid_coupon_message))

            }
        }

        return totalAmount
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}