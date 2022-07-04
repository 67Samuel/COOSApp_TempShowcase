package com.samuel.coosapp2.database

import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.samuel.coosapp2.business.datasource.cache.AppDatabase
import org.junit.Rule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

/**
 * For help, refer to https://github.com/AdamMc331/mastering-room-migrations
 */

@RunWith(JUnit4::class)
class MigrationTest {
    private lateinit var database: SupportSQLiteDatabase

    @JvmField
    @Rule
    val migrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(), // the instrumentation we're running the test under
        AppDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    /**
     * test fails, unknown reason
     */
    // TODO: Refactor to test using video instead of note, since we don't have notes for this version
//    @Test
//    fun migrate1To2() = runBlocking {
//        val databaseName = "test-database"
//        val pk = "this is the pk"
//        val body = "This is the body"
//        database = migrationTestHelper.createDatabase(databaseName, 1)
//            .apply {
//                // manipulation of db before migration
//                // we need to write manual SQLite since we're updating the old database which is not supported
//                execSQL("""
//                    INSERT INTO note VALUES ('$pk', '$body')
//                """.trimIndent())
//                close()
//            }
//
//        database = migrationTestHelper.runMigrationsAndValidate(
//            databaseName,
//            2,
//            true,
//            MIGRATION_1_2
//        )
//
//        val appDatabase = Room.databaseBuilder(
//            InstrumentationRegistry.getInstrumentation().targetContext,
//            AppDatabase::class.java,
//            databaseName
//        )
//            .allowMainThreadQueries() // not good in production, but it simplifies things in testing
//            .build()
//
//        // try to get the note we passed to the database in the old version
//        val note = appDatabase.getNoteDao().getNote(pk)
//
//        // validate the data is correct in the new database version
//        assertTrue (note?.pk == pk)
//        assertTrue (note?.body == pk)
//    }
}