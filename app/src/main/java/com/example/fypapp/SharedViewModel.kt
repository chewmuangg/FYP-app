package com.example.fypapp

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import com.example.fypapp.data.GraphResult
import com.example.fypapp.data.GraphResultRepository
import kotlinx.coroutines.launch
import org.opencv.core.Scalar

class SharedViewModel(private val graphResultRepository: GraphResultRepository) : ViewModel() {

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

    /* Database */

    // Retrieve all GraphResults from database
    val allGraphResult: LiveData<List<GraphResult>> = graphResultRepository.allResults.asLiveData()

    // Save new GraphResult to database
    fun insertRecord(newGraphResult: GraphResult) = viewModelScope.launch {
        graphResultRepository.insert(newGraphResult)
    }

    // Retrieve the specific GraphResult from database using the specified id


    // Delete the specific GraphResult


}

class SharedViewModelFactory(
    private val graphResultRepository: GraphResultRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SharedViewModel(graphResultRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}