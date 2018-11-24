package com.f22lab.task.controller

import com.f22lab.task.data.AppData

class RoomController {
    private val mDatabase = AppApplication.instance.databaseInstance

    fun addItem(foodsData: AppData.FoodsData) {
        mDatabase.userDao().insert(foodsData)
    }

    fun getItems(): List<AppData.FoodsData> {
        return mDatabase.userDao().getAll()
    }

    fun removeItem(food: AppData.FoodsData) {
        mDatabase.userDao().delete(food.itemName)
    }

    fun deleteItem(food: AppData.FoodsData) {

        if (food.quantity > 0) {
            addItem(food)
        } else removeItem(food)

    }

}
