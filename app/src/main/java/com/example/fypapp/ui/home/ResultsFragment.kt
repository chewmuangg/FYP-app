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

        sharedViewModel.processedBitmap.observe(viewLifecycleOwner, Observer {
            val processedImg = binding.processedImg
            processedImg.setImageBitmap(it)
        })

        val resultsAdapter = ResultsAdapter(resultsList)
        val resultsRecyclerView = binding.resultsRecyclerView
        resultsRecyclerView.adapter = resultsAdapter

        var colNum = 1

        sharedViewModel.intensityValues.observe(viewLifecycleOwner, Observer {dataList ->
            // Loop through the dataList List and map to the respective attribute in the ColResult data class
            for (i in dataList.indices step 3) {
                // Every 3 values becomes one group
                // Set the name of the column
                val colName = "Test ${colNum++}"    // Test 1, Test 2, Test 3 ...

                // Set the 3 individual values into value1, 2, 3 respectively
                val value1 = dataList[i]
                val value2 = dataList[i + 1]
                val value3 = dataList[i + 2]

                // Calculate the average of the 3 values
                val average = (value1 + value2 + value3) / 3

                // Add the data array of ColResult into resultList
                resultsList.add(ColResult(colName, String.format("%.5f", average), value1.toString(), value2.toString(), value3.toString()))

            }
        })
    }

}