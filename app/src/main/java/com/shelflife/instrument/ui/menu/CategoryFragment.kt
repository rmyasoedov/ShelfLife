package com.shelflife.instrument.ui.menu

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.shelflife.instrument.MyApp
import com.shelflife.instrument.databinding.FragmentCategoryBinding
import com.shelflife.instrument.ui.dialogs.InputDialogFragment
import com.shelflife.instrument.util.AnimateView
import com.shelflife.instrument.viewmodel.RoomViewModel
import javax.inject.Inject
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shelflife.instrument.R
import com.shelflife.instrument.factory.RoomViewModelFactory
import com.shelflife.instrument.model.room.Category
import com.shelflife.instrument.ui.BaseFragment
import com.shelflife.instrument.ui.MainActivity
import com.shelflife.instrument.ui.adapter.AdapterCategory
import com.shelflife.instrument.ui.dialogs.AboutCategoryDialog
import com.shelflife.instrument.ui.dialogs.ConfirmDialog
import com.shelflife.instrument.util.UserScreenManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CategoryFragment : BaseFragment() {

    init {
        MyApp.getComponent().inject(this)
    }

    private lateinit var _binding: FragmentCategoryBinding
    private val binding get() = _binding

    @Inject
    lateinit var userScreenManager: UserScreenManager

    @Inject
    lateinit var viewModelFactory: RoomViewModelFactory
    private val roomViewModel: RoomViewModel by viewModels{viewModelFactory}

    private lateinit var adapterCategory: AdapterCategory

    private var selectedCategory: Category?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryBinding.inflate(inflater, container, false)

        roomViewModel.getModCategories().observe(viewLifecycleOwner){listCategories->
            adapterCategory = AdapterCategory(listCategories, object : AdapterCategory.IEvent{
                override fun onClick(category: Category) {
                    onClickCategory(category)
                }
            })
            binding.rvCategories.layoutManager = LinearLayoutManager(requireContext())
            binding.rvCategories.adapter = adapterCategory
        }

        binding.tvAddButton.setOnClickListener {
            AnimateView(it).animateAlpha()

            try {
                selectedCategory = null
                adapterCategory.notifyDataSetChanged()
            }catch (e: Exception){}

            InputDialogFragment(
                pTextTitle = "Категория",
                hintText = "Введите название категории",
                listener = object : InputDialogFragment.IEvent{
                    override fun onClose() {}

                    override fun onAccent(textValue: String) {
                        val newCategory = Category(categoryName = textValue)
                        CoroutineScope(Dispatchers.Main).launch {
                            roomViewModel.insertCategory(newCategory)
                        }
                    }
                }
            ).show(requireActivity().supportFragmentManager, "InputCategory")
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()


        roomViewModel.getShowMessage.observe(this){
            showSnackBar(binding.root, it.second, it.first)
        }
    }

    private fun onClickCategory(category: Category){
        AboutCategoryDialog(category, object : AboutCategoryDialog.IEvent{
            override fun onDelete(waitingDelete: Boolean) {
                if(waitingDelete){
                    ConfirmDialog(
                        pTitle = "Внимание!",
                        pDescription = "Удаление приведет к потере уже привязанных товаров к данной категории. Продолжить?",
                        pOkButton = "Да",
                        pCancelButton = "Нет",
                        listener = object : ConfirmDialog.IEvent{

                            override fun onOk() {
                                roomViewModel.deleteCategory(category)
                            }

                            override fun onCancel() {}
                            override fun onClose() {}
                        }
                    ).show(requireActivity().supportFragmentManager, "ConfirmDialog")
                }else{
                    roomViewModel.deleteCategory(category)
                }
            }

            override fun onEdit() {
                onEditCategory(category)
            }

            override fun onOpenProducts() {
                userScreenManager.openMainFragment(requireActivity() as MainActivity, category.id)
            }
        }).show(requireActivity().supportFragmentManager, "AboutCategoryDialog")
    }

    private fun onEditCategory(category: Category){
            InputDialogFragment(
                pTextTitle = "Категория",
                pTextValue = category.categoryName,
                hintText = "Введите название категории",
                listener = object : InputDialogFragment.IEvent{
                    override fun onClose() {

                    }

                    override fun onAccent(textValue: String) {
                        category.categoryName = textValue
                        roomViewModel.updateCategory(category)
                    }
                }
            ).show(requireActivity().supportFragmentManager, "InputDialog")
    }
}