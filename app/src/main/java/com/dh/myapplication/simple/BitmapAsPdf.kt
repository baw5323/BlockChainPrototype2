package com.dh.myapplication.simple

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.pdf.PdfDocument
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class BitmapAsPdf {

    companion object {
        private const val TAG = "saveBitmapAsPdf"
    }


    fun saveBitmapAsPdf(context: Context, bitmap: Bitmap, filename: String): Pair<String, Boolean> {
        // Create a new PdfDocument object
        val pdfDocument = PdfDocument()

        // Define the page dimensions of the PDF document based on the size of the Bitmap
        val pageInfo = PdfDocument.PageInfo.Builder(bitmap.width, bitmap.height, 1).create()

        // Create a new page in the PdfDocument
        val page = pdfDocument.startPage(pageInfo)

        // Draw the Bitmap on the page
        val canvas = page.canvas

        val inverse2: Bitmap = invertQRCodeBitmap2(bitmap)
        val fullBitmap = addLayerOnBlack(inverse2)

        canvas.drawBitmap(fullBitmap, 0f, 0f, null)


        // End the page
        pdfDocument.finishPage(page)

        // Get the cache directory
    //    val cacheDir = context.cacheDir

        // Save the PdfDocument as a PDF file in the cache folder
        val pdfFile = File(context.externalCacheDir, "$filename.pdf")

        var isSuccess = false
        try {
            val fileOutputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(fileOutputStream)
            pdfDocument.close()
            fileOutputStream.close()
            isSuccess = true
            // The PDF was successfully saved
        } catch (e: IOException) {
            e.printStackTrace()
            // Error while saving the PDF
        }


        Log.i(TAG, "saveBitmapAsPdf: pdfFile.absolutePath: ${pdfFile.absolutePath}")


        return Pair(pdfFile.absolutePath, isSuccess)
    }

    fun addLayerOnBlack(layerBitmap: Bitmap): Bitmap {
        val width = layerBitmap.width
        val height = layerBitmap.height

        // Create a new mutable bitmap with the same dimensions as the layer bitmap
        val resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        // Create a canvas to draw on the result bitmap
        val canvas = Canvas(resultBitmap)

        // Draw the black background
        canvas.drawColor(Color.BLACK)

        // Draw the layer bitmap on top
        val paint = Paint()
        paint.alpha = 255 // Make sure the layer is fully opaque
        canvas.drawBitmap(layerBitmap, 0f, 0f, paint)

        return resultBitmap
    }


    fun invertQRCodeBitmap2(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        // Create a new mutable bitmap with the same dimensionBitmapAsPDF:s as the reference bitmap
        val invertedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 until width) {
            for (y in 0 until height) {
                // Get the color of the pixel at (x, y) in the reference bitmap
                val pixelColor = bitmap.getPixel(x, y)

                // Invert the color by changing each color component
                val invertedColor = pixelColor xor 0x00ffffff // XOR with 0x00ffffff inverts the color

                // Set the inverted color to the corresponding pixel in the new bitmap
                invertedBitmap.setPixel(x, y, invertedColor)
            }
        }

        return invertedBitmap
    }


}