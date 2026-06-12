package eu.anifantakis.networkapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import eu.anifantakis.networkapp.features.jokes.domain.Joke
import eu.anifantakis.networkapp.features.jokes.presentation.screens.joke_details.JokeDetailsScreenRoot
import eu.anifantakis.networkapp.features.jokes.presentation.screens.joke_details.JokesDetailsViewModel
import eu.anifantakis.networkapp.features.jokes.presentation.screens.jokes_list.JokesListScreenRoot
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

sealed interface RandomJokesRoute: NavKey{
    @Serializable data object JokesList: RandomJokesRoute
    @Serializable data class SelectedJoke(val joke: Joke): RandomJokesRoute
}

@Composable
fun NavigationRoot(
    modifier: Modifier = Modifier
) {
    val backStack = rememberNavBackStack(RandomJokesRoute.JokesList)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),

        entryProvider = entryProvider {
            entry<RandomJokesRoute.JokesList> {
                JokesListScreenRoot(
                    modifier = modifier,
                    onGoToJokeDetails = { joke ->
                        backStack.add(RandomJokesRoute.SelectedJoke(joke))
                    }
                )
            }

            entry<RandomJokesRoute.SelectedJoke> {
                println(it.joke.answer)

                val viewModel: JokesDetailsViewModel = koinViewModel { parametersOf(it.joke) }

                JokeDetailsScreenRoot(
                    modifier = modifier,
                    viewModel = viewModel,
                    onGoBack = {
                        backStack.removeLastOrNull()
                    })
            }
        }
    )
}