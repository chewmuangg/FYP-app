package com.example.fypapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface GraphResultDao {

    // Add a new record into the database
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRecord (graphResult: GraphResult)

    // Get all the records to display
    @Query("SELECT * FROM graph_results_table")
    fun getAllRecords(): Flow<List<GraphResult>>

    // Get the specific record from the database using the primary key id
    @Query("SELECT * FROM graph_results_table WHERE id = :id")
    fun getRecord(id: Int): Flow<GraphResult>

    // Delete a record from database
    @Delete
    suspend fun deleteRecord(graphResult: GraphResult)
    
}