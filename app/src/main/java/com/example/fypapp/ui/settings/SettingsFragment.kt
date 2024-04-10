package com.example.fypapp.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.fypapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // Declare variables
    private lateinit var resSeekbar: SeekBar
    private lateinit var r6gSeekbar: SeekBar

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

        // TODO: database + update threshold value live
        // Save Button
        val saveBtn = binding.saveBtn
        saveBtn.setOnClickListener {
            viewingMode()
            Toast.makeText(requireContext(), "Changes saved!", Toast.LENGTH_SHORT).show()
        }

        // Cancel Button
        val cancelBtn = binding.cancelBtn
        cancelBtn.setOnClickListener {
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