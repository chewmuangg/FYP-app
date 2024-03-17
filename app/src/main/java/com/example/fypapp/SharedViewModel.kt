package com.example.fypapp

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

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
    private val _intensityValues = MutableLiveData<List<Double>>()
    val intensityValues: LiveData<List<Double>> = _intensityValues

    // Function to set the value of the intensityValues List
    fun setIntensityValuesList(dataList: List<Double>) {
        _intensityValues.value = dataList
    }

}