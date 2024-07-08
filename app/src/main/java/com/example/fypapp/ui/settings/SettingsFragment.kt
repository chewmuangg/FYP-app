package com.example.fypapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.fypapp.MedifyApplication
import com.example.fypapp.SharedViewModel
import com.example.fypapp.SharedViewModelFactory
import com.example.fypapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // SharedViewModel
    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(
            (requireActivity().application as MedifyApplication).gResultRepository,
            (requireActivity().application as MedifyApplication).thresholdValueRepository
        )
    }

    // Declare variables
    private lateinit var resSeekbar: SeekBar
    private lateinit var r6gSeekbar: SeekBar
    private var newResazurinValue: Int = 0
    private var newR6gValue: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Assign the SharedViewModel component to a property in the binding class
        binding.sharedViewModel = sharedViewModel

        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        sharedViewModel.thresholdSettings.observe(viewLifecycleOwner, Observer { settings ->
            newResazurinValue = settings.resazurinThreshold
            newR6gValue = settings.r6gThreshold
        })

        // Resazurin Seekbar
        resSeekbar = binding.seekBarResazurin
        resSeekbar.isEnabled = false

        // R6G Seekbar
        r6gSeekbar = binding.seekBarR6G
        r6gSeekbar.isEnabled = false

        // Edit Button
        val editBtn = binding.editBtn
        editBtn.setOnClickListener {
            editingMode()
        }

        // Save Button
        val saveBtn = binding.saveBtn
        saveBtn.setOnClickListener {
            // update values
            sharedViewModel.updateResazurinValue(newResazurinValue)
            sharedViewModel.updateR6gValue(newR6gValue)
            viewingMode()
            Toast.makeText(requireContext(), "Changes saved!", Toast.LENGTH_SHORT).show()
        }

        // Cancel Button
        val cancelBtn = binding.cancelBtn
        cancelBtn.setOnClickListener {
            sharedViewModel.thresholdSettings.observe(viewLifecycleOwner, Observer { settings ->
                // Resazurin
                newResazurinValue = settings.resazurinThreshold
                binding.resThresholdVal.text = "$newResazurinValue"
                binding.seekBarResazurin.progress = newResazurinValue

                // R6G
                newR6gValue = settings.r6gThreshold
                binding.r6gThresholdVal.text = "$newR6gValue"
                binding.seekBarR6G.progress = newR6gValue
            })
            viewingMode()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun editingMode() {
        // Enable seekbars
        resSeekbar.isEnabled = true
        r6gSeekbar.isEnabled = true

        // Set saveLayout to visible, show cancel and save buttons
        binding.saveLayout.visibility = View.VISIBLE

        // Set editLayout to gone, hide edit button
        binding.editLayout. visibility = View.GONE

        // Resazurin Seekbar Listener
        resSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.resThresholdVal.text = progress.toString()
                newResazurinValue = progress
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })

        // R6G Seekbar Listener
        r6gSeekbar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekbar: SeekBar?, progress: Int, fromUser: Boolean) {
                binding.r6gThresholdVal.text = progress.toString()
                newR6gValue = progress
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
    }

    private fun viewingMode() {
        // Disable seekbars
        resSeekbar.isEnabled = false
        r6gSeekbar.isEnabled = false

        // Set saveLayout to gone, hide cancel and save buttons
        binding.saveLayout.visibility = View.GONE

        // Set editLayout to visible, show edit button
        binding.editLayout. visibility = View.VISIBLE
    }
}