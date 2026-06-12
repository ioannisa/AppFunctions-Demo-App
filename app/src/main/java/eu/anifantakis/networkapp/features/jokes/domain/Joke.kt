package eu.anifantakis.networkapp.features.jokes.domain

import kotlinx.serialization.Serializable

@Serializable
data class Joke (
    val id: Int,
    val question: String,
    val answer: String,
    val isFavorite: Boolean = false
)