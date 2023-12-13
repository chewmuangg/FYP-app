package com.example.fypapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import com.example.fypapp.R
import com.example.fypapp.SharedViewModel
import com.example.fypapp.databinding.FragmentAnalysisBinding

class AnalysisFragment : Fragment() {

    private var _binding:FragmentAnalysisBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val sharedViewModel:SharedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAnalysisBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Display captured image
        val capturedImage : ImageView = binding.captureImageView

        sharedViewModel.imageUri.observe(viewLifecycleOwner, { uri ->
            // update UI with the captured image
            capturedImage.setImageURI(uri)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}