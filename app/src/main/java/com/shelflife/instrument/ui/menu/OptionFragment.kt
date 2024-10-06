package com.shelflife.instrument.ui.menu

import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.shelflife.instrument.BuildConfig
import com.shelflife.instrument.MyApp
import com.shelflife.instrument.R
import com.shelflife.instrument.databinding.FragmentOptionBinding
import com.shelflife.instrument.factory.OptionsViewModelFactory
import com.shelflife.instrument.model.Options
import com.shelflife.instrument.repository.SharedPreferenceRepository
import com.shelflife.instrument.ui.BaseFragment
import com.shelflife.instrument.ui.MainActivity
import com.shelflife.instrument.ui.custom.TypeMessage
import com.shelflife.instrument.util.AnimateView
import com.shelflife.instrument.util.MyUiElementFormatter
import com.shelflife.instrument.util.Permission
import com.shelflife.instrument.util.UserScreenManager
import com.shelflife.instrument.viewmodel.OptionsViewModel
import javax.inject.Inject

class OptionFragment : BaseFragment() {

    init {
        appComponent.inject(this)
    }

    @Inject
    lateinit var optionsViewModelFactory: OptionsViewModelFactory
    private val optionsViewModel: OptionsViewModel by viewModels {optionsViewModelFactory}

    @Inject
    lateinit var sharedPreference: SharedPreferenceRepository

    @Inject
    lateinit var userScreenManager: UserScreenManager

    private var _binding: FragmentOptionBinding?=null
    private val binding get() = _binding!!

    private lateinit var options: Options

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        options = sharedPreference.getOptions() ?: Options()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOptionBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listOf(binding.cbNotifyPeriod, binding.cbNotifyExpired).forEach { cb->
            MyUiElementFormatter.setCheckboxColor(
                cb,
                ContextCompat.getColor(requireContext(), R.color.radio_checked_color),
                ContextCompat.getColor(requireContext(), R.color.radio_unchecked_color)
            )
        }

        binding.tvSetNotification.isVisible = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            !Permission.checkRequestPermission(requireContext(), android.Manifest.permission.POST_NOTIFICATIONS)

        setDefaultData()
    }

    private fun setDefaultData(){
        options.defaultUntilDays.let {
            binding.cbNotifyPeriod.isChecked = it.isNotEmpty()
            binding.etNotificationPeriod.setText(it)
        }

        binding.cbNotifyExpired.isChecked = options.isNotifyExpired
        binding.tvTime.text = options.timeNotification
    }

    override fun onResume() {
        super.onResume()

        binding.cbNotifyPeriod.setOnCheckedChangeListener { buttonView, isChecked ->
            if(!isChecked){
                binding.etNotificationPeriod.apply {
                    setText("")
                    isEnabled = false
                }
            }else{
                binding.etNotificationPeriod.isEnabled = true
            }
        }

        binding.tvTime.setOnClickListener {
            AnimateView(it).animateAlpha()

            val (hour, minute) = optionsViewModel.getTimeInt(options.timeNotification)

            val dialog = TimePickerDialog(
                requireContext(),
                R.style.CustomTimePickerDialog,
                {_, hourOfDay, minute ->
                    binding.tvTime.text = optionsViewModel.getTimeStr(hourOfDay, minute)
                },
                hour,
                minute,
                true
            )
            dialog.show()
        }

        binding.tvSave.setOnClickListener {
            AnimateView(it).animateAlpha()
            if(binding.cbNotifyPeriod.isChecked && binding.etNotificationPeriod.text.toString().isEmpty()){
                showSnackBar(binding.root, TypeMessage.ERROR, "Не заполнено поле с количеством дней до конца срока годности")
                return@setOnClickListener
            }

            options.defaultUntilDays = binding.etNotificationPeriod.text.toString()
            options.isNotifyExpired = binding.cbNotifyExpired.isChecked
            options.timeNotification = binding.tvTime.text.toString()

            sharedPreference.saveOptions(options)
            MyApp.updateDailyCheck()
            userScreenManager.openMainFragment(requireActivity() as MainActivity)
        }

        binding.tvCancel.setOnClickListener {
            AnimateView(it).animateAlpha()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.tvSetNotification.setOnClickListener {
            AnimateView(it).animateAlpha()
            requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }

        binding.tvSendmail.setOnClickListener {

            AnimateView(it).animateAlpha()
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(BuildConfig.MAIL))
                putExtra(Intent.EXTRA_SUBJECT, "МП: Срок годности")
            }

            try {
                startActivity(Intent.createChooser(emailIntent, "Выберите почтовый клиент"))
            } catch (e: android.content.ActivityNotFoundException) {
                showSnackBar(binding.root, TypeMessage.WARNING, "Нет почтовых клиентов")
            }

        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted: Boolean ->
         if(isGranted){
             binding.tvSetNotification.gone()
         }
    }
}