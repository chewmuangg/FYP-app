package com.example.fypapp.data

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.github.mikephil.charting.data.Entry

@Entity(tableName = "graph_results_table")
data class GraphResult(

    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "date_and_time") var dateAndTime: String,
    @ColumnInfo(name = "image") val image: Bitmap,
    @ColumnInfo(name = "dye_colour") val dyeColour: String,
    @ColumnInfo(name = "hue_dataset") val hueDataset: ArrayList<Entry>,
    @ColumnInfo(name = "saturation_dataset") val satDataset: ArrayList<Entry>,
    @ColumnInfo(name = "value_dataset") val valDataset: ArrayList<Entry>

    )
