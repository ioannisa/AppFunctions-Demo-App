package eu.anifantakis.networkapp.features.jokes.data.datasource

import eu.anifantakis.networkapp.features.jokes.data.database.JokesDao
import eu.anifantakis.networkapp.features.jokes.domain.Joke
import eu.anifantakis.networkapp.features.jokes.data.model.JokeEntity
import eu.anifantakis.networkapp.features.jokes.data.model.toJoke
import eu.anifantakis.networkapp.features.jokes.domain.datasource.LocalJokesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LocalJokesDataSourceImpl(
    private val database: JokesDao,
): LocalJokesDataSource {

    override fun getJokes(): Flow<List<Joke>> {
        // The ordering is handled by the DAO query (ORDER BY isFavorite DESC, id ASC)
        return database.getAllJokes().map { entities ->
            entities.map { it.toJoke() }
        }
    }

    override suspend fun deleteAllNonFavoriteJokes() {
        database.deleteAllNonFavoriteJokes()
    }

    override suspend fun upsertJokes(jokes: List<JokeEntity>) {
        database.upsertJokes(jokes)
    }

    override suspend fun getJokeById(jokeId: Int): JokeEntity? {
        return database.getJokeById(jokeId)
    }

    override suspend fun toggleFavorite(jokeId: Int) {
        database.toggleFavorite(jokeId)
    }

    override suspend fun setFavorite(jokeId: Int, isFavorite: Boolean) {
        database.setFavorite(jokeId, isFavorite)
    }

    override suspend fun getFavoriteJokesIds(): List<Int> {
        return database.getFavoriteJokesIds()
    }

    override suspend fun getFavoriteJokes(): List<Joke> {
        return database.getFavoriteJokes().map { it.toJoke() }
    }

    override suspend fun clearAllFavorites() {
        database.clearAllFavorites()
    }
}