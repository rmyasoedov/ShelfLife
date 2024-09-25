package com.shelflife.instrument.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.shelflife.instrument.R
import com.shelflife.instrument.databinding.AboutProductDialogBinding
import com.shelflife.instrument.model.room.Product
import com.shelflife.instrument.util.AnimateView
import com.shelflife.instrument.util.MyDateFormatter

class AboutProductDialog(
    private val product: Product,
    private val listener: IEvent
) : SlideDialogFragment() {

    interface IEvent{
        fun onClose()
        fun onDelete()
        fun onEdit()
    }

    private var _binding: AboutProductDialogBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = AboutProductDialogBinding.inflate(inflater, container, false)

        showView(binding.infoBlock,binding.parentBlock)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            tvTitle.text = product.productName

            if(product.dateStart.isEmpty()){
                llDateStart.visibility = View.GONE
            }else{
                tvDateStart.text = MyDateFormatter.convertFromDateFormat(product.dateStart)
            }

            tvDateEnd.text = MyDateFormatter.convertFromDateFormat(product.dateEnd)
            val (years, months, days) = MyDateFormatter.calculateTimeUntil(product.dateEnd)

            if(years==0 && months==0 && days==0){
                tvUntilDays.setTextColor(ContextCompat.getColor(requireContext(), R.color.sb_error_color))
                tvUntilDays.text = "Просрочен"
            }else{
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

            clEdit.setOnClickListener {
                AnimateView(it).animateAlpha()
                listener.onEdit()
                dismiss()
            }

            clDelete.setOnClickListener {
                AnimateView(it).animateAlpha()
                listener.onDelete()
                dismiss()
            }
        }


        listOf(binding.ivClose, binding.clearBlock).forEach {
            it.setOnClickListener {
                onlyClose = true
                closeView()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if(onlyClose){
            listener.onClose()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}