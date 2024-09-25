package com.shelflife.instrument.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.shelflife.instrument.MyConst
import com.shelflife.instrument.model.CategoryData
import com.shelflife.instrument.model.room.Category
import com.shelflife.instrument.model.room.Notification
import com.shelflife.instrument.model.room.Product

@Dao
interface Dao {

    //------------------Category---------------------------------------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertDefaultCategory(category: Category = Category(1,"Общая"))

    @Insert
    suspend fun insertCategory(category: Category): Long

    @Delete
    suspend fun deleteCategory(category: Category): Int

    @Update
    suspend fun updateCategory(category: Category): Int

    @Query("SELECT * FROM category ORDER BY CASE WHEN id = 1 THEN 0 ELSE 1 END, category_name ASC")
    fun getAllCategory(): LiveData<List<Category>>

    @Query("SELECT * FROM category WHERE id<>1 ORDER BY CASE WHEN id = 1 THEN 0 ELSE 1 END, category_name ASC")
    fun getModCategory(): LiveData<List<Category>>
    //-------------------Product----------------------------------------

    @Insert
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product): Int

    @Query("DELETE FROM product WHERE id=:id")
    suspend fun deleteProductId(id: Int)

    @Query("DELETE FROM product WHERE date_end<=date('now')")
    suspend fun deleteExpiredProducts()

    @Query("SELECT *FROM product ORDER BY date_end ASC")
    suspend fun getAllProducts(): List<Product>

    @Query("SELECT *FROM product WHERE id=:productId")
    suspend fun getSelectedProduct(productId: Int): Product

    @Query("SELECT *FROM product WHERE category_id=:categoryId ORDER BY date_end ASC")
    suspend fun getProducts(categoryId: Int): List<Product>

    @Query("SELECT *FROM product WHERE product_name LIKE :inputName ORDER BY date_end ASC")
    suspend fun getSearchProducts(inputName: String): List<Product>

    @Query("SELECT * FROM product WHERE date_end>date('now') AND notification_period<>''")
    suspend fun getDelayProducts(): List<Product>

    @Query("SELECT *FROM product WHERE date_end<=date('now') AND notification_expired=1")
    suspend fun getExpiredProducts(): List<Product>

    @Query("SELECT count(*) FROM product WHERE category_id=:categoryId")
    fun getQuantityProduct(categoryId: Int): Int

    @Query("UPDATE product SET notification_period = '' WHERE id=:id")
    suspend fun resetNotificationPeriod(id: Int)

    @Query("""
        SELECT
        COUNT(*) as "totalCount", 
        COUNT(CASE WHEN date_end<=date('now') THEN 1 END) as "totalExpired" 
        FROM product 
        WHERE category_id=:categoryId
    """)
    suspend fun getCategoryData(categoryId: Int): CategoryData

    //----------------Notification-------------------------------------

    @Insert
    suspend fun insertNotify(notification: Notification): Long

    @Query("SELECT *FROM notification ORDER BY date_time DESC")
    suspend fun getNotification(): List<Notification>

    @Query("""
        DELETE FROM notification 
        WHERE id NOT IN (
            SELECT id FROM notification 
            ORDER BY date_time DESC 
            LIMIT ${MyConst.limitMotifyMessage}
        )
    """)
    suspend fun deleteOldRecords()

    @Transaction
    suspend fun insertAndDeleteNotify(notification: Notification): Long{
        val newRecord = insertNotify(notification)
        deleteOldRecords()
        return newRecord
    }
}