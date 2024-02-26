package com.example.fypapp

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {

    // Captured/Selected Image Uri from Home Fragment to Analysis Fragment
    private val _imageUri = MutableLiveData<Uri>()
    val imageUri: LiveData<Uri> = _imageUri

    fun setImageUri(uri: Uri) {
        _imageUri.value = uri
    }
}