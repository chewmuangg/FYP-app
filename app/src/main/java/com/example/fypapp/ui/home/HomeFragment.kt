package com.example.fypapp.ui.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.fypapp.R
import com.example.fypapp.SharedViewModel
import com.example.fypapp.databinding.FragmentHomeBinding
import java.io.File

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by viewModels()
    private lateinit var navController: NavController
    val REQUEST_IMAGE_CAPTURE = 100

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

        // Initialise NavController
        navController = Navigation.findNavController(view)

        // Camera Button
        val cameraBtn : ImageButton = binding.cameraButton
        cameraBtn.setOnClickListener {
            Toast.makeText(requireContext(), "clicked!", Toast.LENGTH_SHORT).show()
            // need to request permission?
            openCamera()
        }

        // Gallery Button
        val galleryBtn : ImageButton = binding.galleryButton
        galleryBtn.setOnClickListener {

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageUri = createImageUri()
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    private fun createImageUri(): Uri {
        val imageFile = File(requireContext().filesDir, "camera_photos.jpg")
        return FileProvider.getUriForFile(
            requireContext(),
            "com.example.fypapp.fileProvider",
            imageFile
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imageUri : Uri? = data?.data
            imageUri?.let { uri ->
                // set imageuri
                sharedViewModel.setImageUri(uri)

                // navigate from home fragment to analysis fragment
                navController.navigate(R.id.action_navigation_home_to_analysisFragment)
            }
        }
    }
}