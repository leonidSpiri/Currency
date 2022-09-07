package spiridonov.currency.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import spiridonov.currency.CurrencyApp
import spiridonov.currency.presentation.CurrDetailFragment
import spiridonov.currency.presentation.MainActivity

@ApplicationScope
@Component(
    modules = [
        DataModule::class,
        ViewModelModule::class,
        WorkerModule::class
    ]
)
interface ApplicationComponent {
    fun inject(activity: MainActivity)
    fun inject(fragment: CurrDetailFragment)
    fun inject(application: CurrencyApp)

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance application: Application): ApplicationComponent
    }
}