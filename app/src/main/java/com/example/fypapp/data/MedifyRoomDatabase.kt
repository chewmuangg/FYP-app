package com.example.fypapp.data

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    version = 2,
    entities = [GraphResult::class, ThresholdValue::class],
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class MedifyRoomDatabase : RoomDatabase() {

    abstract fun graphResultDao(): GraphResultDao
    abstract fun thresholdValueDao(): ThresholdValueDao

    companion object {

        @Volatile
        private var Instance: MedifyRoomDatabase? = null

        fun getDatabase(context: Context): MedifyRoomDatabase {
            return Instance ?:  synchronized(this) {

                // Migration from 1 to 2
                val MIGRATION_1_2 = object : Migration(1, 2) {
                    override fun migrate(db: SupportSQLiteDatabase) {
                        // Migration logic from version 1 to version 2
                        db.execSQL("CREATE TABLE IF NOT EXISTS threshold_value_table (id INTEGER PRIMARY KEY NOT NULL, resazurin_threshold INTEGER NOT NULL, r6g_threshold INTEGER NOT NULL)")
                    }
                }

                // Migration from 2 to 3
                val MIGRATION_2_3 = object : Migration(2, 3) {
                    override fun migrate(db: SupportSQLiteDatabase) {
                        // original table was accidentally dropped manually in the sql query tab
                        db.execSQL("CREATE TABLE IF NOT EXISTS threshold_value_table (id INTEGER PRIMARY KEY NOT NULL, resazurin_threshold REAL NOT NULL, r6g_threshold REAL NOT NULL)")
                    }
                }

                // Migration back to 2 from 3
                val REVERT_MIGRATION_3_2 = object : Migration(3, 2) {
                    override fun migrate(db: SupportSQLiteDatabase) {
                        // drop table
                        db.execSQL("DROP TABLE threshold_value_table")

                        // create table
                        db.execSQL("CREATE TABLE IF NOT EXISTS threshold_value_table (id INTEGER PRIMARY KEY NOT NULL, resazurin_threshold INTEGER NOT NULL, r6g_threshold INTEGER NOT NULL)")
                    }
                }

                Room.databaseBuilder(context, MedifyRoomDatabase::class.java, "medify_database")
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(REVERT_MIGRATION_3_2)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}