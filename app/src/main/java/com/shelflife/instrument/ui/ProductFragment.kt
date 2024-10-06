package com.shelflife.instrument.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.shelflife.instrument.BundleVar
import com.shelflife.instrument.MyApp
import com.shelflife.instrument.MyConst
import com.shelflife.instrument.R
import com.shelflife.instrument.databinding.FragmentMainBinding
import com.shelflife.instrument.databinding.FragmentProductBinding
import com.shelflife.instrument.factory.ProductViewModelFactory
import com.shelflife.instrument.factory.RoomViewModelFactory
import com.shelflife.instrument.factory.SharedViewModelFactory
import com.shelflife.instrument.model.room.Category
import com.shelflife.instrument.model.room.Product
import com.shelflife.instrument.ui.adapter.AdapterCategorySpinner
import com.shelflife.instrument.ui.camera.BarcodeScanActivity
import com.shelflife.instrument.ui.custom.CustomSnackBar
import com.shelflife.instrument.ui.custom.TypeMessage
import com.shelflife.instrument.ui.custom.maskedEditText.MaskedEditText
import com.shelflife.instrument.ui.dialogs.InputDialogFragment
import com.shelflife.instrument.util.AnimateView
import com.shelflife.instrument.util.MyDateFormatter
import com.shelflife.instrument.util.MyUiElementFormatter
import com.shelflife.instrument.util.UserScreenManager
import com.shelflife.instrument.viewmodel.ProductViewModel
import com.shelflife.instrument.viewmodel.RoomViewModel
import com.shelflife.instrument.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

class ProductFragment : BaseFragment() {


    init {
        appComponent.inject(this)
    }

    private lateinit var _binding: FragmentProductBinding
    private val binding get() = _binding

    @Inject
    lateinit var productViewModelFactory: ProductViewModelFactory
    private val productViewModel: ProductViewModel by viewModels { productViewModelFactory }

    @Inject
    lateinit var userScreenManager: UserScreenManager

    @Inject
    lateinit var viewModelFactory: RoomViewModelFactory
    private val roomViewModel: RoomViewModel by viewModels{viewModelFactory}

    private val sharedViewModel: SharedViewModel by activityViewModels()

    enum class LongPeriod{
        DAYS, MONTHS
    }

    private var isStarted = false

    private var selectedLongPeriod: LongPeriod = LongPeriod.MONTHS
    private var newIdCategory: Int?=null

    private var productId: Int? = null
    private var updatedProduct: Product? = null
    private lateinit var adapterCategory: AdapterCategorySpinner

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        productId = arguments?.getInt(BundleVar.ProductID)

        listOf(binding.rbDate, binding.rbExpired).forEach {rb->
            MyUiElementFormatter.setRadioButtonColor(
                rb,
                ContextCompat.getColor(requireContext(), R.color.radio_unchecked_color),
                ContextCompat.getColor(requireContext(), R.color.radio_checked_color)
            )
        }

        listOf(binding.cbNotifyPeriod, binding.cbNotifyExpired).forEach { cb->
            MyUiElementFormatter.setCheckboxColor(
                cb,
                ContextCompat.getColor(requireContext(), R.color.radio_checked_color),
                ContextCompat.getColor(requireContext(), R.color.radio_unchecked_color)
            )
        }

        binding.ivScan.isVisible = MyConst.USE_BARCODE_FINDER

