package com.example.fypapp

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.fypapp.databinding.ActivityAnalysisBinding

class AnalysisActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAnalysisBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAnalysisBinding. inflate(layoutInflater)
        setContentView(binding.root)

        // Display image
        val analyseImage : ImageView = binding.analyseImageView

        // Get the Uri from the intent
        val imageUri: Uri? = intent.getParcelableExtra("imageUri")

        // Load and display the image using Glide
        Glide.with(this)
            .load(imageUri)
            .into(analyseImage)

    }
}