package eu.anifantakis.networkapp.features.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import eu.anifantakis.networkapp.features.jokes.data.database.JokesDao
import eu.anifantakis.networkapp.features.jokes.data.model.JokeEntity

// Define migration from version 1 to 2 (adding isFavorite column)
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // Add isFavorite column with default value of 0 (false)
        database.execSQL("ALTER TABLE joke ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(entities = [JokeEntity::class], version = 2, exportSchema = false)
abstract class JokesDatabase : RoomDatabase() {
    abstract fun jokesDao(): JokesDao
}