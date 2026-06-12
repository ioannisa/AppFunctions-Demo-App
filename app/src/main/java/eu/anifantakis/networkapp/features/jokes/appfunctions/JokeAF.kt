package eu.anifantakis.networkapp.features.jokes.appfunctions

import androidx.appfunctions.AppFunctionSerializable
import eu.anifantakis.networkapp.features.jokes.domain.Joke

/**
 * A single joke from this app, modelled as a question-and-answer pair: the question is the set-up
 * and the answer is the punchline. Returned by joke-reading functions such as the user's favorites
 * list, and identified by a stable numeric id that other functions accept to act on this joke.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class JokeAF(
    /** Stable unique identifier of the joke. Pass this back to functions that act on a single joke. */
    val id: Int,
    /** The set-up line of the joke (its "question" part). */
    val question: String,
    /** The punchline of the joke (its "answer" part). */
    val answer: String,
)

/**
 * Extension function to convert a domain [Joke] into its AppFunctions boundary model [AppFunctionJoke].
 */
fun Joke.toJokeAF(): JokeAF = JokeAF(
    id = id,
    question = question,
    answer = answer,
)