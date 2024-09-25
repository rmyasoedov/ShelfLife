package com.shelflife.instrument.ui.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shelflife.instrument.databinding.ConfirmDialogBinding
import com.shelflife.instrument.util.AnimateView

class ConfirmDialog(
    val pTitle: String,
    val pDescription: String,
    val pOkButton: String,
    val pCancelButton: String?=null,
    val listener: IEvent
) : SlideDialogFragment() {

    interface IEvent{
        fun onClose()
        fun onCancel()
        fun onOk()
    }

    private var _binding: ConfirmDialogBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ConfirmDialogBinding.inflate(inflater, container, false)

        binding.tvTitle.text = pTitle
        binding.tvDescription.text = pDescription
        binding.tvOk.text = pOkButton

        binding.tvCancel.visibility = if(pCancelButton==null){
            View.GONE
        }else{
            binding.tvCancel.text = pCancelButton
            View.VISIBLE
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvOk.setOnClickListener {
            AnimateView(it).animateAlpha()
            listener.onOk()
            dismiss()
        }

        binding.tvCancel.setOnClickListener {
            AnimateView(it).animateAlpha()
            listener.onCancel()
            dismiss()
        }

        binding.clearBlock.setOnClickListener {
            onlyClose = true
            dismiss()
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