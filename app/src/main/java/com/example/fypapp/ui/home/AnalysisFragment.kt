package com.example.fypapp.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fypapp.R
import com.example.fypapp.SharedViewModel
import com.example.fypapp.databinding.FragmentAnalysisBinding
import org.opencv.android.OpenCVLoader
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.imgproc.Imgproc
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

class AnalysisFragment : Fragment() {

    private var _binding: FragmentAnalysisBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Declare variables
    //private var imageUri: Uri? = null
    private var bitmap: Bitmap? = null
    private lateinit var lowerThresholdValue: Scalar
    private lateinit var upperThresholdValue: Scalar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Assign the ImageView instance from the layout binding to the selectedImage variable
        val selectedImage : ImageView = binding.selectedImage

        // Observe the image URI Livedata from the SharedViewModel
        sharedViewModel.imageUri.observe(viewLifecycleOwner, Observer { uri ->
            // Display the image in ImageView using the imageUri
            selectedImage.setImageURI(uri)

            // Convert Image URI to Bitmap
            try {
                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(uri!!)
                bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                // Handle file not found exception
            } catch (e: IOException) {
                e.printStackTrace()
                // Handle IO exception
            }
        })

        // Perform Image Processing with OpenCV upon clicking onto the "Process" Button
        val processButton : Button = binding.processBtn
        processButton.setOnClickListener {
            // Call the Image Processing function
            val result = processImage(bitmap, lowerThresholdValue, upperThresholdValue)

            // Retrieve the processed bitmap and list of intensity values
            val processedBitmap: Bitmap? = result.first
            val meanIntensityList = result.second

            // Display the processed image with contour drawings in an ImageView
            selectedImage.setImageBitmap(processedBitmap)

            // Display the list of Intensity Values
            val listText : TextView = binding.listText
            val formattedText = meanIntensityList.joinToString(", ")
            listText.text = formattedText
        }

