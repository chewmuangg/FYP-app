package com.example.fypapp.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import com.github.mikephil.charting.data.Entry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayOutputStream

class Converters {

    /* Bitmap */
    // Tells the database how to convert from Bitmap to ByteArray to be stored
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }

    // Tells the database how to convert from ByteArray to Bitmap to be read
    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    /* ArrayList<Entry> */
    // Tells the database how to convert from ArrayList<Entry> to json String to be stored
    @TypeConverter
    fun fromEntryArrayList(value: ArrayList<Entry>):String {
        return Gson().toJson(value)
    }

    // Tells the database how to convert from json String to ArrayList<Entry> to be read
    @TypeConverter
    fun toEntryArrayList(value: String): ArrayList<Entry> {
        val type = object : TypeToken<ArrayList<Entry>>() {}.type
        return Gson().fromJson(value, type)
    }
}