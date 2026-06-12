package eu.anifantakis.networkapp.features.jokes.data

import kotlinx.coroutines.CancellationException

/** safeCall = runCatching + one fix.
 *
 * The only difference is it rethrows CancellationException so structured concurrency stays intact.
 */
inline fun <T> safeCall(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: CancellationException) {
    throw e
} catch (e: Throwable) {
    Result.failure(e)
}
