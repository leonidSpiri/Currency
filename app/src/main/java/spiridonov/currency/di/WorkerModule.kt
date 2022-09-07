package spiridonov.currency.di

import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import spiridonov.currency.workers.ChildWorkerFactory
import spiridonov.currency.workers.RefreshDataWorker

@Module
interface WorkerModule {
    @Binds
    @IntoMap
    @WorkerKey(RefreshDataWorker::class)
    fun bindRefreshDataWorkerFactory(worker: RefreshDataWorker.Factory): ChildWorkerFactory
}