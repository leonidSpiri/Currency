package spiridonov.currency

import android.app.Application
import androidx.work.Configuration
import spiridonov.currency.di.DaggerApplicationComponent
import spiridonov.currency.workers.CurrWorkerFactory
import javax.inject.Inject

class CurrencyApp : Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: CurrWorkerFactory

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        component.inject(this)
        super.onCreate()
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(
                workerFactory
            )
            .build()
    }
}