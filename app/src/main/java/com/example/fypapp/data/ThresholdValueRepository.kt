package com.example.fypapp.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

class ThresholdValueRepository(private val thresholdValueDao: ThresholdValueDao) {

    /* Retrieves all the results from the threshold_value_table */
    fun getThresholdValue(id: Int): Flow<ThresholdValue> = thresholdValueDao.getThreshold(id)

    /* get data count */
    suspend fun getDataCount(): Int = thresholdValueDao.getDataCount()

    /* Add a ThresholdValue into the threshold_value_table */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertThreshold(thresholdValue: ThresholdValue) {
        thresholdValueDao.insertThreshold(thresholdValue)
    }

    /* Update resazurin threshold value */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateResazurin(resazurinValue: Int) {
        thresholdValueDao.updateResazurin(resazurinValue)
    }

    /* Update R6G threshold value */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateR6g(r6gValue: Int) {
        thresholdValueDao.updateR6g(r6gValue)
    }

}