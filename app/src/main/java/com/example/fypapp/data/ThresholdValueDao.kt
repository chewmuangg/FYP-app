package com.example.fypapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ThresholdValueDao {

    // add data into database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertThreshold(thresholdValue: ThresholdValue)

    // read data from database
    @Query("SELECT * FROM threshold_value_table WHERE id = :id")
    fun getThreshold(id: Int): Flow<ThresholdValue>

    // get data count
    @Query("SELECT COUNT(*) FROM threshold_value_table")
    suspend fun getDataCount(): Int

    // update data in database
    //@Update
    //suspend fun updateThreshold(thresholdValue: ThresholdValue)

    // update resazurinThreshold
    @Query("UPDATE threshold_value_table SET resazurin_threshold = :resazurinValue")
    suspend fun updateResazurin(resazurinValue: Int)

    // update r6gThreshold
    @Query("UPDATE threshold_value_table SET r6g_threshold = :r6gValue")
    suspend fun updateR6g(r6gValue: Int)

}