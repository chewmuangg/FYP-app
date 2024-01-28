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
import org.opencv.core.*
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

        // Bitwise AND operation
//        val resultMat = Mat()
//        Core.bitwise_and(originalMat, originalMat, resultMat, thresholdMat)

        /* Start of contour detection that kinda works */

        // Find contours on the resulting image
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(thresholdMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        // Log the number of contours found
        val numberOfContours = contours.size
        Log.d("ContourDetection", "Number of contours found: $numberOfContours")

        // Iterate through contours and filter by area
        val minContourArea = 10000.0
        val filteredContours = ArrayList<MatOfPoint>()

        for (contour in contours) {
            val contourArea = Imgproc.contourArea(contour)

            if (contourArea > minContourArea) {
                filteredContours.add(contour)
            }
        }

        // Log the number of filtered contours
        val numberOfFilteredContours = filteredContours.size
        Log.d("ContourDetection", "Number of filtered contours: $numberOfFilteredContours")

        // Visualise the filtered contours on the binary image
        val contourImage = Mat.zeros(originalMat.size(), CvType.CV_8UC4)
        Imgproc.drawContours(contourImage, filteredContours, -1, Scalar(0.0, 255.0, 0.0, 255.0), 2)

        // Convert the result Mat back to Bitmap
        val resultBitmap = Bitmap.createBitmap(contourImage.cols(), contourImage.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(contourImage, resultBitmap)

        /* End of contour detection */

        // Convert the thresholded Mat back to Bitmap
//        val resultBitmap = Bitmap.createBitmap(thresholdMat.cols(), thresholdMat.rows(), Bitmap.Config.ARGB_8888)
//        Utils.matToBitmap(thresholdMat, resultBitmap)

        /* Using Hough Circle Transform */
        // Apply Gaussian Blur to reduce noise and improve circle detection
        //Imgproc.GaussianBlur(thresholdMat, thresholdMat, Size(9.0, 9.0), 2.0, 2.0)

        // Find circles using Hough Circle Transform
        /*val circles = Mat()
        Imgproc.HoughCircles(thresholdMat, circles, Imgproc.CV_HOUGH_GRADIENT, 1.0, 20.0, 100.0, 30.0, 5, 100)

        // Log the number of circles found
        val numberOfCircles = circles.cols()
        Log.d("CircleDetection", "Number of circles found: $numberOfCircles")

        // Draw circles on the original image
        val resultMat = originalMat.clone()
        for (i in 0 until numberOfCircles) {
            val circleCenter = Point(circles[0, i][0], circles[0, i][1])
            val radius = circles[0, i][2].toInt()

            Imgproc.circle(resultMat, circleCenter, radius, Scalar(0.0, 255.0, 0.0), 8)
        }

        // Convert the resultMat back to bitmap
        val resultBitmap = Bitmap.createBitmap(resultMat.cols(), resultMat.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(resultMat, resultBitmap)*/

        /*End of Hough Circle Transform*/

        // Release Mats to free up memory
        originalMat.release()
        hsvMat.release()
        thresholdMat.release()
        //circles.release()
        //resultMat. release()

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