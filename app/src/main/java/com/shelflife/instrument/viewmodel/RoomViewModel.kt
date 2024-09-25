package com.shelflife.instrument.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shelflife.instrument.model.CategoryData
import com.shelflife.instrument.model.room.Category
import com.shelflife.instrument.model.room.Notification
import com.shelflife.instrument.model.room.Product
import com.shelflife.instrument.repository.RoomRepository
import com.shelflife.instrument.ui.custom.TypeMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class RoomViewModel @Inject constructor(private val repository: RoomRepository) : ViewModel() {

    private val showMessage = MutableLiveData<Pair<String, TypeMessage>>()
    val getShowMessage: LiveData<Pair<String, TypeMessage>> get() = showMessage

    suspend fun insertCategory(category: Category): Long{
         return repository.insertCategory(category)
    }

    fun updateCategory(category: Category){
        CoroutineScope(Dispatchers.Main).launch {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category){
        CoroutineScope(Dispatchers.Main).launch {
            val totalCat = repository.deleteCategory(category)
            if(totalCat>0){
                showMessage.value = Pair("Категория удалена", TypeMessage.MESSAGE)
            }else{
                showMessage.value = Pair("Ошибка при удалении записи", TypeMessage.ERROR)
            }
        }
    }

    //private var categories = MutableLiveData<List<Category>>()
    //val getAllCategories: LiveData<List<Category>> get() = categories

    fun getAllCategories(): LiveData<List<Category>> {
        return repository.getAllCategories()
    }

    fun getModCategories(): LiveData<List<Category>>{
        return repository.getModCategories()
    }

    suspend fun insertProduct(product: Product): Long{
        return repository.insertProduct(product)
    }

    suspend fun updateProduct(product: Product){
        return repository.updateProduct(product)
    }

    suspend fun saveProduct(product: Product, oldProduct: Product?=null){
        if(oldProduct==null){
            repository.insertProduct(product)
            return
        }

        //Update product
        oldProduct.apply {
            productName = product.productName
            categoryId = product.categoryId
            dateStart = product.dateStart
            dateEnd = product.dateEnd
            shelfLife = product.shelfLife
            notificationPeriod = product.notificationPeriod
            notificationExpired = product.notificationExpired
        }
        repository.updateProduct(oldProduct)
    }

    private val products = MutableLiveData<List<Product>>()

    val getSelectedProducts: LiveData<List<Product>> get() = products

    suspend fun getSelectedProduct(productId: Int): Product{
        return repository.getSelectedProduct(productId)
    }

    suspend fun getProducts(categoryId: Int?=null){
        products.value = repository.getProducts(categoryId)
    }

    suspend fun getSearchProducts(inputText: String){
        products.value = repository.getSearchProducts(inputText)
    }

    suspend fun deleteProduct(product: Product): Int{
        val result = repository.deleteProduct(product)
        return result
    }

    suspend fun getCategoryData(categoryId: Int): CategoryData {
        return repository.getCategoryData(categoryId)
    }

    private val notifications = MutableLiveData<List<Notification>>()
    val getListNotification: LiveData<List<Notification>> get() = notifications

    fun getNotifications(){
        CoroutineScope(Dispatchers.Main).launch {
            notifications.value = repository.getNotifications()
        }
    }
}