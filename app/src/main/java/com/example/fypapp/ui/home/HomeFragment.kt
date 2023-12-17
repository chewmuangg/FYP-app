package com.example.fypapp.ui.home

import android.content.Intent
import android.content.pm.PackageManager
import android.Manifest
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.fypapp.R
import com.example.fypapp.SharedViewModel
import com.example.fypapp.databinding.FragmentHomeBinding
import com.example.fypapp.AnalysisActivity
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by viewModels()

    private val CAMERA_PERMISSION_REQUEST_CODE = 1001
    private val GALLERY_PERMISSION_REQUEST_CODE = 1002

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    // Checks if camera permission is enabled
    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) ==  PackageManager.PERMISSION_GRANTED
    }

    // Request for enabling camera permission
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_REQUEST_CODE
        )
    }

    // Checks if gallery permission is enabled
    private fun checkGalleryPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    // Request for enabling gallery permission
    private fun requestGalleryPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            GALLERY_PERMISSION_REQUEST_CODE
        )
    }

    // function to launch camera
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            val imageUri = createImageUri()
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
            startActivityForResult(takePictureIntent, CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    // function to launch gallery
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, GALLERY_PERMISSION_REQUEST_CODE)
    }

    private fun createImageUri(): Uri {
        val imageFile = File(requireContext().filesDir, "camera_photos.jpg")
        return FileProvider.getUriForFile(
            requireContext(),
            "com.example.fypapp.fileprovider",
            imageFile
        )
    }

    // Handle the result of the Camera and Gallery Intents
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_PERMISSION_REQUEST_CODE -> {
                    // Handle the captured image from the camera
                    val imageUri : Uri? = data?.data

                    // Start AnalysisActivity with the image Uri
                    startAnalysisActivity(imageUri)
                }

                GALLERY_PERMISSION_REQUEST_CODE -> {
                    // Handle the selected image from the gallery
                    val imageUri : Uri? = data?.data

                    // Start AnalysisActivity with the image Uri
                    startAnalysisActivity(imageUri)
                }
            }

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

    // Launch Analysis Activity
    private fun startAnalysisActivity(imageUri: Uri?) {
        val intent = Intent(requireContext(), AnalysisActivity::class.java)
        intent.putExtra("imageUri", imageUri)
        startActivity(intent)
    }
}