        // Navigate to the Results page
        val nextBtn: Button = binding.nextButton
        nextBtn.setOnClickListener {
            findNavController().navigate(R.id.action_analysisFragment_to_resultsFragment)
        }
    }

    override fun onResume() {
        super.onResume()

        // Setup adapter for Colour option dropdown menu
        val colours = resources.getStringArray(R.array.dyeColours)
        val arrayAdapter = ArrayAdapter(requireContext(), R.layout.colour_dropdown_item, colours)
        val colourDropdown = binding.autoCompleteTextView
        colourDropdown.setAdapter(arrayAdapter)

        // Setup the respective actions for each option selected
        colourDropdown.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            when (position) {
                0 -> {
                    // Option: Orange (0.1 – 10 mM)
                    lowerThresholdValue = Scalar(0.0, 100.0, 60.0)
                    upperThresholdValue = Scalar(20.0, 255.0, 255.0)
                }

                1 -> {
                    // Option: Orange (0.025 – 0.5 mM)
                    lowerThresholdValue = Scalar(0.0, 100.0, 60.0)
                    upperThresholdValue = Scalar(20.0, 255.0, 255.0)
                }

                2 -> {
                    // Option: Yellow (0.1 – 10 mM)
                    lowerThresholdValue = Scalar(20.0, 75.0, 25.0)
                    upperThresholdValue = Scalar(35.0, 255.0, 255.0)
                }

                3 -> {
                    // Option: Yellow (0.025 – 0.5 mM)
                    lowerThresholdValue = Scalar(20.0, 75.0, 25.0)
                    upperThresholdValue = Scalar(35.0, 255.0, 255.0)
                }

                4 -> {
                    // Option: Others
                }

                else -> {
                    // Handle other clicks

                }

            }
            // Handle item selection here
            val selectedItem = parent.getItemAtPosition(position).toString()
            Toast.makeText(requireContext(), "dye colour: " + selectedItem, Toast.LENGTH_SHORT).show()
        }

    }

    /* Perform image processing on the captured/selected image
       Step 1: To detect the wells with coloured solution that are within the specified threshold
       Step 2: Draw contour (outline) the detected Regions of Interests (ROIs)
       Step 3: Calculate the average intensity within the ROIs
     */
    private fun processImage(
        originalBitmap: Bitmap?,
        lowerThresholdValue: Scalar,
        upperThresholdValue: Scalar
    ): Pair<Bitmap?, List<Double>> {
        // Ensure OpenCV is loaded
        if (!OpenCVLoader.initDebug()) {
            // OpenCV initialisation failed
            Log.d("Check", "OpenCV initialisation failed")
            return Pair(originalBitmap, emptyList())
        }

        // Convert Bitmap to Mat
        val originalMat = Mat()
        Utils.bitmapToMat(originalBitmap, originalMat)

        // Convert the image to HSV colour space
        val hsvMat = Mat()
        Imgproc.cvtColor(originalMat, hsvMat, Imgproc.COLOR_RGB2HSV)

        // Define the colour range for the circles
        // multi-colour, Blue, purple, pink gradient
//        val lowerThreshold = Scalar(100.0, 50.0, 25.0)
//        val upperThreshold = Scalar(155.0, 255.0, 255.0)

        // Define the colour range for the circles
        // (red) orange gradient
//        val lowerThreshold = Scalar(0.0, 100.0, 60.0)
//        val upperThreshold = Scalar(20.0, 255.0, 255.0)

        // Define the colour range for the circles
        // yellow gradient
//        val lowerThreshold = Scalar(20.0, 75.0, 25.0)
//        val upperThreshold = Scalar(35.0, 255.0, 255.0)

        // Get the respective lower & upper threshold value for the selected dye colour
        val lowerThreshold = lowerThresholdValue
        val upperThreshold = upperThresholdValue

        // Threshold the image to get a binary mask
        val thresholdMat = Mat()
        Core.inRange(hsvMat, lowerThreshold, upperThreshold, thresholdMat)

        /* Start of contour detection that kinda works */

        // Find contours on the resulting image
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(thresholdMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        // Log the number of contours found
        val numberOfContours = contours.size
        Log.d("ContourDetection", "Number of contours found: $numberOfContours")

        // Convert the original image to greyscale for obtaining colour intensity
        val greyMat = Mat()
        Imgproc.cvtColor(originalMat, greyMat, Imgproc.COLOR_RGB2GRAY)

        // To store the values of the colour intensity of the ROIs in a list
        val intensityValues = mutableListOf<Double>()

        // Create a black image with the same size as the original image for the contour binary mask later on
        val mask = Mat.zeros(originalMat.size(), CvType.CV_8U)

        // Iterate through contours and filter by area
        val minContourArea = 8000.0
        val filteredContours = ArrayList<MatOfPoint>()

        for (contour in contours) {
            // Calculate the area of each contour detected
            val contourArea = Imgproc.contourArea(contour)

            // ROIs have area > minContourArea
            if (contourArea > minContourArea) {
                filteredContours.add(contour)

                // Create a binary mask where regions inside the contour is filled with white (255) and outside remain black (0)
                Imgproc.drawContours(mask, listOf(contour), -1, Scalar(255.0), -1)

                // Calculate the average colour intensity within the contour in the greyscale image
                val meanIntensity = Core.mean(greyMat, mask).`val`[0]

                // Round the calculated value to 5d.p.
                val roundedIntensity = String.format("%.5f", meanIntensity).toDouble()

                intensityValues.add(roundedIntensity)
            }
        }

        // Log the number of filtered contours
        val numberOfFilteredContours = filteredContours.size
        Log.d("ContourDetection", "Number of filtered contours: $numberOfFilteredContours")

        // Bitwise AND operation between the original image (Mat) and the binary mask
//        val contourImage = Mat()
//        Core.bitwise_and(originalMat, originalMat, contourImage, mask)

        // Visualise and numbering the filtered contours on the binary image
        val contourImage = originalMat.clone()
        var contourNum = 1
        for (contour in filteredContours) {
            // Draw the contour
            Imgproc.drawContours(contourImage, filteredContours, -1, Scalar(0.0, 255.0, 0.0, 255.0), 4)

            // Number the contour
            val contourCenter = getContourCenter(contour)
            Imgproc.putText(
                contourImage,
                "#${contourNum++}",
                contourCenter,
                Imgproc.FONT_HERSHEY_SIMPLEX,
                3.0,    // Font scale
                Scalar(200.0, 0.0, 0.0, 255.0), // Font colour (Red in RGB format)
                3,      // Font thickness
                Imgproc.LINE_AA
            )
        }

        /* End of contour detection */

        // Convert the result Mat back to Bitmap
        val resultBitmap = Bitmap.createBitmap(contourImage.cols(), contourImage.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(contourImage, resultBitmap)

        // Convert the thresholded Mat back to Bitmap
//        val resultBitmap = Bitmap.createBitmap(thresholdMat.cols(), thresholdMat.rows(), Bitmap.Config.ARGB_8888)
//        Utils.matToBitmap(thresholdMat, resultBitmap)

        // Release Mats to free up memory
        originalMat.release()
        hsvMat.release()
        thresholdMat.release()
        greyMat.release()

        return Pair(resultBitmap, intensityValues)
    }

    // Function to calculate the center of the contour
    private fun getContourCenter(contour: MatOfPoint): Point {
        val moments = Imgproc.moments(contour)
        val centerX = moments.m10 / moments.m00
        val centerY = moments.m01 / moments.m00
        return Point(centerX, centerY)
    }
}