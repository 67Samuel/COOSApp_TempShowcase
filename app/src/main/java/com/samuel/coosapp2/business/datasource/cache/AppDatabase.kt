package com.samuel.coosapp2.business.datasource.cache

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.samuel.coosapp2.business.datasource.cache.main.*

@Database(entities = [VideoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getVideoDao(): VideoDao

    companion object{
        val DATABASE_NAME: String = "app_db"
    }

}

/**
 * demo
 */
val MIGRATION_1_2 = object: Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // nothing changed
    }
}