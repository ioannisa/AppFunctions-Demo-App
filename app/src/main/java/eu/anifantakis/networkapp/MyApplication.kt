package eu.anifantakis.networkapp

import android.app.Application
import androidx.appfunctions.service.AppFunctionConfiguration
import eu.anifantakis.networkapp.di.appModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication: Application(), AppFunctionConfiguration.Provider {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            modules(appModule)
        }
    }

    override val appFunctionConfiguration: AppFunctionConfiguration by inject()
}