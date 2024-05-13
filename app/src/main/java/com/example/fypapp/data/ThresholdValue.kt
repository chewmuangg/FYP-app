package com.example.fypapp.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "threshold_value_table")
data class ThresholdValue(

    @PrimaryKey val id: Int = 1,
    @ColumnInfo(name = "resazurin_threshold") var resazurinThreshold: Int = 128,
    @ColumnInfo(name = "r6g_threshold") var r6gThreshold: Int = 223

)
