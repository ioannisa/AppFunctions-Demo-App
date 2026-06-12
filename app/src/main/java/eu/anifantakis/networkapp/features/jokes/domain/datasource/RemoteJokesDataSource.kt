package eu.anifantakis.networkapp.features.jokes.domain.datasource

import eu.anifantakis.networkapp.features.jokes.data.model.JokeEntity

interface RemoteJokesDataSource {

    suspend fun fetchJokesFromApi() : List<JokeEntity>

}