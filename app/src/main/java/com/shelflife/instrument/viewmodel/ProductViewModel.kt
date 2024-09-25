package com.shelflife.instrument.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.shelflife.instrument.model.Options
import com.shelflife.instrument.repository.SharedPreferenceRepository
import com.shelflife.instrument.ui.ProductFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ProductViewModel @Inject constructor(private val sharedPreference: SharedPreferenceRepository) : ViewModel() {

    private val messageError = MutableLiveData<String>()
    val showMessageError: LiveData<String> get() = messageError

    private val options = MutableLiveData<Options>()
    val getOptions: LiveData<Options> get() = options

    fun getDefaultOptions(){
        options.value = sharedPreference.getOptions() ?: Options()
    }

    fun showSnackBarError(message: String){
        messageError.value = message
    }

    fun calculateDateEnd(
        dateStart: String, shelfLife: Int, longPeriod: ProductFragment.LongPeriod
    ): Pair<Boolean, String>{

        // Форматируем дату
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateStart)

        // Используем Calendar для работы с датой
        val calendar = Calendar.getInstance()
        calendar.time = date

        when(longPeriod){
            ProductFragment.LongPeriod.DAYS -> {
                calendar.add(Calendar.DAY_OF_YEAR, shelfLife)
            }
            ProductFragment.LongPeriod.MONTHS -> {
                calendar.add(Calendar.MONTH, shelfLife)
            }
        }

        val dateEndParse = calendar.time

        if(dateEndParse<= Date()){
            return Pair(true, "Укзанный срок годности уже истек")
        }

        val dateEnd = sdf.format(calendar.time)

        return Pair(false, dateEnd)
    }


}