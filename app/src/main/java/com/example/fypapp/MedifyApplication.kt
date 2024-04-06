package com.example.fypapp

import android.app.Application
import com.example.fypapp.data.GraphResultRepository
import com.example.fypapp.data.MedifyRoomDatabase

class MedifyApplication: Application() {

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts

    val database by lazy { MedifyRoomDatabase.getDatabase(this) }
    val gResultRepository by lazy { GraphResultRepository(database.graphResultDao()) }

}