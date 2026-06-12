package eu.anifantakis.networkapp.features.core.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

object KtorClient {

    val httpClient = HttpClient(OkHttp) {
        // expectSuccess = true // shorthand for: "install a built-in validator that throws when status 200..299."

        // For a customized management for success range, instead of "expectSuccess" use "HttpResponseValidator".
        HttpResponseValidator {
            validateResponse { response ->
                val code = response.status.value
                if (code !in 200..399) {
                    throw ResponseException(response, "HTTP $code")
                }
            }
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true // Be resilient to new fields in the JSON
                prettyPrint = true       // Useful for logging
                isLenient = true         // Be lenient to no-compliant JSON features

            })
        }

        engine {
            config {
                callTimeout(10, TimeUnit.SECONDS)
            }
        }
    }

}