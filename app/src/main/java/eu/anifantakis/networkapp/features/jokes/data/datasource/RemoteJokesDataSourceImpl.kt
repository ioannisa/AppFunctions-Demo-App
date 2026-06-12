package eu.anifantakis.networkapp.features.jokes.data.datasource

import eu.anifantakis.networkapp.features.jokes.data.model.JokeDto
import eu.anifantakis.networkapp.features.jokes.data.model.JokeEntity
import eu.anifantakis.networkapp.features.jokes.data.model.toEntity
import eu.anifantakis.networkapp.features.jokes.data.model.toJoke
import eu.anifantakis.networkapp.features.jokes.domain.datasource.RemoteJokesDataSource
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class RemoteJokesDataSourceImpl(
    private val httpClient: HttpClient,
): RemoteJokesDataSource {

    private val baseUrl = "https://official-joke-api.appspot.com"
    private val randomJokesPath = "/random_ten"

    override suspend fun fetchJokesFromApi(): List<JokeEntity> {
        val remoteJokes = httpClient.get("$baseUrl$randomJokesPath").body<List<JokeDto>>()

        return remoteJokes
            .map { jokeDto ->
                jokeDto.toJoke().toEntity()
            }
    }

}