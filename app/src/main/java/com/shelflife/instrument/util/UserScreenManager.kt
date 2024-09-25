package com.shelflife.instrument.util

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.shelflife.instrument.BundleVar
import com.shelflife.instrument.R
import com.shelflife.instrument.ui.ProductFragment
import com.shelflife.instrument.ui.menu.CategoryFragment
import com.shelflife.instrument.ui.menu.MainFragment
import com.shelflife.instrument.ui.menu.NotificationFragment
import com.shelflife.instrument.ui.menu.OptionFragment
import java.util.Date
import javax.inject.Inject

class UserScreenManager  @Inject constructor(){

    fun openMainFragment(activityParent: AppCompatActivity, categoryId: Int?=null, productId: Int = 0){
        val fragment = MainFragment()
        val bundle = Bundle()

        if(productId!=0){
            bundle.putInt(BundleVar.ProductID, productId)
            fragment.arguments = bundle
        }

        categoryId?.let {
            bundle.putInt(BundleVar.CategoryID, it)
            fragment.arguments = bundle
        }
        openFragment(activityParent, fragment)
    }

    fun openCategoryFragment(activityParent: AppCompatActivity){
        val fragment = CategoryFragment()
        openFragment(activityParent, fragment)
    }

    fun openOptionsFragment(activityParent: AppCompatActivity){
        val fragment = OptionFragment()
        openFragment(activityParent, fragment)
    }

    fun openProductFragment(activityParent: AppCompatActivity, productId: Int?=null){
        val fragment = ProductFragment()
        val bundle = Bundle()
        productId?.let {
            bundle.putInt(BundleVar.ProductID, it)
            fragment.arguments = bundle
        }
        openFragment(activityParent, fragment)
    }

    fun openNotificationFragment(activityParent: AppCompatActivity){
        val fragment = NotificationFragment()
        openFragment(activityParent, fragment)
    }

    private fun openFragment(
        activityParent: AppCompatActivity,
        fragment: Fragment,
        pDeletePreviewFragmentInBackstack: Boolean = false
    ) {
        val fragmentManager = activityParent.supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()

        val isNeedPopBackStack = fragmentManager.findFragmentByTag(ProductFragment::class.simpleName)!=null

        fragmentTransaction.replace(R.id.fContainerView, fragment,fragment::class.simpleName)
        fragmentTransaction.addToBackStack("${fragment::class.simpleName}")

        if(pDeletePreviewFragmentInBackstack || isNeedPopBackStack){
            fragmentManager.popBackStack()
        }

        fragmentTransaction.commit()
    }
}