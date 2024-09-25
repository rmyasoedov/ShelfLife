package com.shelflife.instrument.repository

import androidx.lifecycle.LiveData
import com.shelflife.instrument.model.CategoryData
import com.shelflife.instrument.model.room.Category
import com.shelflife.instrument.model.room.Notification
import com.shelflife.instrument.model.room.Product
import com.shelflife.instrument.room.Dao
import com.shelflife.instrument.util.MyDateFormatter
import javax.inject.Inject

class RoomRepository @Inject constructor(private val dataBase: Dao) {

    suspend fun insertCategory(category: Category): Long{
        return dataBase.insertCategory(category)
    }

    suspend fun updateCategory(category: Category){
        dataBase.updateCategory(category)
    }

    suspend fun deleteCategory(category: Category): Int{
        return dataBase.deleteCategory(category)
    }

    fun getAllCategories(): LiveData<List<Category>>{
        return dataBase.getAllCategory()
    }

    fun getModCategories(): LiveData<List<Category>>{
        return dataBase.getModCategory()
    }


    //Products
    suspend fun insertProduct(product: Product): Long{
        return dataBase.insertProduct(product)
    }

    suspend fun updateProduct(product: Product){
        return dataBase.updateProduct(product)
    }

    suspend fun getSelectedProduct(productId: Int): Product{
        return dataBase.getSelectedProduct(productId)
    }

    suspend fun getProducts(categoryId: Int?=null): List<Product>{
        return if(categoryId==null){
            dataBase.getAllProducts()
        }else{
            dataBase.getProducts(categoryId)
        }
    }

    suspend fun remindTomorrow(productId: Int){
        try {
            //Достаем продукт из БД
            val product = dataBase.getSelectedProduct(productId)
            //Считаем сколько дней доконца хранения
            val untilDays = MyDateFormatter.calculateDaysUntil(product.dateEnd)

            //Если остается больше одного дня, то ставив напоинание на день меньше
            if(untilDays>1){
                product.notificationPeriod = "${untilDays - 1}"
            }else{ //если осталось 1 и менее дней, то активируем напоминание об окончании срора
                product.notificationPeriod = ""
                product.notificationExpired = true
            }
            dataBase.updateProduct(product)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    suspend fun getSearchProducts(inputText: String): List<Product>{
        return dataBase.getSearchProducts(inputText)
    }

    suspend fun deleteProduct(product: Product): Int{
        return dataBase.deleteProduct(product)
    }

    suspend fun deleteProductId(id: Int){
        dataBase.deleteProductId(id)
    }

    suspend fun getCategoryData(categoryId: Int): CategoryData{
        return dataBase.getCategoryData(categoryId)
    }

    suspend fun getDelayProducts(): List<Product>{
        return dataBase.getDelayProducts()
    }

    suspend fun getExpiredProducts(): List<Product>{
        return dataBase.getExpiredProducts()
    }

    suspend fun resetNotificationPeriod(id: Int){
        dataBase.resetNotificationPeriod(id)
    }

    suspend fun insertNotify(notification: Notification): Long{
        return dataBase.insertAndDeleteNotify(notification)
    }

    suspend fun getNotifications(): List<Notification>{
        return dataBase.getNotification()
    }
}