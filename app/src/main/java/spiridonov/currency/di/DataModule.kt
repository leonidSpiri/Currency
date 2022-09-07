package spiridonov.currency.di

import android.app.Application
import dagger.Binds
import dagger.Module
import dagger.Provides
import spiridonov.currency.data.database.AppDatabase
import spiridonov.currency.data.database.CurrListDao
import spiridonov.currency.data.repository.CurrListRepositoryImpl
import spiridonov.currency.domain.CurrListRepository

@Module
interface DataModule {

    @Binds
    @ApplicationScope
    fun bindCurrencyRepository(impl: CurrListRepositoryImpl): CurrListRepository

    companion object{
        @Provides
        @ApplicationScope
        fun provideCurrListDao(
            application: Application
        ):CurrListDao{
            return AppDatabase.getInstance(application).currListDao()
        }
    }
}