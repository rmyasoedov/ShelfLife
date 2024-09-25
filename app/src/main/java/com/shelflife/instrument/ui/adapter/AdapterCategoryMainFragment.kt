package com.shelflife.instrument.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.shelflife.instrument.R
import com.shelflife.instrument.model.room.Category

class AdapterCategoryMainFragment(
    val appContext: Context,
    val listCategory: List<Category>,
    val resource: Int = R.layout.item_category_spinner
) :
    ArrayAdapter<Category>(appContext, resource, listCategory) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View = convertView ?: LayoutInflater.from(appContext).inflate(R.layout.title_category, parent, false)
        val item = getItem(position)

        val tvCategory = view.findViewById<TextView>(R.id.tvItem)
        tvCategory.text = item?.categoryName ?: ""

        return view
    }

    fun getPositionCategory( categoryId: Int): Int{
        return listCategory.indexOfFirst { it.id==categoryId }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(appContext).inflate(resource, parent, false)
        val item = getItem(position)

        val tvCategory = view.findViewById<TextView>(R.id.tvItem)
        tvCategory.text = item?.categoryName ?: ""

        return view
    }

}