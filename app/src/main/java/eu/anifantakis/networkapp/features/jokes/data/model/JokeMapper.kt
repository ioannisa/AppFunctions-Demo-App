package eu.anifantakis.networkapp.features.jokes.data.model

import eu.anifantakis.networkapp.features.jokes.domain.Joke

/**
 * Extension function to convert a JokeEntity to a Joke domain model.
 */
fun JokeEntity.toJoke(): Joke = Joke(
    id = id,
    question = question,
    answer = answer,
    isFavorite = isFavorite
)

fun JokeDto.toJoke(): Joke {
    return Joke(
        id = id,
        question = setup,
        answer = punchline,
        isFavorite = false // Default value for new jokes from API
    )
}

/**
 * Extension function to convert a Joke domain model to a JokeEntity.
 */
fun Joke.toEntity(): JokeEntity = JokeEntity(
    id = id,
    question = question,
    answer = answer,
    isFavorite = isFavorite
)