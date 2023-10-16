package com.dh.myapplication.core

import android.graphics.Bitmap
import android.util.Log
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BinaryBitmap
import com.google.zxing.LuminanceSource
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class QrCodeHandler {

    companion object {
        private const val TAG = "QrCodeHandler"
        private const val smallerDimension = 200
    }

    fun generateQrCode(randomString: String): Bitmap {
        // Initializing the QR Encoder with your value to be encoded, type you required and Dimension
        val qrgEncoder = QRGEncoder(randomString, null, QRGContents.Type.TEXT, smallerDimension)
      qrgEncoder.colorWhite = android.R.color.black
        // Initialize the `bitmap` variable
//        qrgEncoder.colorBlack = android.R.color.white

        // Return the `bitmap` variable
        return qrgEncoder.bitmap
    }

    suspend fun QrcodeConverterTwo(bMap: Bitmap): String? {
        return withContext(Dispatchers.IO) {
            try {
                val image = InputImage.fromBitmap(bMap, 0)
                val options = BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                    .build()
                val scanner = BarcodeScanning.getClient(options)

                suspendCoroutine<String?> { continuation ->
                    scanner.process(image)
                        .addOnSuccessListener { barcodes ->
                            val barcode = barcodes.firstOrNull()
                            val rawValue = barcode?.rawValue
                            rawValue?.let {
                                Log.d("Barcode", it)
                                continuation.resume(it)
                            } ?: continuation.resume(null)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("QrcodeConverterTwo", "Error scanning QR code", exception)
                            continuation.resume(null)
                        }
                }

            } catch (e: Exception) {
                Log.e("QrcodeConverterTwo", "Error scanning QR code", e)
                null
            }
        }
    }


}