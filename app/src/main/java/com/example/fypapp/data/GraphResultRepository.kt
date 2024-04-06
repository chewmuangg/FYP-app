package com.example.fypapp.data

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow

/* Declares the DAO as a private property in the constructor. Pass in the DAO
 * instead of the whole database because you only need to access to the DAO
 */

class GraphResultRepository(private val graphResultDao: GraphResultDao) {

    // Room executes all the queries on a separate thread
    // Observed Flow will notify the observer when the data has changed.

    /* Retrieve all the results from the graph_results_table */
    val allResults: Flow<List<GraphResult>> = graphResultDao.getAllRecords()

    /* Retrieve a result from the graph_results_table that matches with the given id */
    fun getResultDetails(id: Int):Flow<GraphResult?> = graphResultDao.getRecord(id)

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.

    /* Add a result into the graph_results_table */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(graphResult: GraphResult) {
        graphResultDao.insertRecord(graphResult)
    }

    /* Delete a result from the graph_results_table */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(graphResult: GraphResult) {
        graphResultDao.deleteRecord(graphResult)
    }
}