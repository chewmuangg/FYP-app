package com.example.fypapp.ui.history

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.fypapp.R
import com.example.fypapp.databinding.FragmentHistoryItemBinding
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet

class HistoryItemFragment : Fragment() {

    private var _binding: FragmentHistoryItemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // contains the current history item
    private val args by navArgs<HistoryItemFragmentArgs>()

    // Data entries for each of the H, S, V values
    private var hueData = ArrayList<Entry>()
    private var satData = ArrayList<Entry>()
    private var valData = ArrayList<Entry>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryItemBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set Data and Time text
        binding.textDateTime.text = args.currentResult.dateAndTime

        // Set Dye Colour text
        binding.textDyeColour.text = args.currentResult.dyeColour

        // Set image
        binding.processedImg.setImageBitmap(args.currentResult.image)

        /* Plot LineChart */
        // LineChart data values
        hueData = args.currentResult.hueDataset
        satData = args.currentResult.satDataset
        valData = args.currentResult.valDataset

        val lineChart = binding.lineChart
        val hueDataSet = LineDataSet(hueData, "Hue")
        hueDataSet.color = Color.BLUE
        val satDataSet = LineDataSet(satData, "Saturation")
        satDataSet.color = Color.GREEN
        val valDataSet = LineDataSet(valData, "Value")
        valDataSet.color = Color.RED

        val lineData = LineData(hueDataSet, satDataSet, valDataSet)
        lineChart.data = lineData

    }

}