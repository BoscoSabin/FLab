package com.f22lab.task.ui

import android.app.Dialog
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.f22lab.task.R
import com.f22lab.task.interfaces.AdapterItemClickListener
import com.f22lab.task.adapter.FoodAdapter
import com.f22lab.task.constants.Constants
import com.f22lab.task.controller.HomeActivityContoller
import com.f22lab.task.data.AppData
import com.f22lab.task.dialog.DialogManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), HomeActivityContoller.MainPresenter, AdapterItemClickListener, DialogManager.DialogListener {
    override fun onDialogClick(requestCode: Int, dialog: Dialog, any: Any, type: String) {
        when (type) {
            "LowToHigh" -> mFoodAdapter.sortListByPrice()
            "Rating" -> mFoodAdapter.sortListByRating()
        }
    }

    override fun onItemClick(v: View, position: Int, data: Any, requestType: String) {
        val food = data as AppData.FoodsData

        when (requestType) {
            "AddItemToCart" -> mMainPresenter.addItemToCart(food)
            "RemoveItemFromCart" -> mMainPresenter.removeItemFromCart(food)
            "Details" -> mMainPresenter.navigateDetailScreen(food)
        }
        updateCartCount()
    }


    override fun onSuccess(type: Int, response: Any?) {
        response?.let {
            progressBar.visibility = View.GONE
            val foodsData = response as List<AppData.FoodsData>
            mFoodAdapter.addItems(mMainPresenter.updateCartItemQuantity(foodsData))
        }
    }

    override fun onErrorResponse(type: Int, response: Any?) {

    }

    override fun onFailure(type: Int, response: Any) {

    }

    val TAG = "MainActivity"
    lateinit var mFoodAdapter: FoodAdapter
    lateinit var mMainPresenter: HomeActivityContoller
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        setupRecyclerview()
        mMainPresenter = HomeActivityContoller(this, this)
        mMainPresenter.getFoodListItems()

    }

    private fun setupRecyclerview() {
        mFoodAdapter = FoodAdapter(applicationContext, this)
        val layoutManager = LinearLayoutManager(applicationContext)
        rvFoodList.layoutManager = layoutManager
        rvFoodList.adapter = mFoodAdapter

    }

    private lateinit var mMenu: Menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mMenu = menu
        // Inflate the menu to use in the action bar
        val inflater = menuInflater
        inflater.inflate(R.menu.home_menu, menu)
        mMainPresenter.setCartItemsCount(mMenu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        when (item.itemId) {
            R.id.action_filter -> {
                if (!mFoodAdapter.getListItem().isEmpty())
                    mMainPresenter.showFilterDialog()
                return true
            }
            R.id.action_cart -> {
                mMainPresenter.navigateCartScreen()
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        if (!mFoodAdapter.getListItem().isEmpty())
            mFoodAdapter.refreshQuantity(mMainPresenter.updateCartItemQuantity(mFoodAdapter.getListItem()))
        updateCartCount()
    }

    private fun updateCartCount() {
        if (::mMenu.isInitialized)
            mMainPresenter.setCartItemsCount(mMenu)
    }
}
