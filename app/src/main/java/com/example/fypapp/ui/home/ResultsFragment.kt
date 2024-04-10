package com.example.fypapp.ui.home

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.fypapp.MedifyApplication
import com.example.fypapp.R
import com.example.fypapp.SharedViewModel
import com.example.fypapp.SharedViewModelFactory
import com.example.fypapp.adapter.ResultsAdapter
import com.example.fypapp.data.ColResult
import com.example.fypapp.data.GraphResult
import com.example.fypapp.databinding.FragmentResultsBinding
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ResultsFragment : Fragment() {

    private var _binding: FragmentResultsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val sharedViewModel: SharedViewModel by activityViewModels {
        SharedViewModelFactory(
            (requireActivity().application as MedifyApplication).gResultRepository
        )
    }

    // Declare variables
    private val resultsList = ArrayList<ColResult>()
    private var isRed: Boolean? = null
    private lateinit var image: Bitmap

    // Data entries for each of the H, S, V values
    private var hueData = ArrayList<Entry>()
    private var satData = ArrayList<Entry>()
    private var valData = ArrayList<Entry>()

    // Limit Lines
    private lateinit var limitLine: LimitLine

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
            image = it
            val processedImg = binding.processedImg
            processedImg.setImageBitmap(image)
        })

        sharedViewModel.isRed.observe(viewLifecycleOwner, Observer {
            isRed = it

            if (isRed!!) {
                // Set the R6G dye info text to be visible
                binding.r6gInfoText.visibility = View.VISIBLE

                // Set nested scrollview containing recyclerview to be visible
                binding.resultsScrollView.visibility = View.VISIBLE

                // Results in RecyclerView
                val resultsAdapter = ResultsAdapter(resultsList)
                val resultsRecyclerView = binding.resultsRecyclerView
                resultsRecyclerView.adapter = resultsAdapter
            }
        })

        // HSV value analysis
        // LineChart
        val lineChart = binding.lineChart
        var count = 1f

        // observe the intensityValues LiveData
        sharedViewModel.intensityValues.observe(viewLifecycleOwner, Observer { dataList ->

            if (isRed!!) {
                // R6G dye
                // Extract the Saturation values only
                val satList = ArrayList<Double>()

                for (data in dataList) {
                    val satValue = data.`val`[1]
                    satList.add(satValue)
                }

                // TODO: catch error here when data list not in multiples of 3
                // Loop through the dataList List and map to the respective attribute in the ColResult data class
                for (i in satList.indices step 3) {
                    // Every 3 values becomes one group
                    // Set the 3 individual values into value1, 2, 3 respectively
                    val value1 = satList[i]
                    val value2 = satList[i + 1]
                    val value3 = satList[i + 2]

                    // Calculate the average of the 3 values
                    val average = (value1 + value2 + value3) / 3

                    // Add average saturation values to satData array
                    satData.add(Entry(count, average.toFloat()))

                    // Add the data array of ColResult into resultList to display in recyclerView
                    resultsList.add(ColResult(count.toInt().toString(), String.format("%.5f", average), value1.toString(), value2.toString(), value3.toString()))

                    count++

                }

            } else {
                // Resazurin dye
                // Extract hue data only
                for (data in dataList) {
                    val hueValue = data.`val`[0]
                    val satValue = data.`val`[1]
                    val valValue = data.`val`[2]

                    Log.d("Debug HSV", "Area ${count.toInt()}, Hue: $hueValue, Saturation: $satValue, Value: $valValue")

                    // add the data values into the ArrayList<Entry> of hueData, satData & valData respectively
                    hueData.add(Entry(count, hueValue.toFloat()))
                    //satData.add(Entry(count, satValue.toFloat()))
                    //valData.add(Entry(count, valValue.toFloat()))

                    count++
                }
            }


            /*val listText : TextView = binding.listText
            val formattedText = dataList.joinToString(", ")
            listText.text = formattedText*/

            // Log the data entries added to verify correctness
//            Log.d("Debug Hue Data", "Hue Data: $hueData")
//            Log.d("Debug Sat Data", "Saturation Data: $satData")
//            Log.d("Debug Val Data", "Value Data: $valData")

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

            // Customise graph according to the different dyes
            if (isRed!!) {
                // R6G Dye
                // Set Label Count to 5
                xAxis.setLabelCount(5, true)

                // Declare limit lines to indicate above threshold value is bad
                limitLine = LimitLine(223f, "Threshold")

            } else {
                // Resazurin Dye
                // Set Label Count to 15
                xAxis.setLabelCount(15, true)

                // Declare limit lines to indicate below threshold value is bad
                limitLine = LimitLine(128f, "Threshold")
            }

            // Add limitline to y-axis
            limitLine.lineWidth = 4f
            limitLine.enableDashedLine(10f, 10f, 0f)
            limitLine.labelPosition = LimitLine.LimitLabelPosition.RIGHT_TOP
            limitLine.textSize = 10f
            yAxis.addLimitLine(limitLine)

            // Set data to plot the line graph
            val hueDataSet = LineDataSet(hueData, "Hue")
            hueDataSet.color = Color.BLUE

            val satDataSet = LineDataSet(satData, "Saturation")
            satDataSet.color = Color.GREEN

            val valDataSet = LineDataSet(valData, "Value")
            valDataSet.color = Color.RED

            val lineData = LineData(hueDataSet, satDataSet, valDataSet)

            lineChart.data = lineData
        })

        /*hueData.add(Entry(0f, 10f))
        satData.add(Entry(0f, 20f))
        valData.add(Entry(0f,30f))

        hueData.add(Entry(1f, 70f))
        satData.add(Entry(1f, 50f))
        valData.add(Entry(1f,90f))

        hueData.add(Entry(2f, 100f))
        satData.add(Entry(2f, 120f))
        valData.add(Entry(2f,40f))

        */


        // Navigate back to home upon clicking on "Back to Home button"
        val homeBtn = binding.homeButton
        homeBtn.setOnClickListener {
            // Navigate back to home
            findNavController().navigate(R.id.action_resultsFragment_to_navigation_home)
        }

        // Save current results into database upon clicking on "Save result" Button
        val saveBtn = binding.saveButton
        saveBtn.setOnClickListener {
            insertDataToDatabase()
        }

    }

    private fun insertDataToDatabase() {
        val resultImg = image
        val dyeColour = if (isRed!!) "R6G Dye" else "Resazurin"
        val resultHueData = hueData
        val resultSatData = satData
        val resultValData = valData

        // Create GraphResult object
        val result = GraphResult(0, getDateAndTime(), resultImg, dyeColour, resultHueData, resultSatData, resultValData)
        sharedViewModel.insertRecord(result)

        // Show Toast msg
        Toast.makeText(requireContext(), "Result is successfully saved!", Toast.LENGTH_SHORT).show()
    }

    // Function to get the date and time upon saving the result
    private fun getDateAndTime(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault())
        return dateFormat.format(Date())
    }

}
