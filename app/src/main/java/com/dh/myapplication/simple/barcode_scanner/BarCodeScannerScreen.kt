package com.dh.myapplication.simple.barcode_scanner

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.centri.mypropane.utils.findActivity
import com.dh.myapplication.simple.simpleViewModel
import com.google.zxing.client.android.BeepManager
import com.journeyapps.barcodescanner.CompoundBarcodeView


// Composable function for the entire Barcode Scanner screen
@Composable
fun BarCodeScannerScreenCamera(UserViewModel: simpleViewModel, nav: () -> Unit) {

// Place the ScannerView inside a Box to position it and manage its layout
    Box(Modifier.fillMaxWidth()) {
        ScannerView() {
            // Call the scan function in the ViewModel with the scanned result
            UserViewModel.scan(it)
            // Invoke the provided nav lambda to navigate after scanning
            nav.invoke()

        }
    }


}

@Composable
private fun ScannerView(onScan: (String) -> Unit) {
    val context = LocalContext.current
    // Initialize a BeepManager to control beep and vibrate effects
    val beepManager: BeepManager by remember { mutableStateOf(BeepManager(context.findActivity()).apply {
        isBeepEnabled = true
        isVibrateEnabled = true
    }) }


    // Create and configure the BarcodeView
    val barcodeView = remember {
        // Initialize the view with the intent from the current activity
        CompoundBarcodeView(context).apply {
            this.initializeFromIntent((context as Activity).intent)
            this.setStatusText("")
            this.decodeSingle { result ->
                result.text?.let { barCodeOrQr ->
                    // When a barcode is decoded, invoke the provided onScan lambda
                    onScan.invoke(barCodeOrQr)
                    // Play beep sound using the BeepManager
                    beepManager.playBeepSound()
                }
            }

        }
    }
    // Use AndroidView to display the BarcodeView
    AndroidView(factory = { barcodeView })
    // Register lifecycle callbacks to manage the BarcodeView's lifecycle
    DisposableLifecycleCallbacks(
        onResume = barcodeView::resume,
        onPause = barcodeView::pause
    )
}

