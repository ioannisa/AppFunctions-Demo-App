package eu.anifantakis.networkapp.features.jokes.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Database entity representing a joke in the local Room database.
 */
@Entity(tableName = "joke")
data class JokeEntity(
    @PrimaryKey
    val id: Int,
    val question: String,
    val answer: String,
    val isFavorite: Boolean = false
)