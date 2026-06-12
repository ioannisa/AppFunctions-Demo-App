package eu.anifantakis.networkapp.features.jokes.domain.datasource

import eu.anifantakis.networkapp.features.jokes.data.model.JokeEntity
import eu.anifantakis.networkapp.features.jokes.domain.Joke
import kotlinx.coroutines.flow.Flow

interface LocalJokesDataSource {

    fun getJokes(): Flow<List<Joke>>

    suspend fun deleteAllNonFavoriteJokes()

    suspend fun upsertJokes(jokes: List<JokeEntity>)

    suspend fun getJokeById(jokeId: Int): JokeEntity?

    suspend fun toggleFavorite(jokeId: Int)

    suspend fun setFavorite(jokeId: Int, isFavorite: Boolean)

    suspend fun getFavoriteJokesIds(): List<Int>

    suspend fun getFavoriteJokes(): List<Joke>

    suspend fun clearAllFavorites()

}