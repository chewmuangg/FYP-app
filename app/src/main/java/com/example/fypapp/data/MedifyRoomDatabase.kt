package com.example.fypapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [GraphResult::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class MedifyRoomDatabase : RoomDatabase() {

    abstract fun graphResultDao(): GraphResultDao

    companion object {

        @Volatile
        private var Instance: MedifyRoomDatabase? = null

        fun getDatabase(context: Context): MedifyRoomDatabase {
            return Instance ?:  synchronized(this) {
                Room.databaseBuilder(context, MedifyRoomDatabase::class.java, "medify_database")
                    .build()
                    .also { Instance = it }
            }
        }
    }
}