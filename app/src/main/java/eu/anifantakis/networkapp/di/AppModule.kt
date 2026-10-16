package eu.anifantakis.networkapp.di

import androidx.room.Room
import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.networkapp.features.core.data.JokesDatabase
import eu.anifantakis.networkapp.features.core.data.KtorClient
import eu.anifantakis.networkapp.features.core.data.MIGRATION_1_2
import eu.anifantakis.networkapp.features.jokes.data.JokesRepositoryImpl
import eu.anifantakis.networkapp.features.jokes.data.datasource.LocalJokesDataSourceImpl
import eu.anifantakis.networkapp.features.jokes.data.datasource.RemoteJokesDataSourceImpl
import eu.anifantakis.networkapp.features.jokes.domain.JokesRepository
import eu.anifantakis.networkapp.features.jokes.domain.datasource.LocalJokesDataSource
import eu.anifantakis.networkapp.features.jokes.domain.datasource.RemoteJokesDataSource
import eu.anifantakis.networkapp.features.jokes.presentation.screens.joke_details.JokesDetailsViewModel
import eu.anifantakis.networkapp.features.jokes.presentation.screens.jokes_list.JokesListViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.new
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { KtorClient.httpClient }
    single {
        Room.databaseBuilder(
            androidContext(),
            JokesDatabase::class.java,
            "jokes_database"
        )
            .addMigrations(MIGRATION_1_2)
            .build()
    }
    single { get<JokesDatabase>().jokesDao() }
    single { KSafe(androidContext()) }

    // Koin Compiler Plugin DSL: `new(::Impl)` resolves all constructor
    // parameters via `get()` at compile time, and `single<Iface>` declares
    // the binding type — replacing `singleOf(::Impl) bind Iface::class`.
    single<LocalJokesDataSource> { new(::LocalJokesDataSourceImpl) }
    single<RemoteJokesDataSource> { new(::RemoteJokesDataSourceImpl) }
    factory<JokesRepository> { new(::JokesRepositoryImpl) }

    viewModel { new(::JokesListViewModel) }
    viewModel { parameters -> JokesDetailsViewModel(joke = parameters.get(), repository = get()) }
}
