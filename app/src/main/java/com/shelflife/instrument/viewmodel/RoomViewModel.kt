package com.shelflife.instrument.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shelflife.instrument.model.CategoryData
import com.shelflife.instrument.model.room.Category
import com.shelflife.instrument.model.room.Notification
import com.shelflife.instrument.model.room.Product
import com.shelflife.instrument.repository.RoomRepository
import com.shelflife.instrument.ui.custom.TypeMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
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

    private val products = MutableStateFlow<SelectedType>(SelectedType.GetProducts())
    val getSelectedProducts: StateFlow<List<Product>> = products
        .flatMapLatest {type->
            flow {
                val result = when(type){
                    is SelectedType.SearchProducts -> repository.getSearchProducts(type.inputText)
                    is SelectedType.GetProducts -> repository.getProducts(type.categoryId)
                }
                emit(result)
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, listOf())

    fun getProducts(categoryId: Int?=null){
        products.value = SelectedType.GetProducts(categoryId)
    }

    fun getSearchProducts(inputText: String){
        products.value = SelectedType.SearchProducts(inputText)
    }

    sealed class SelectedType{
        data class SearchProducts(val inputText: String): SelectedType()
        data class GetProducts(val categoryId: Int?=null): SelectedType()
    }

    suspend fun getSelectedProduct(productId: Int): Product{
        return repository.getSelectedProduct(productId)
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