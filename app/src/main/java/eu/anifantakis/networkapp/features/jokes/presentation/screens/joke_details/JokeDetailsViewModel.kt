package eu.anifantakis.networkapp.features.jokes.presentation.screens.joke_details

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import eu.anifantakis.networkapp.features.jokes.domain.JokesRepository
import eu.anifantakis.networkapp.features.jokes.domain.Joke
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class JokeDetailsState(
    val joke: eu.anifantakis.networkapp.features.jokes.domain.Joke? = null,
    val loading: Boolean = false
)

sealed interface JokeDetailsIntent {
    data object ToggleFavorite: JokeDetailsIntent
    data object GoBack: JokeDetailsIntent
}

sealed interface JokesDetailsEvent {
    data object GoBack: JokesDetailsEvent
}

@Stable
class JokesDetailsViewModel(
    joke: Joke,
    private val repository: JokesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(JokeDetailsState(joke = joke))
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<JokesDetailsEvent>()
    val eventChannel = _eventChannel.receiveAsFlow()

    fun onIntent(intent: JokeDetailsIntent) {
        when(intent) {
            JokeDetailsIntent.GoBack -> {
                _eventChannel.trySend(JokesDetailsEvent.GoBack)
            }

            JokeDetailsIntent.ToggleFavorite -> toggleFavorite()
        }
    }

    /**
     * Toggle the favorite status of the current joke
     */
    private fun toggleFavorite() {
        viewModelScope.launch {
            _state.value.joke?.let { joke ->
                repository.toggleFavorite(joke.id)
                    .onSuccess {
                        // to be sure, let's read that the joke is updated in the database before we update the state
                        val updatedJoke = repository.getJokeById(joke.id).getOrNull()
                        updatedJoke?.let {
                            _state.update { it.copy(joke = updatedJoke) }
                        }
                    }
            }
        }
    }
}