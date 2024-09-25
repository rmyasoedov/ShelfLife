package com.shelflife.instrument.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shelflife.instrument.R
import com.shelflife.instrument.databinding.ItemCategoryBinding
import com.shelflife.instrument.databinding.ItemProductBinding
import com.shelflife.instrument.model.room.Category
import com.shelflife.instrument.model.room.Product
import com.shelflife.instrument.util.AnimateView
import com.shelflife.instrument.util.MyDateFormatter
import com.shelflife.instrument.util.MyUiElementFormatter
import java.time.LocalDate
import java.time.Period

class AdapterProduct(
    val listProduct: List<Product>,
    val listCategory: List<Category>,
    val listener: IEvent
): RecyclerView.Adapter<AdapterProduct.ProductViewHolder>() {

    interface IEvent{
        fun onClick(product: Product)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ProductViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {holder.binding.apply {

        tvName.text = listProduct[position].productName
        tvCategory.text = listCategory.find {
            it.id==listProduct[position].categoryId
        }?.categoryName ?: ""

        tvDateEnd.text = "до ${MyDateFormatter.convertFromDateFormat(listProduct[position].dateEnd)}"

        val (years, months, days) = MyDateFormatter.calculateTimeUntil(listProduct[position].dateEnd)

        if(years==0 && months==0 && days==0){
            MyUiElementFormatter.setBgColor(tvUntilDays, ContextCompat.getColor(root.context, R.color.bg_color_expired))
            tvUntilDays.setTextColor(ContextCompat.getColor(root.context, R.color.text_color_expired))
            tvUntilDays.text = "Просрочен"
        }else{
            MyUiElementFormatter.setBgColor(tvUntilDays, ContextCompat.getColor(root.context, R.color.bg_marker_color))
            tvUntilDays.setTextColor(ContextCompat.getColor(root.context, R.color.marker_text_color))
            var reamined = ""

            if(years>0){
                reamined = "${years}г "
            }
            if(months>0){
                reamined+= "${months}м "
            }
            reamined+="${days} дн"

            tvUntilDays.text = reamined
        }

        root.setOnClickListener {
            AnimateView(it).animateAlpha()
            listener.onClick(listProduct[position])
        }
    }}

    override fun getItemCount() = listProduct.size

    class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)
}