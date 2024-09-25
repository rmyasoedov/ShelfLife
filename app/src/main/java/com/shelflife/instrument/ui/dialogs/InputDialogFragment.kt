package com.shelflife.instrument.ui.dialogs

import android.os.Bundle
import android.text.InputType
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.shelflife.instrument.databinding.FragmentInputDialogBinding
import com.shelflife.instrument.util.Keyboard

class InputDialogFragment(
    val pTextTitle: String,
    val hintText: String,
    val pTextValue: String?=null,
    val inputType: Int = InputType.TYPE_CLASS_TEXT,
    val listener: IEvent
) : SlideDialogFragment() {

    interface IEvent{
        fun onClose()
        fun onAccent(textValue: String)
    }

    private var _binding: FragmentInputDialogBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInputDialogBinding.inflate(inflater, container, false)
        binding.etText.inputType = inputType
        Keyboard.showKeyboardDelay(binding.etText, requireActivity())
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvTitle.text = pTextTitle
        binding.tvTile.text = hintText

        pTextValue?.let {
            binding.etText.setText(pTextValue)
        }

        listOf(binding.ivClose, binding.clearBlock).forEach {
            it.setOnClickListener {
                onlyClose = true
                Keyboard.hideKeyboard(requireActivity())
                dismiss()
            }
        }

        binding.etText.addTextChangedListener {
            binding.ivClear.visibility =
            if(binding.etText.text.toString().isEmpty()){
                View.INVISIBLE
            }else{
                View.VISIBLE
            }
            binding.tvErrorCode.visibility = View.GONE
        }

        binding.ivClear.setOnClickListener {
            binding.etText.setText("")
        }

        binding.etText.setOnKeyListener { v, keyCode, event ->
            var flag = true
            if (event?.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                val textValue = binding.etText.text.toString()
                if(textValue.isEmpty()){
                    binding.tvErrorCode.visibility = View.VISIBLE
                }else{
                    listener.onAccent(textValue)
                    Keyboard.hideKeyboard(requireActivity())
                    dismiss()
                }
            }else{
                flag = false
            }
            flag
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