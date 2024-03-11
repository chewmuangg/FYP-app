package com.example.fypapp.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.fypapp.R
import com.example.fypapp.SharedViewModel
import com.example.fypapp.adapter.ResultsAdapter
import com.example.fypapp.data.ColResult
import com.example.fypapp.databinding.FragmentResultsBinding

class ResultsFragment : Fragment() {

    private var _binding: FragmentResultsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels()

    // Declare variables
    private val resultsList = ArrayList<ColResult>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

        val resultsAdapter = ResultsAdapter(resultsList)
        val resultsRecyclerView = binding.resultsRecyclerView
        resultsRecyclerView.adapter = resultsAdapter

        sharedViewModel.intensityValues.observe(viewLifecycleOwner, Observer {
            // Display the list of Intensity Values
            val listText : TextView = binding.listText
            val formattedText = it.joinToString(", ")
            listText.text = formattedText
        })
    }

    private fun initData() {
        resultsList.add(
            ColResult(
            "Column 1",
            "50",
            "51",
            "52",
            "53"
            )
        )
        resultsList.add(
            ColResult(
            "Column 2",
            "60",
            "61",
            "62",
            "63"
            )
        )
        resultsList.add(
            ColResult(
            "Column 3",
            "70",
            "71",
            "72",
            "73"
            )
        )

    }

}