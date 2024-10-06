package com.shelflife.instrument.ui.menu

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shelflife.instrument.BundleVar
import com.shelflife.instrument.MyApp
import com.shelflife.instrument.databinding.FragmentMainBinding
import com.shelflife.instrument.factory.RoomViewModelFactory
import com.shelflife.instrument.model.room.Category
import com.shelflife.instrument.model.room.Product
import com.shelflife.instrument.notify.Notify
import com.shelflife.instrument.notify.OperationReceiver
import com.shelflife.instrument.ui.BaseFragment
import com.shelflife.instrument.ui.MainActivity
import com.shelflife.instrument.ui.adapter.AdapterCategoryMainFragment
import com.shelflife.instrument.ui.adapter.AdapterProduct
import com.shelflife.instrument.ui.custom.TypeMessage
import com.shelflife.instrument.ui.dialogs.AboutProductDialog
import com.shelflife.instrument.util.AnimateView
import com.shelflife.instrument.util.UserScreenManager
import com.shelflife.instrument.viewmodel.RoomViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainFragment : BaseFragment() {

    init {
        MyApp.getComponent().inject(this)
    }

    private var _binding: FragmentMainBinding?=null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: RoomViewModelFactory
    private val roomViewModel: RoomViewModel by viewModels{viewModelFactory}

    @Inject
    lateinit var userScreenManager: UserScreenManager

    private lateinit var adapterProduct: AdapterProduct

    private var listCategories = arrayListOf<Category>()

    private var arrayListProducts = arrayListOf<Product>()

    private lateinit var adapterCategory: AdapterCategoryMainFragment

    private var modeSearch = false
    private var selectedCategory = -1

    private var openCategory: Int? = null
    private var openProductId: Int? = null

    private var isStartedAdapter = false

    private var rootView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openCategory = arguments?.getInt(BundleVar.CategoryID)
        openProductId = arguments?.getInt(BundleVar.ProductID)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootView?.let {
            _binding = FragmentMainBinding.bind(it)
            return it
        }

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        rootView = binding.root

        return rootView!!
    }

    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            try {
                //убираем из списка продукты которые удалены через уведомление
                (intent?.getIntExtra(BundleVar.DeleteProduct, 0))?.let { deleteProductId->
                    removeProductFromList(deleteProductId)
                }
            }catch (_:Exception){}
        }
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(MyApp.appContext)
            .unregisterReceiver(broadcastReceiver)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LocalBroadcastManager.getInstance(MyApp.appContext)
            .registerReceiver(broadcastReceiver, IntentFilter(BundleVar.BroadcastEvent))

        roomViewModel.getAllCategories().distinctUntilChanged().observe(viewLifecycleOwner){
            listCategories = arrayListOf()
            listCategories.add(Category(-1,"Все"))
            listCategories.addAll(1,it)

            adapterCategory = AdapterCategoryMainFragment(requireContext(), listCategories)
            binding.spCategory.adapter = adapterCategory

            binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if(!isStartedAdapter) return
                    val selectedId = (binding.spCategory.selectedItem as Category).id
                    roomViewModel.getProducts(if(selectedId==-1) null else selectedId)
                    openCategory = null

                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

            isStartedAdapter = true

            if(openCategory==null){
                binding.spCategory.setSelection(0)
            }else{
                binding.spCategory.setSelection(adapterCategory.getPositionCategory(openCategory!!))
            }
        }

        binding.tvAddProduct.setOnClickListener {
            AnimateView(it).animateAlpha()
            userScreenManager.openProductFragment(requireActivity() as AppCompatActivity)
        }

        binding.rvProducts.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    binding.tvAddProduct.gone()
                } else if (dy < 0) {
                    binding.tvAddProduct.visible()
                }
            }
        })

        binding.ivSearch.setOnClickListener {
            AnimateView(it).animateAlpha()
            modeSearch = true
            selectedCategory = (binding.spCategory.selectedItem as Category).id
            binding.clMainPanel.gone()
            binding.clSearchPanel.visible()
        }

        binding.tvCancelSearch.setOnClickListener {
            AnimateView(it).animateAlpha()
            modeSearch = false
            binding.clSearchPanel.gone()
            binding.clMainPanel.visible()
            roomViewModel.getProducts(if(selectedCategory==-1) null else selectedCategory)
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val inputText = binding.etSearch.text.toString()
                if(inputText.length<2){
                    return
                }

                CoroutineScope(Dispatchers.Main).launch {
                    roomViewModel.getSearchProducts("%$inputText%")
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()

        lifecycleScope.launch {
            roomViewModel.getSelectedProducts.collect{listProducts->
                arrayListProducts.clear()
                arrayListProducts.addAll(listProducts)


                //Если в уведомлении нажата кнопка Просмотр
                openProductId?.let { id->
                    listProducts.find { it.id==id }?.let {
                        Notify.cancelManager(MyApp.appContext, it.id)
                        adapterCategory.notifyDataSetChanged()
                        showProductDialog(it)
                        openProductId = null
                    }
                }

                adapterProduct = AdapterProduct(arrayListProducts, listCategories, object : AdapterProduct.IEvent{
                    override fun onClick(product: Product) {
                        showProductDialog(product)
                    }
                })

                binding.rvProducts.layoutManager = LinearLayoutManager(requireContext())
                binding.rvProducts.adapter = adapterProduct
            }
        }


    }

    private fun showProductDialog(product: Product){
        val dialog = AboutProductDialog(product, object : AboutProductDialog.IEvent{
            override fun onClose() {

            }

            override fun onDelete() {
                CoroutineScope(Dispatchers.Main).launch {
                    //Получаем выбранную категорию
                    val catId = (binding.spCategory.selectedItem as Category).id.let {
                        if(it==-1) null else it
                    }
                    val result = roomViewModel.deleteProduct(product)
                    if(result>0){
                        if(modeSearch){
                            roomViewModel.getSearchProducts("%${binding.etSearch.text}%")
                        }else{
                            roomViewModel.getProducts(catId)
                        }

                        showSnackBar(binding.root, TypeMessage.MESSAGE, "Запись удалена")
                    }else{
                        showSnackBar(binding.root, TypeMessage.ERROR, "Ошибка при удалении записи")
                    }
                }
            }

            override fun onEdit() {
                userScreenManager.openProductFragment(requireActivity() as MainActivity, product.id)
            }
        })

        CoroutineScope(Dispatchers.Main).launch {
            if(roomViewModel.getSelectedProduct(product.id)==null){
                removeProductFromList(product.id)
                showSnackBar(binding.root, TypeMessage.ERROR, "Товар в базе не найден")
            }else{
                dialog.show(requireActivity().supportFragmentManager,"AboutProductDialog")
            }
        }
    }

    fun removeProductFromList(productId: Int){
        arrayListProducts.removeAll {
            it.id == productId
        }
        adapterProduct.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}