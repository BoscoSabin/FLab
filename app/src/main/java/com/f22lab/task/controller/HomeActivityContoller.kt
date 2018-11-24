package com.f22lab.task.controller

import android.app.Activity
import android.content.Intent
import android.util.Log
import com.f22lab.task.data.AppData
import com.f22lab.task.dialog.DialogManager
import com.f22lab.task.rest.ApiController
import com.f22lab.task.util.CountDrawable
import android.graphics.drawable.LayerDrawable
import android.support.design.widget.Snackbar
import android.view.Menu
import com.f22lab.task.R
import com.f22lab.task.constants.Constants
import com.f22lab.task.ui.CartActivity
import com.f22lab.task.ui.FoodDetailedActivity
import kotlinx.android.synthetic.main.activity_main.*


class HomeActivityContoller(val mContext: Activity, val mPresenter: MainPresenter) : ApiController.ApiCallBack {
    override fun onSuccess(type: Int, response: Any?) {
        mPresenter.onSuccess(type, response)
    }

    override fun onErrorResponse(type: Int, response: Any?) {
        mPresenter.onErrorResponse(type, response)
    }


    override fun onFailure(type: Int, response: Any) {
        mPresenter.onFailure(type, response)
    }

    private var mApiController = ApiController(this)
    private var mDialogManager = DialogManager(mContext, mContext as DialogManager.DialogListener)
    private var mRoomController = RoomController()


    fun getFoodListItems() {
        mApiController.foodItemsApiCall(1)
    }

    fun showFilterDialog() {
        mDialogManager.showFilterDialog()
    }

    fun addItemToCart(foodsData: AppData.FoodsData) {
        mRoomController.addItem(foodsData)
    }

    fun getAllItems(): List<AppData.FoodsData> {
        return mRoomController.getItems()
    }

    fun removeItemFromCart(food: AppData.FoodsData) {
        mRoomController.deleteItem(food)

    }

    private val TAG = "HomePresenter"

    fun updateCartItemQuantity(listItem: List<AppData.FoodsData>): List<AppData.FoodsData> {
        val items = getAllItems()
        for (item in items) {
            listItem.find { it -> it.itemName == item.itemName }?.quantity = item.quantity

        }

        Log.d(TAG, "updateCartItemQuantity : " + items)
        return listItem
    }


    interface MainPresenter {
        fun onSuccess(type: Int, response: Any?)

        fun onErrorResponse(type: Int, response: Any?)

        fun onFailure(type: Int, response: Any)

    }

    fun setCartItemsCount(menu: Menu) {
        val menuItem = menu.findItem(R.id.action_cart)
        val icon = menuItem.getIcon() as LayerDrawable

        val badge: CountDrawable

        // Reuse drawable if possible
        val reuse = icon.findDrawableByLayerId(R.id.ic_group_count)
        if (reuse != null && reuse is CountDrawable) {
            badge = reuse
        } else {
            badge = CountDrawable(mContext)
        }

        badge.setCount(getAllItems().sumBy { it.quantity }.toString())
        icon.mutate()
        icon.setDrawableByLayerId(R.id.ic_group_count, badge)
    }

    fun showSnackBar(message: String) {
        val snackbar = Snackbar.make(mContext.main_content, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }

    fun navigateCartScreen() {
        if (getAllItems().isEmpty())
            showSnackBar(mContext.getString(R.string.no_item_message))
        else
            mContext.startActivity(Intent(mContext, CartActivity::class.java))
    }

    fun navigateDetailScreen(food: AppData.FoodsData) {
        val intent = Intent(mContext, FoodDetailedActivity::class.java)
        intent.putExtra(Constants.FOODDETAILS, food)
        mContext.startActivity(intent)
    }
}