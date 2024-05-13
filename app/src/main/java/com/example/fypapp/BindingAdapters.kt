package com.example.fypapp

import android.graphics.Bitmap
import android.widget.ImageView
import android.widget.SeekBar
import androidx.databinding.BindingAdapter

@BindingAdapter("bitmap")
fun setBitmap(imageView: ImageView ,bitmap: Bitmap?) {
    bitmap?.let {
        imageView.setImageBitmap(it)
    }
}

@BindingAdapter("app:floatProgress")
fun setFloatProgress(seekBar: SeekBar, value: Float) {
    val intValue = value.toInt()
    seekBar.progress = intValue
}