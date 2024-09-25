package com.shelflife.instrument.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.shelflife.instrument.MyApp
import com.shelflife.instrument.databinding.AboutCategoryDialogBinding
import com.shelflife.instrument.factory.RoomViewModelFactory
import com.shelflife.instrument.model.room.Category
import com.shelflife.instrument.util.AnimateView
import com.shelflife.instrument.viewmodel.RoomViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

class AboutCategoryDialog(val category: Category, val listener: IEvent) : SlideDialogFragment() {

    init {
        MyApp.getComponent().inject(this)
    }

    @Inject
    lateinit var viewModelFactory: RoomViewModelFactory
    private val roomViewModel: RoomViewModel by viewModels{viewModelFactory}

    private var waitingDelete = false

    interface IEvent{
        fun onDelete(waitingDelete: Boolean)
        fun onEdit()
        fun onOpenProducts()
    }

    private var _binding: AboutCategoryDialogBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AboutCategoryDialogBinding.inflate(inflater, container, false)

        showView(binding.infoBlock,binding.parentBlock)

        CoroutineScope(Dispatchers.Main).launch {
            val data = roomViewModel.getCategoryData(category.id)
            waitingDelete = data.totalCount>0
            binding.tvQuantityProduct.text = data.totalCount.toString()
            binding.tvExpired.text = data.totalExpired.toString()
        }


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            tvTitle.text = category.categoryName

            clOpenList.setOnClickListener {
                AnimateView(it).animateAlpha()
                listener.onOpenProducts()
                dismiss()
            }

            clEdit.setOnClickListener {
                AnimateView(it).animateAlpha()
                listener.onEdit()
                dismiss()
            }

            clDelete.setOnClickListener {
                AnimateView(it).animateAlpha()
                listener.onDelete(waitingDelete)
                dismiss()
            }

            listOf(ivClose, clearBlock).forEach {
                it.setOnClickListener {
                    onlyClose = true
                    closeView()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}