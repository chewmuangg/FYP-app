package com.example.fypapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.fypapp.databinding.ActivityAnalysisBinding
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc
import java.io.InputStream

class AnalysisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalysisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnalysisBinding. inflate(layoutInflater)
        setContentView(binding.root)

        // Get the Uri from the intent
        val imageUri: Uri? = intent.getParcelableExtra("imageUri")

        // Display original image
        val originalImage : ImageView = binding.originalImageView
        originalImage.setImageURI(imageUri)

        // Convert Image URI to Bitmap
        val inputStream: InputStream? = contentResolver.openInputStream(imageUri!!)
        val bitmap: Bitmap? = BitmapFactory.decodeStream(inputStream)

        // Perform Image Processing with OpenCV
        //val processedBitmap: Bitmap? = performImageProcessing(bitmap)
        val processedBitmap: Bitmap? = processImage(bitmap)

        // Display the processed greyscale Bitmap in an ImageView
        val processedImage : ImageView = binding.processedImageView
        processedImage.setImageBitmap(processedBitmap)

        // Load and display the image using Glide
        /*Glide.with(this)
            .load(imageUri)
            .into(analyseImage)
        */

    }

    /* Perform image processing
       To identify wells with coloured solution
       and analyse the intensity of the colour in each well
     */
    private fun processImage(originalBitmap: Bitmap?): Bitmap? {
        // Ensure OpenCV is loaded
        if (!OpenCVLoader.initDebug()) {
            // OpenCV initialisation failed
            Log.d("Check", "OpenCV initialisation failed")
            return originalBitmap
        }

        // Convert Bitmap to Mat
        val originalMat = Mat()
        Utils.bitmapToMat(originalBitmap, originalMat)

        // Convert the image to HSV colour space
        val hsvMat = Mat()
        Imgproc.cvtColor(originalMat, hsvMat, Imgproc.COLOR_RGB2HSV)

        // Define the colour range for the circles
        val lowerThreshold = Scalar(100.0, 50.0, 25.0)
        val upperThreshold = Scalar(155.0, 255.0, 255.0)

        // Threshold the image to get a binary mask
        val thresholdMat = Mat()
        Core.inRange(hsvMat, lowerThreshold, upperThreshold, thresholdMat)

        // Convert the thresholded Mat back to Bitmap
        val resultBitmap = Bitmap.createBitmap(thresholdMat.cols(), thresholdMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(thresholdMat, resultBitmap)

        /*// Find circles using Hough Circle Transform
        val circles = Mat()
        Imgproc.HoughCircles(thresholdMat, circles, Imgproc.CV_HOUGH_GRADIENT, 1.0, 20.0, 100.0, 30.0, 10, 400)

        // Draw circles and number the circles on the image
        val resultMat = originalMat.clone()
        for (i in 0 until circles.cols()) {
            val circle = circles.get(0, i)
            if (circle != null) {
                val center = Point(circle[0], circle[1])
                val radius = circle[2].toInt()

                // Draw a circle on the result image
                Imgproc.circle(resultMat, center, radius, Scalar(0.0, 255.0, 0.0), 4)

                // Draw the circle number
                Imgproc.putText(
                    resultMat,
                    (i + 1).toString(),
                    Point(center.x - 10, center.y - 10),
                    Imgproc.FONT_HERSHEY_SIMPLEX,
                    0.5,
                    Scalar(255.0, 0.0, 0.0),
                    2
                )
            }
        }

        // Convert the resultMat back to bitmap
        val resultBitmap = Bitmap.createBitmap(resultMat.cols(), resultMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(resultMat, resultBitmap)*/

        // Release Mats to free up memory
        originalMat.release()
        hsvMat.release()
        thresholdMat.release()
        circles.release()
        resultMat. release()

        return resultBitmap
    }

    // Perform greyscale conversion using OpenCV
    private fun performImageProcessing(bitmap: Bitmap?): Bitmap? {
        // Ensure OpenCV is loaded
        if (!OpenCVLoader.initDebug()) {
            // OpenCV initialisation failed
            Log.d("Check", "OpenCV initialisation failed")
            return bitmap
        }

        // Convert Bitmap to Mat
        val mat = Mat()
        Utils.bitmapToMat(bitmap, mat)

        // Convert image to greyscale using OpenCV
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2GRAY)

        // Convert Mat back to Bitmap
        Utils.matToBitmap(mat, bitmap)

        return bitmap
    }
}