package com.example.fypapp.ui.history

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.fypapp.MedifyApplication
import com.example.fypapp.R
import com.example.fypapp.SharedViewModel
import com.example.fypapp.SharedViewModelFactory
import com.example.fypapp.adapter.ResultsAdapter
import com.example.fypapp.data.ColResult
import com.example.fypapp.databinding.FragmentHistoryItemBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter

class HistoryItemFragment : Fragment() {

    private var _binding: FragmentHistoryItemBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    // contains the current history item
    private val args by navArgs<HistoryItemFragmentArgs>()

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(
            (requireActivity().application as MedifyApplication).gResultRepository,
            (requireActivity().application as MedifyApplication).thresholdValueRepository
        )
    }

    // Declare variables
    private val resultsList = ArrayList<ColResult>()

    // Data entries for each of the H, S, V values
    private var hueData = ArrayList<Entry>()
    private var satData = ArrayList<Entry>()
    private var valData = ArrayList<Entry>()

    // Limit Lines
    private lateinit var limitLine: LimitLine
    private var resazurinThreshold: Float = 0f
    private var r6gThreshold: Float = 0f
    private val testVal: Int = 130

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

        // For R6G Dye only
        if (args.currentResult.dyeColour == "R6G Dye") {
            // Set the R6G dye info text to be visible
            binding.r6gInfoText.visibility = View.VISIBLE

            // Set nested scrollview containing recyclerview to be visible
            binding.resultsScrollView.visibility = View.VISIBLE

            // TODO: find a way to get the results...
            // Results in RecyclerView
            val resultsAdapter = ResultsAdapter(resultsList)
            val resultsRecyclerView = binding.resultsRecyclerView
            resultsRecyclerView.adapter = resultsAdapter

            // Set x-Axis TextView
            binding.xAxisText.text = getString(R.string.text_xAxis_r6g)

            // Set y-Axis TextView
            binding.yAxisText.text = getString(R.string.text_yAxis_r6g)
        } else {
            // For Resazurin
            // Set x-Axis TextView
            binding.xAxisText.text = getString(R.string.text_xAxis_resazurin)

            // Set y-Axis TextView
            binding.yAxisText.text = getString(R.string.text_yAxis_resazurin)
        }

        /* Plot LineChart */
        // LineChart data values
        hueData = args.currentResult.hueDataset
        satData = args.currentResult.satDataset
        //valData = args.currentResult.valDataset

        val lineChart = binding.lineChart

        // Disable description label
        lineChart.description.isEnabled = false

        // Set pinch zoom
        lineChart.setPinchZoom(true)

        // x-axis
        val xAxis = lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM     // Set x-axis to bottom of graph
        xAxis.enableGridDashedLine(10f, 10f,0f)
        // Customise x-Axis Labels
        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                return value.toInt().toString()
            }
        }

        // y-axis (left)
        val yAxis = lineChart.axisLeft
        yAxis.removeAllLimitLines()
        yAxis.enableGridDashedLine(10f, 10f,0f)

        // Disable the y-axis on the right
        lineChart.axisRight.isEnabled = false

        sharedViewModel.thresholdSettings.observe(viewLifecycleOwner, Observer { settings ->
            resazurinThreshold = settings.resazurinThreshold.toFloat()
            r6gThreshold = settings.r6gThreshold.toFloat()

            Log.d("Debug_values", "$resazurinThreshold & $r6gThreshold")

            // Customise graph according to the different dyes
            if (args.currentResult.dyeColour == "R6G Dye") {
                // R6G Dye
                // Set Label Count to 5
                xAxis.setLabelCount(5, true)

                // Declare limit lines to indicate above threshold value is bad
                limitLine = LimitLine(r6gThreshold, "Threshold")

            } else {
                // Resazurin Dye
                // Set Label Count to 15
                xAxis.setLabelCount(15, true)

                // Declare limit lines to indicate below threshold value is bad
                limitLine = LimitLine(resazurinThreshold, "Threshold")
            }

            // Add limitline to y-axis
            limitLine.lineWidth = 4f
            limitLine.enableDashedLine(10f, 10f, 0f)
            limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            limitLine.textSize = 10f
            yAxis.addLimitLine(limitLine)
        })


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