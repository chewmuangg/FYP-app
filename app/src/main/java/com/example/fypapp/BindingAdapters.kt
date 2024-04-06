package com.example.fypapp

import android.graphics.Bitmap
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("bitmap")
fun setBitmap(imageView: ImageView ,bitmap: Bitmap?) {
    bitmap?.let {
        imageView.setImageBitmap(it)
    }
}