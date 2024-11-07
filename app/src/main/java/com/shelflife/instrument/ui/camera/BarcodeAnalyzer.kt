package com.shelflife.instrument.ui.camera

import android.graphics.RectF
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.graphics.toRectF
import com.google.gson.Gson
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzer
import com.huawei.hms.mlsdk.common.MLFrame
import com.shelflife.instrument.MyApp
import com.shelflife.instrument.Services
import com.shelflife.instrument.util.AndroidSystem

class BarcodeAnalyzer(private val onBarcodeDetected: (String, Boolean, RectF?, Size?) -> Unit) : ImageAnalysis.Analyzer {

    private val barcodeScanner: BarcodeScanner by lazy {
        BarcodeScanning.getClient()
    }

    private val analyzer: HmsScanAnalyzer by lazy {
        HmsScanAnalyzer.Creator(MyApp.appContext).setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create()
    }


    private var stopScan = false

    fun stopAnalyzer(){
        stopScan = true
    }

    fun resumeAnalyzer(){
        stopScan = false
    }

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        if(imageProxy.image!=null){
            when(AndroidSystem.getServices){
                Services.GOOGLE -> runGoogleServiceScanBarcode(imageProxy)
                Services.HUAWEI -> runHuaweiServiceScanBarcode(imageProxy)
                Services.NONE -> {}
            }
        }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun runGoogleServiceScanBarcode(imageProxy: ImageProxy){
        val image = InputImage.fromMediaImage(imageProxy.image, imageProxy.imageInfo.rotationDegrees)
        // Распознавание штрихкодов
        barcodeScanner.process(image)
            .addOnSuccessListener { barcodes ->
                // Обработка распознанных штрихкодов
                if(!stopScan){
                    if(barcodes.isEmpty()){
                        onBarcodeDetected("",false, null,null)
                    }

                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        onBarcodeDetected(
                            rawValue,
                            true,
                            barcode.boundingBox.toRectF(), //координаты найденного ш/к
                            Size(imageProxy.width,
                                imageProxy.height)
                        )
                        break
                    }
                }
            }
            .addOnFailureListener {
                // Обработка ошибок
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    }

    @OptIn(ExperimentalGetImage::class)
    private fun runHuaweiServiceScanBarcode(imageProxy: ImageProxy){
        val frame = MLFrame.fromMediaImage(imageProxy.image, imageProxy.imageInfo.rotationDegrees)
        analyzer.analyzInAsyn(frame)
            .addOnSuccessListener {barcodes->
                if(!stopScan){
                    if(barcodes.isNotEmpty()){
                        println(Gson().toJson(barcodes))
                    }

                    for (barcode in barcodes) {
                        val rawValue = barcode.originalValue // Получаем значение штрихкода
                        onBarcodeDetected(rawValue,true,barcode.borderRect.toRectF(), Size(imageProxy.width,
                            imageProxy.height)
                        )
                        break // Завершаем после обнаружения первого штрихкода
                    }
                }
            }
            .addOnFailureListener {}
            .addOnSuccessListener {
                imageProxy.close()
            }
    }
}