        if(productId==null){
            productViewModel.getDefaultOptions()
        }else{
            writeFields()
        }
        return binding.root
    }

    //Если режим редактирования -- заполняем поля из БД
    private fun writeFields(){
        productId?.let { id->
            binding.tvTitle.text = "Редактирование"
            CoroutineScope(Dispatchers.Main).launch {
                val product = roomViewModel.getSelectedProduct(id)
                binding.etProduct.setText(product.productName)

                if(product.dateStart.isNotEmpty()){
                    val date = MyDateFormatter.convertFromDateFormat(product.dateStart)
                    binding.maskStart.setText(date)
                }

                if(product.dateEnd.isNotEmpty()){
                    val date = MyDateFormatter.convertFromDateFormat(product.dateEnd)
                    binding.maskEnd.setText(date)
                }

                binding.spCategory.setSelection(
                    adapterCategory.getPositionIdCategory(product.categoryId)
                )

                product.notificationPeriod.let {
                    if(it.isNotEmpty()){
                        binding.cbNotifyPeriod.isChecked = true
                        binding.etNotificationPeriod.setText(it)
                    }else{
                        binding.cbNotifyPeriod.isChecked = false
                    }
                }
                binding.cbNotifyExpired.isChecked = product.notificationExpired
                updatedProduct = product
            }
        }
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.getStringExtra(BundleVar.ProductName)?.let { productName->
                binding.etProduct.setText(productName)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if(isStarted){
            return
        }
        isStarted = true

        productViewModel.getOptions.observe(viewLifecycleOwner){options->
            if(options.defaultUntilDays.isNotEmpty()){
                binding.cbNotifyPeriod.isChecked = true
                binding.etNotificationPeriod.setText(options.defaultUntilDays)
            }else{
                binding.cbNotifyPeriod.isChecked = false
            }
            binding.cbNotifyExpired.isChecked = options.isNotifyExpired
        }

        roomViewModel.getAllCategories().observe(this){
            val listCategories = arrayListOf<Category>()

            listCategories.addAll(it)
            listCategories.add(
                Category(-1, "<Добавить категорию>")
            )

            adapterCategory = AdapterCategorySpinner(requireContext(), listCategories)
            binding.spCategory.adapter = adapterCategory

            newIdCategory?.let {id->
                val index = listCategories.indexOfFirst { it.id==id }
                binding.spCategory.setSelection(index)
            }

            //слушатель выбора категории товара
            binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val selectedId = (binding.spCategory.selectedItem as Category).id
                    //если выбран пункт добавления категории
                    if(selectedId==-1){
                        InputDialogFragment(
                            pTextTitle = "Категория",
                            hintText = "Введите название категории",
                            listener = object : InputDialogFragment.IEvent{
                                override fun onClose() {
                                    binding.spCategory.setSelection(0)
                                }

                                override fun onAccent(textValue: String) {
                                    val newCategory = Category(categoryName = textValue)
                                    CoroutineScope(Dispatchers.Main).launch {
                                        newIdCategory = roomViewModel.insertCategory(newCategory).toInt()
                                    }
                                }
                            }
                        ).show(requireActivity().supportFragmentManager, "InputCategory")
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        }

        //Отображаем сообщение об ошибке
        productViewModel.showMessageError.observe(this){message->
            showSnackBar(binding.root, TypeMessage.ERROR, message)
        }

        //Кнопка "Сохранить"
        binding.tvSave.setOnClickListener {
            AnimateView(it).animateAlpha()
            val categoryId = (binding.spCategory.selectedItem as Category).id
            val productName = binding.etProduct.text.toString()
            var dateStart = MyDateFormatter.convertDateFormat(binding.maskStart.text.toString())
            var dateEnd = MyDateFormatter.convertDateFormat(binding.maskEnd.text.toString())
            var shelfLife = binding.etLongPeriod.text.toString()
            val notificationPeriod =
                if(binding.cbNotifyPeriod.isChecked) binding.etNotificationPeriod.text.toString() else ""
            val notificationExpired = binding.cbNotifyExpired.isChecked


            if(productName.isBlank()){
                productViewModel.showSnackBarError("Не заполнено наименование продукта")
                return@setOnClickListener
            }

            when(binding.rgButtons.checkedRadioButtonId){
                R.id.rbDate->{
                    shelfLife = ""
                    if(dateEnd.isEmpty()){
                        productViewModel.showSnackBarError("Дата окончания срока хранения заполнена некорректно")
                        return@setOnClickListener
                    }
                }
                R.id.rbExpired->{
                    //dateEnd = ""
                    if(dateStart.isEmpty()){
                        productViewModel.showSnackBarError("Дата изготовления заполнена некорректно")
                        return@setOnClickListener
                    }
                    if(shelfLife.toIntOrNull()==null){
                        productViewModel.showSnackBarError("Срок заполнен некорректно")
                        return@setOnClickListener
                    }else{
                        val (error, resultStr) =
                            productViewModel.calculateDateEnd(dateStart, shelfLife.toInt(), selectedLongPeriod)
                        if(error){
                            productViewModel.showSnackBarError(resultStr)
                            return@setOnClickListener
                        }else{
                            dateEnd = resultStr
                        }
                    }
                }
            }

            if(binding.cbNotifyPeriod.isChecked && binding.etNotificationPeriod.text.toString().isBlank()){
                productViewModel.showSnackBarError("Не заполнен срок уведомления")
                return@setOnClickListener
            }

            val product = Product(
                productName = productName,
                categoryId = categoryId,
                dateStart = dateStart,
                dateEnd = dateEnd,
                shelfLife = shelfLife,
                notificationPeriod = notificationPeriod,
                notificationExpired = notificationExpired
            )

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    roomViewModel.saveProduct(product, updatedProduct)
                    sharedViewModel.showSnackBar("Сохранено")
                    userScreenManager.openMainFragment(requireActivity() as AppCompatActivity)
                }catch (e: Exception){
                    productViewModel.showSnackBarError("Ошибка при сохранении")
                }
            }

        }

        binding.tvCancel.setOnClickListener {
            AnimateView(it).animateAlpha()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.rgButtons.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.rbDate -> {
                    setModeDate()
                }

                R.id.rbExpired -> {
                    setModeExpired()
                }
            }
        }
        binding.rbDate.isChecked = true

        binding.tvInactivePeriod.setOnClickListener {
            selectedLongPeriod = if(selectedLongPeriod==LongPeriod.DAYS){
                LongPeriod.MONTHS
            }else{
                LongPeriod.DAYS
            }

            when(selectedLongPeriod){
                LongPeriod.DAYS->{
                    binding.tvActivePeriod.text = "Дней"
                    binding.tvInactivePeriod.text = "Месяцев"
                }
                LongPeriod.MONTHS->{
                    binding.tvActivePeriod.text = "Месяцев"
                    binding.tvInactivePeriod.text = "Дней"
                }
            }
        }

        binding.ivCalendarStart.setOnClickListener {
            showDatePicker(binding.maskStart)
        }

        binding.ivCalendarEnd.setOnClickListener {
            showDatePicker(binding.maskEnd)
        }

        binding.ivScan.setOnClickListener {
            AnimateView(it).animateAlpha()
            val intent = Intent(requireContext(), BarcodeScanActivity::class.java)
            getResult.launch(intent)
        }
    }

    private fun showDatePicker(maskDate: MaskedEditText){
        // Получаем текущую дату
        val dateString = maskDate.text.toString()
        val (day, month, year) = MyDateFormatter.parseDateOrDefault(dateString)

        // Создаем DatePickerDialog
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, selectedYear, selectedMonth, selectedDay ->
                // Форматируем день и месяц с ведущими нулями
                val formattedDay = String.format("%02d", selectedDay)
                val formattedMonth = String.format("%02d", selectedMonth + 1)

                // Создаем строку даты в формате DD/MM/YYYY
                val selectedDate = "$formattedDay$formattedMonth$selectedYear"
                maskDate.setText(selectedDate)
            },
            year, month, day
        )

        // Показываем диалог
        datePickerDialog.show()
    }

    private fun setModeDate(){
        binding.tvDateStar.invisible()
        binding.llLongPeriod.gone()
        binding.llBlockEndDate.visible()
    }

    private fun setModeExpired(){
        binding.tvDateStar.visible()
        binding.llLongPeriod.visible()
        binding.llBlockEndDate.gone()
    }
}