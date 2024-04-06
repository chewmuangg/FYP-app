package com.example.fypapp.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fypapp.MedifyApplication
import com.example.fypapp.SharedViewModel
import com.example.fypapp.SharedViewModelFactory
import com.example.fypapp.adapter.HistoryListAdapter
import com.example.fypapp.databinding.FragmentHistoryBinding

class HistoryFragment : Fragment() {

    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels() {
        SharedViewModelFactory(
            (requireActivity().application as MedifyApplication).gResultRepository
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView
        val historyAdapter = HistoryListAdapter(emptyList())
        val historyRecyclerView = binding.historyRecyclerView
        historyRecyclerView.adapter = historyAdapter

        sharedViewModel.allGraphResult.observe(viewLifecycleOwner, Observer { data ->
            historyAdapter.setGraphResults(data)

            val textNoHistory = binding.textNoHistory
            if (historyAdapter.itemCount == 0) {
                textNoHistory.visibility = View.VISIBLE
            } else {
                textNoHistory.visibility = View.GONE
            }
        })

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}