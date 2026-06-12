package eu.anifantakis.networkapp.features.jokes.data

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.invoke
import eu.anifantakis.networkapp.features.jokes.domain.datasource.LocalJokesDataSource
import eu.anifantakis.networkapp.features.jokes.domain.datasource.RemoteJokesDataSource
import eu.anifantakis.networkapp.features.jokes.domain.Joke
import eu.anifantakis.networkapp.features.jokes.data.model.toJoke
import eu.anifantakis.networkapp.features.jokes.domain.JokesRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Repository implementation for jokes data with offline-first approach.
 */
class JokesRepositoryImpl(
    private val remoteDataSource: RemoteJokesDataSource,
    private val localDataSource: LocalJokesDataSource,

    private val kSafe: KSafe
) : JokesRepository {

    private var lastUpdateDateTime: String? by kSafe(null)

    override fun getJokes(): Flow<List<Joke>> {
        // The ordering is handled by the DAO query (ORDER BY isFavorite DESC, id ASC)
        return localDataSource.getJokes()
    }

    override suspend fun fetchJokesFromApi(): Result<Unit> {
        return safeCall {
            println("Fetching jokes from API")

            // First, get a list of all favorite movies to preserve their status
            val favoriteJokesIds = localDataSource.getFavoriteJokesIds()

            val jokesToUpsert = remoteDataSource.fetchJokesFromApi()
                .filter { jokeDto ->
                    jokeDto.id !in favoriteJokesIds
                }


            // Delete only non-favorite jokes
            localDataSource.deleteAllNonFavoriteJokes()

            // Upsert new jokes (this won't affect existing favorites due to Room's upsert behavior)
            localDataSource.upsertJokes(jokesToUpsert)

            lastUpdateDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss"))

            println("Successfully updated database with ${jokesToUpsert.size} jokes")
        }
    }

    override fun getLastUpdate(): String? = lastUpdateDateTime


    override suspend fun getJokeById(id: Int): Result<Joke?> {
        return safeCall {
            localDataSource.getJokeById(id)?.toJoke()
        }
    }

    override suspend fun toggleFavorite(jokeId: Int): Result<Unit> {
        return safeCall {
            localDataSource.toggleFavorite(jokeId)
        }
    }

    override suspend fun setFavorite(jokeId: Int, isFavorite: Boolean): Result<Unit> {
        return safeCall {
            localDataSource.setFavorite(jokeId, isFavorite)
        }
    }

    override suspend fun isFavorite(jokeId: Int): Result<Boolean> {
        return safeCall {
            localDataSource.getJokeById(jokeId)?.isFavorite ?: false
        }
    }

    override suspend fun getFavorites(): Result<List<Joke>> {
        return safeCall {
            localDataSource.getFavoriteJokes()
        }
    }

    override suspend fun clearAllFavorites(): Result<Unit> {
        return safeCall {
            localDataSource.clearAllFavorites()
        }
    }
}