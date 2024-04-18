package com.example.fypapp

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import com.example.fypapp.data.GraphResult
import com.example.fypapp.data.GraphResultRepository
import com.example.fypapp.data.ThresholdValue
import com.example.fypapp.data.ThresholdValueRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.opencv.core.Scalar

class SharedViewModel(
    private val graphResultRepository: GraphResultRepository,
    private val thresholdValueRepository: ThresholdValueRepository
) : ViewModel() {

    /* Image Uri */
    // To get the Captured/Selected Image Uri from Home Fragment in Analysis Fragment
    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> = _imageUri

    // Function to set the Image Uri
    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }

    /* Processed Image Uri */
    // To get the resulting processed image bitmap from Analysis Fragment in Results Fragment
    private val _processedBitmap = MutableLiveData<Bitmap>()
    val processedBitmap: LiveData<Bitmap> = _processedBitmap

    // Function to set the value of processedBitmap
    fun setProcessedBitmap(bitmap: Bitmap) {
        _processedBitmap.value = bitmap
    }

    /* Intensity Values List */
    // To get the intensityValues List from AnalysisFragment in ResultsFragment
//    private val _intensityValues = MutableLiveData<List<Double>>()
//    val intensityValues: LiveData<List<Double>> = _intensityValues
    private val _intensityValues = MutableLiveData<List<Scalar>>()
    val intensityValues: LiveData<List<Scalar>> = _intensityValues

    // Function to set the value of the intensityValues List
    /*fun setIntensityValuesList(dataList: List<Double>) {
        _intensityValues.value = dataList
    }*/
    fun setIntensityValuesList(dataList: List<Scalar>) {
        _intensityValues.value = dataList
    }

    /* Number of Contours */
    private val _contoursNum = MutableLiveData<Int>()
    val contourNum: LiveData<Int> = _contoursNum

    fun setContourNum(contourValue: Int) {
        _contoursNum.value = contourValue
    }


    private val _isRed = MutableLiveData<Boolean>()
    val isRed: LiveData<Boolean> = _isRed

    fun setIsRed(state: Boolean) {
        _isRed.value = state
    }

    /* Threshold Value */
    private val _thresholdSettings = MutableLiveData<ThresholdValue>()
    val thresholdSettings: LiveData<ThresholdValue> = _thresholdSettings

    /* Database */

    /* GraphResult */
    // Retrieve all GraphResults from database
    val allGraphResult: LiveData<List<GraphResult>> = graphResultRepository.allResults.asLiveData()

    // Save new GraphResult to database
    fun insertRecord(newGraphResult: GraphResult) = viewModelScope.launch {
        graphResultRepository.insert(newGraphResult)
    }

    // Retrieve the specific GraphResult from database using the specified id


    // Delete the specific GraphResult


    /* ThresholdValue */
    // Initialise Threshold settings upon app launch
    private val id = 1
    fun initThresholdSettings() {
        viewModelScope.launch {
            val existingSettings = thresholdValueRepository.getThresholdValue(id = id)
            val isExists = thresholdValueRepository.getDataCount()
            if (isExists == 0) {
                // No data in threshold_value_table
                // Create new (default) ThresholdValue and save to database
                val defaultThreshold = ThresholdValue()
                thresholdValueRepository.insertThreshold(defaultThreshold)
                _thresholdSettings.value = defaultThreshold
            } else {
                // There is data in threshold_value_table
                existingSettings.collect{
                    // retrieve the existing settings and set as the current setting
                    _thresholdSettings.value = it
                }
            }
        }
    }

    // Function to update resazurin_threshold value in threshold_value_table
    fun updateResazurinValue(value: Int) = viewModelScope.launch {
        thresholdValueRepository.updateResazurin(value)
    }

    // Function to update r6g_threshold value in threshold_value_table
    fun updateR6gValue(value: Int) = viewModelScope.launch {
        thresholdValueRepository.updateR6g(value)
    }

}

class SharedViewModelFactory(
    private val graphResultRepository: GraphResultRepository,
    private val thresholdValueRepository: ThresholdValueRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedViewModel(graphResultRepository, thresholdValueRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}