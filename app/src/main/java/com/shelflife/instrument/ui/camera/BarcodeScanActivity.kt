package com.shelflife.instrument.ui.camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.RectF
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.shelflife.instrument.BundleVar
import com.shelflife.instrument.MyApp
import com.shelflife.instrument.R
import com.shelflife.instrument.databinding.ActivityBarcodeScanBinding
import com.shelflife.instrument.factory.BarcodeScanFactory
import com.shelflife.instrument.ui.custom.BarcodeFocusView
import com.shelflife.instrument.ui.custom.RectangleOverlayView
import com.shelflife.instrument.ui.dialogs.ConfirmDialog
import com.shelflife.instrument.util.AnimateView
import com.shelflife.instrument.util.Permission
import com.shelflife.instrument.util.ScreenUtils
import com.shelflife.instrument.viewmodel.BarcodeScanViewModel
import com.shelflife.instrument.viewmodel.RequestType
import com.shelflife.instrument.viewmodel.SharedViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

class BarcodeScanActivity : AppCompatActivity() {

    private val binding: ActivityBarcodeScanBinding by lazy {
        ActivityBarcodeScanBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var viewModelProvider: BarcodeScanFactory
    private val barcodeScanViewModel: BarcodeScanViewModel by viewModels { viewModelProvider }

    private val sharedViewModel: SharedViewModel by viewModels()

    private lateinit var cameraExecutor: ExecutorService

    private var imageAnalyzer: ImageAnalysis? = null

    private var isAnalyzerActive = false
    private var barcodeAnalyzer: BarcodeAnalyzer? = null

    private val overlayView: RectangleOverlayView by lazy {
        RectangleOverlayView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        MyApp.getComponent().inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setModeFullScreen()
        cameraExecutor = Executors.newSingleThreadExecutor()
        if(Permission.checkRequestPermission(this, android.Manifest.permission.CAMERA)){
            startCamera()
        }else{
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }

        binding.tvCancel.setOnClickListener {
            AnimateView(it).animateAlpha()
            finish()
        }
    }

    @SuppressLint("RepeatOnLifecycleWrongUsage")
    override fun onResume() {
        super.onResume()
        setHoleView(this, binding.barcodeFocusView)

        lifecycleScope.launch {
            delay(300)

            if(binding.root.height.toFloat()/binding.root.width.toFloat() < 1.75F){
                val params = binding.previewView.layoutParams as ViewGroup.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = 0
                binding.previewView.layoutParams = params
            }

            (binding.previewView as ViewGroup).addView(overlayView)
        }

        lifecycleScope.launch {
            barcodeScanViewModel.getBarcodeResult.collect{requestType->
                when(requestType){
                    RequestType.LoadStart -> {}
                    RequestType.LoadStop -> {
                        overlayView.updateRectangle(null)
                    }
                    is RequestType.onError -> {
                        onErrorResult(requestType.message)
                    }
                    is RequestType.onResult -> {
                        val intent = Intent()
                        intent.putExtra(BundleVar.ProductName, requestType.productName)  // Передаёшь строку
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
            }
        }
    }

    private fun onErrorResult(message: String){
        val errorDialog = ConfirmDialog(
            pTitle = "Внимание!",
            pDescription = "$message\nПопробовать снова?",
            pOkButton = "Да",
            pCancelButton = "Нет",
            listener = object : ConfirmDialog.IEvent{
                override fun onClose() {
                    //finish()
                }

                override fun onCancel() {
                    finish()
                }

                override fun onOk() {
                    startImageAnalysis()
                }
            }
        )
        errorDialog.show(supportFragmentManager, "ErrorDialog")
    }

    private var camera: Camera? = null
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var cameraSelector: CameraSelector
    private lateinit var preview: Preview

    @SuppressLint("RestrictedApi")
    private fun startCamera(){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            val resolutionSelector = ResolutionSelector.Builder()
                .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
                .build()

            preview = Preview.Builder()
                .setResolutionSelector(resolutionSelector)
                .build()
                .apply {
                    setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            imageAnalyzer = ImageAnalysis.Builder()
                .setResolutionSelector(resolutionSelector)
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()


            barcodeAnalyzer = BarcodeAnalyzer {result, type, rect, size ->

                runOnUiThread {
                    if(!type){
                        overlayView.updateRectangle(null)
                        return@runOnUiThread
                    }

                    size?.let {

                        val scaleX = overlayView.height.toFloat() / it.width
                        val scaleY = overlayView.width.toFloat() / it.height

                        //строим координаты рамки
                        val transformedRect = RectF(
                            (rect?.left ?: 0f) * scaleX,
                            (rect?.top ?: 0f) * scaleY,
                            (rect?.right ?: 0f) * scaleX,
                            (rect?.bottom ?: 0f) * scaleY
                        )

                        overlayView.updateRectangle(transformedRect)
                    }

                    barcodeScanViewModel.loadBarcodeData(result)
                    stopImageAnalysis()
                }
            }

            barcodeAnalyzer?.let {
                imageAnalyzer?.setAnalyzer(cameraExecutor, it)
            }

            cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
                // Log whether the camera supports flash
                val hasFlash = camera?.cameraInfo?.hasFlashUnit() ?: false
            } catch (exc: Exception) {
                exc.printStackTrace()
            }


        }, ContextCompat.getMainExecutor(this))
    }

    private fun startImageAnalysis() {
        barcodeAnalyzer?.resumeAnalyzer()
    }

    private fun stopImageAnalysis() {
        barcodeAnalyzer?.stopAnalyzer()
        //imageAnalyzer?.clearAnalyzer()
        //cameraProvider.unbind(imageAnalyzer)
    }

    override fun onPause() {
        super.onPause()
        stopImageAnalysis()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()){isGranted->
            if(isGranted){
                startCamera()
            }else{

            }
        }

    private fun setModeFullScreen(){
        try {
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
            WindowCompat.setDecorFitsSystemWindows(window, false)
        }catch (_:Exception){}
    }

    fun setHoleView(activity: Activity, holeView: BarcodeFocusView){
        val indent = ScreenUtils.dimenDpToPxInt(activity, R.dimen.indent_camera_hole)
        val heightScreen = ScreenUtils.getHeightFullscreen(this@BarcodeScanActivity)
        val widthScreen = ScreenUtils.getWidthScreen(this@BarcodeScanActivity)
        val left = indent
        val right = widthScreen - indent

        val width = right - left
        val height = (width.toFloat() / (4f/3f)).toInt()


        val top = heightScreen / 2  - height / 2
        val bottom = heightScreen / 2 + height / 2

        holeView.setHolePosition(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
    }
}