package com.f22lab.task.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.bumptech.glide.Glide
import com.f22lab.task.R
import com.f22lab.task.constants.Constants
import com.f22lab.task.controller.RoomController
import com.f22lab.task.data.AppData
import kotlinx.android.synthetic.main.activity_food_details.*

class FoodDetailedActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.ivRemoveItem -> {
                mFoodData.quantity = if (mFoodData.quantity > 0) --mFoodData.quantity else 0
                updateQuantity()
                mRoomController.deleteItem(mFoodData)
            }
            R.id.ivAddItem -> {
                ++mFoodData.quantity
                updateQuantity()
                mRoomController.addItem(mFoodData)
            }
        }
        Log.d(TAG, "onItemClick: " + mRoomController.getItems())
    }

    val TAG = "FoodDetailedActivity"
    private lateinit var mFoodData: AppData.FoodsData
    private val mRoomController = RoomController()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_details)
        setupActionBar()


        intent?.extras?.let {
            mFoodData = intent.getSerializableExtra(Constants.FOODDETAILS) as AppData.FoodsData
            updateItems()
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(toolbarFoodDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        setTitle("Food Details")
    }

    fun updateItems() {
        ivRemoveItem.setOnClickListener(this)
        ivAddItem.setOnClickListener(this)
        tvFoodName.text = mFoodData.itemName
        tvPrice.text = getString(R.string.food_price, mFoodData.itemPrice)
        tvRating.text = getString(R.string.food_rating, mFoodData.averageRating)
        updateQuantity()
        Glide.with(applicationContext).load(mFoodData.imageUrl).into(ivFoodPic)
    }

    private fun updateQuantity() {
        tvItemCount.text = if (mFoodData.quantity <= 0) "Add" else "${mFoodData.quantity}"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}