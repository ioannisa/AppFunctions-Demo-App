package eu.anifantakis.networkapp.features.jokes.presentation.screens.jokes_list

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.compose.rememberKSafeState
import eu.anifantakis.networkapp.R
import eu.anifantakis.networkapp.features.jokes.domain.Joke
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import kotlin.toString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JokesListScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: JokesListViewModel = koinViewModel(),
    onGoToJokeDetails: (Joke) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.eventChannel.collect {
            when (it) {
                is JokesListEvent.GotoJokeDetails -> {
                    onGoToJokeDetails(it.joke)
                }

                is JokesListEvent.ShowError -> {
                    Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    var menuExpanded by rememberSaveable { mutableStateOf(false) }
    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        topBar = {
            TopAppBar(
                title = { Text("Jokes List") },
                actions = {
                    // Add IconButton for the menu
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "content description"
                        )
                    }
                    // Add DropdownMenu
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Preferences") },
                            onClick = {
                                menuExpanded = false
                                //onGoToPreferences() // Call the navigation lambda
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("About") },
                            onClick = {
                                menuExpanded = false
                                //onGoToAbout() // Call the navigation lambda
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.menu_clear_favorites)) },
                            onClick = {
                                menuExpanded = false
                                viewModel.onIntent(JokesListIntent.ClearFavorites)
                            }
                        )
                    }
                }
            )
        }
    ) { inner ->

        JokesListScreen(
            modifier = Modifier.padding(inner),
            state = state,
            onIntent = viewModel::onIntent
        )
    }
}

@Composable
private fun JokesListScreen(
    modifier: Modifier = Modifier,
    state: JokesListState,
    onIntent: (JokesListIntent) -> Unit
) {

    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox (
        modifier = modifier,
        contentAlignment = Alignment.Center,
        state = pullToRefreshState,
        isRefreshing = state.refreshing,
        onRefresh = { onIntent(JokesListIntent.Refresh) },
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {

            val kSafe: KSafe = koinInject()
            //var selectedTabIndex by rememberSaveable { mutableStateOf(0) }
            var selectedTabIndex by kSafe.rememberKSafeState(0)

            val tabs = listOf(
                "All",
                "Favorites"
            )

            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                modifier = Modifier.fillMaxWidth()
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        text = { Text(title) },
                        selected = selectedTabIndex == index,
                        onClick = {

                            println("[test] selectedTabIndex before $selectedTabIndex")
                            selectedTabIndex = index
                            println("[test] index: $index")
                            println("[test] selectedTabIndex after $selectedTabIndex")

                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val displayedJokes = when (selectedTabIndex) {
                0 -> state.jokes
                1 -> state.jokes.filter { it.isFavorite }
                else -> state.jokes
            }

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(
                    items = displayedJokes,
                    key = { it.id }
                ) { joke ->
                    JokesListItem(
                        joke = joke,
                        onIntent = onIntent
                    )
                }
            }

            state.lastUpdate?.let { lastUpdate ->
                Text(
                    text = "Last updated: $lastUpdate",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }

        if (state.loading) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun JokesListItem(
    joke: Joke,
    onIntent: (JokesListIntent) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onIntent(JokesListIntent.ClickOnJoke(joke))
                }
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = joke.id.toString(),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 16.dp)
                )

                Column(
                    modifier = Modifier.weight(1f),
                ) {
                    Text(text = joke.question, fontWeight = FontWeight.Bold)
                    Text(text = joke.answer)
                }

                IconButton(
                    onClick = { onIntent(JokesListIntent.ToggleFavorite(joke = joke)) },
                    modifier = Modifier.size(24.dp)
                ) {
                    if (joke.isFavorite) {
                        Icon(
                            imageVector = Icons.Outlined.Favorite,
                            contentDescription = "Info",
                            tint = Color.Red
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.FavoriteBorder,
                            contentDescription = "Info",
                            tint = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PreviewJokesListItem() {
    JokesListItem(
        joke = Joke(
            id = 1,
            question = "Why did the chicken cross the road?",
            answer = "To get to the other side!"
        ),
        onIntent = {}
    )
}

@Preview
@Composable
private fun PreviewJokesList() {
    JokesListScreen(
        state = JokesListState(
            jokes = listOf(
                Joke(
                    id = 1,
                    question = "Why did the chicken cross the road?",
                    answer = "To get to the other side!"
                ),
                Joke(
                    id = 2,
                    question = "What do you call a bear with no teeth?",
                    answer = "A gummy bear!"
                )
            ),
            loading = false
        ),
        onIntent = {}
    )
}