package com.shelflife.instrument.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shelflife.instrument.databinding.ItemCategoryBinding
import com.shelflife.instrument.model.room.Category
import com.shelflife.instrument.util.AnimateView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class AdapterCategory(val listCategory: List<Category>, val listener: IEvent) :
    RecyclerView.Adapter<AdapterCategory.CategoryViewHolder>() {

    interface IEvent{
        fun onClick(category: Category)
    }

    private var selectedIndex: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CategoryViewHolder(binding)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: CategoryViewHolder, @SuppressLint("RecyclerView") position: Int) {holder.binding.apply{
        tvCategoryName.text = listCategory[position].categoryName

        clMain.setOnClickListener {
            AnimateView(it).animateAlpha()
            listener.onClick(listCategory[position])
        }
    }}

    override fun getItemCount() = listCategory.size

    class CategoryViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)
}