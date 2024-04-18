package com.example.fypapp.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.fypapp.MedifyApplication
import com.example.fypapp.SharedViewModel
import com.example.fypapp.databinding.FragmentHomeBinding
import com.example.fypapp.R
import com.example.fypapp.SharedViewModelFactory
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (requireActivity().application as MedifyApplication).gResultRepository,
            (requireActivity().application as MedifyApplication).thresholdValueRepository
        )
    }

    // Request codes for camera and gallery permissions
    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val GALLERY_PERMISSION_REQUEST_CODE = 1002

    // URI to store the current photo captured by the camera
    private var currentPhotoUri: Uri? = null

    // Current captured image filename
    private lateinit var imageFileName: String

    // ActivityResultLauncher for handling camera intent result
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the captured image from the camera
                // Save the image to external storage before navigating to the Analysis Fragment
                saveImageToStorage(currentPhotoUri)

                // Set the image URI to the SharedViewModel
                currentPhotoUri?.let { sharedViewModel.setImageUri(it) }

                // Navigate to Analysis Fragment Page
                navigateToAnalysisFragment()
            }
        }

    // ActivityResultLauncher for handling gallery intent result
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Handle the selected image from the gallery
                val imageUri: Uri? = result.data?.data

                // Set the image URI to the SharedViewModel
                imageUri?.let { sharedViewModel.setImageUri(it) }

                // Navigate to Analysis Fragment Page
                navigateToAnalysisFragment()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: update this
        // set isRed to null
        //sharedViewModel.setIsRed(null)

        // Camera Button
        val cameraBtn : ImageButton = binding.cameraButton
        cameraBtn.setOnClickListener {
            // check if camera permission is already enabled
            if (checkCameraPermission()) {
                // camera permission enabled
                // open camera app to take a picture
                openCamera()
            } else {
                // camera permission not enabled,
                // request for permission
                requestCameraPermission()
            }
        }

        // Gallery Button
        val galleryBtn : ImageButton = binding.galleryButton
        galleryBtn.setOnClickListener {
            // check if gallery permission is already enabled
            if (checkGalleryPermission()) {
                // gallery permission enabled,
                // open gallery to select a picture
                openGallery()
            } else {
                // gallery permission not enabled,
                // request for permission
                requestGalleryPermission()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Checks if camera permission is granted
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) ==  PackageManager.PERMISSION_GRANTED
    }

    // Request camera permission
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    // Checks if gallery permission is granted
    private fun checkGalleryPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request gallery permission
    private fun requestGalleryPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            GALLERY_PERMISSION_REQUEST_CODE
        )
    }

    // Function to launch camera intent
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val photoFile: File? = createImageFile()
        photoFile?.let {
            currentPhotoUri = FileProvider.getUriForFile(
                requireContext(),
                "com.example.fypapp.fileprovider",
                it
            )
            // Save the captured image
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)

            // Launch the camera intent using the ActivityResultLauncher
            cameraLauncher.launch(takePictureIntent)
        }
    }

    // Function to launch gallery intent
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"

        // Launch the gallery intent using the ActivityResultLauncher
        galleryLauncher.launch(galleryIntent)
    }

    // Create a temporary image file
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        imageFileName = "FYP_img_${timeStamp}_"
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

    // Save image file to external storage (gallery)
    private fun saveImageToStorage(imageUri: Uri?) {
        try {
            imageUri?.let {
                // Open an input stream to read from the content URI
                val inputStream: InputStream? = requireContext().contentResolver.openInputStream(it)

                // Prepare the ContentValues to insert the image into MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.Images.Media.DISPLAY_NAME, "$imageFileName.jpg")
                    put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                }

                // Use the MediaStore.Images.Media content URI to insert the image
                val contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                val resolver = requireContext().contentResolver
                val insertUri = resolver.insert(contentUri, contentValues)

                // Open an output stream to write to the content URI
                insertUri?.let { uri->
                    resolver.openOutputStream(uri)?.use { outputStream ->
                        inputStream?.copyTo(outputStream)
                    }
                }

                // Close the input stream
                inputStream?.close()

            }
        } catch (e: IOException) {
            e.printStackTrace()
            // Handle the exception as needed
        }
    }

    /* Handle the results of the permission requests.
     * If granted, calls the corresponding method openCamera() or openGallery().
     * If denied, do nothing.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            // Handle Camera permission request result
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted for camera
                    openCamera()
                }
            }

            // Handle Gallery permission request result
            GALLERY_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission granted for gallery
                    openGallery()
                }
            }
        }
    }

    // Navigate from Home Fragment to Analysis Fragment
    private fun navigateToAnalysisFragment() {
        findNavController().navigate(R.id.action_navigation_home_to_analysisFragment)
    }

}