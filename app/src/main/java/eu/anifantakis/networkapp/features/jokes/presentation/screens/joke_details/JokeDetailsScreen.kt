package eu.anifantakis.networkapp.features.jokes.presentation.screens.joke_details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JokeDetailsScreenRoot(
    modifier: Modifier = Modifier,
    viewModel: JokesDetailsViewModel,
    onGoBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventChannel.collect {
            when (it) {
                JokesDetailsEvent.GoBack -> onGoBack()
            }
        }
    }

    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets,
        topBar = {
            TopAppBar(
                title = { Text("Joke Details") },
                navigationIcon = {
                    IconButton(onClick = { onGoBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.onIntent(JokeDetailsIntent.ToggleFavorite) },
                        modifier = Modifier.size(48.dp)
                    ) {
                        if (state.joke?.isFavorite == true) {
                            Icon(
                                imageVector = Icons.Outlined.Favorite,
                                contentDescription = "Remove from favorites",
                                tint = Color.Red,
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.FavoriteBorder,
                                contentDescription = "Add to favorites",
                                tint = Color.Gray,
                            )
                        }
                    }
                }
            )
        }
    ) { inner ->


        state.joke?.let {
            JokeDetailsScreen(
                modifier = modifier.padding(inner),
                state = state,
                onIntent = viewModel::onIntent
            )
        }
    }
}

@Composable
private fun JokeDetailsScreen(
    modifier: Modifier = Modifier,
    state: JokeDetailsState,
    onIntent: (JokeDetailsIntent) -> Unit
) {
    Column(
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Joke content
        Text(
            text = state.joke?.question ?: "",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 32.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = state.joke?.answer ?: "",
            fontSize = 20.sp,
            lineHeight = 28.sp
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}