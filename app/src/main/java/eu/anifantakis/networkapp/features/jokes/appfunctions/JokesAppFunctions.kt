package eu.anifantakis.networkapp.features.jokes.appfunctions

import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunctionAppUnknownException
import androidx.appfunctions.AppFunctionDeclaration
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import eu.anifantakis.networkapp.features.jokes.domain.JokesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject

/**
 * App Functions that can be exposed to AI agents via MCP.
 *
 * AppFunctions live inside an abstract [AppFunctionService] marked with
 * [AppFunctionServiceEntryPoint]. From this class KSP generates:
 * - `JokesAppFunctionService`, the concrete service registered in `AndroidManifest.xml`.
 * - `assets/jokes_app_functions.xml`, the schema that describes each function to agents.
 *
 * The system creates the service, so dependencies come from Koin instead of the constructor.
 */
@RequiresApi(36)
@AppFunctionServiceEntryPoint(
    serviceName = "JokesAppFunctionService",
    appFunctionXmlFileName = "jokes_app_functions", // no ".xml": KSP appends it
)
abstract class JokesAppFunctions : AppFunctionService() {

    private val repository: JokesRepository by inject()

    /**
     * Unmarks every joke the user has previously marked as a favorite, leaving the favorites list empty.
     *
     * Only the favorite flag is affected. The underlying jokes remain in the database and are still
     * visible in the main list. This operation is safe to repeat: calling it on an already-empty favorites
     * list is a no-op. It is irreversible: cleared favorite markers cannot be restored.
     *
     * @return A short human-readable message confirming that all favorites were cleared.
     * @throws AppFunctionAppUnknownException If the database update fails; the favorites were not
     * cleared, so tell the user and suggest trying again.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun clearFavorites(): String = withContext(Dispatchers.IO) {
        repository.clearAllFavorites().getOrElse { error ->
            throw AppFunctionAppUnknownException("Failed to clear favorite jokes: ${error.message}")
        }

        "All favorite jokes have been cleared successfully."
    }

    /**
     * Lists every joke the user has currently marked as a favorite, including its set-up and punchline.
     *
     * Returns structured data rather than a sentence, so a calling agent can read, count, or quote
     * individual jokes. Use the id of a returned joke when calling "setFavorite" to unmark a specific
     * one. An empty list means the user has no favorites.
     *
     * @return The user's favorite jokes; an empty list if there are none.
     * @throws AppFunctionAppUnknownException If the database read fails; the favorites could not be
     * loaded, so tell the user and suggest trying again.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun getFavorites(): List<JokeAF> = withContext(Dispatchers.IO) {
        repository.getFavorites()
            .getOrElse { error ->
                throw AppFunctionAppUnknownException("Failed to load favorite jokes: ${error.message}")
            }
            .map { joke -> joke.toJokeAF() }
    }

    /**
     * Marks or unmarks a single joke as a favorite, identified by its id.
     *
     * Required workflow: call "getFavorites" first when you need a valid joke id to act on.
     * This sets the favorite flag to an absolute value rather than toggling it, so the call is safe to
     * repeat — requesting isFavorite = true on a joke that is already a favorite leaves it a favorite.
     *
     * @param jokeId The unique identifier of the joke to update.
     * @param isFavorite The desired favorite state: true to mark as a favorite, false to unmark it.
     * @return A short human-readable status message describing the new state of the joke.
     * @throws AppFunctionElementNotFoundException If no joke exists with the given jokeId; suggest the
     * user call "getFavorites" or browse the jokes list to obtain a valid id.
     * @throws AppFunctionAppUnknownException If the database lookup or update fails; the joke was not
     * updated, so tell the user and suggest trying again.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun setFavorite(
        jokeId: Int,
        isFavorite: Boolean,
    ): String = withContext(Dispatchers.IO) {
        val joke = repository.getJokeById(jokeId)
            .getOrElse { error ->
                throw AppFunctionAppUnknownException("Failed to look up joke $jokeId: ${error.message}")
            }
            ?: throw AppFunctionElementNotFoundException("No joke found with id $jokeId.")

        repository.setFavorite(jokeId, isFavorite).getOrElse { error ->
            throw AppFunctionAppUnknownException("Failed to update joke ${joke.id}: ${error.message}")
        }

        if (isFavorite) "Joke ${joke.id} is now a favorite." else "Joke ${joke.id} is no longer a favorite."
    }
}


/* Test by running:

adb shell cmd app_function list-app-functions | grep -A 10 "eu.anifantakis.networkapp"

adb shell cmd app_function execute-app-function \
  --package eu.anifantakis.networkapp \
  --function eu.anifantakis.networkapp.features.jokes.appfunctions.JokesAppFunctions#clearFavorites \
  --parameters '{}'

adb shell cmd app_function execute-app-function \
  --package eu.anifantakis.networkapp \
  --function eu.anifantakis.networkapp.features.jokes.appfunctions.JokesAppFunctions#getFavorites \
  --parameters '{}'

// NOTE on quoting: a JSON payload with quotes/spaces must survive TWO shells (your shell + the
// on-device shell). Wrap the whole command in double quotes and single-quote the JSON inside,
// escaping the inner double quotes. Otherwise you get: JSONException: End of input ...
adb shell "cmd app_function execute-app-function \
  --package eu.anifantakis.networkapp \
  --function eu.anifantakis.networkapp.features.jokes.appfunctions.JokesAppFunctions#setFavorite \
  --parameters '{\"jokeId\":86,\"isFavorite\":true}'"

 */
