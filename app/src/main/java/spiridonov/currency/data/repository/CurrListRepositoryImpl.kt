package spiridonov.currency.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import spiridonov.currency.data.database.CurrListDao
import spiridonov.currency.data.mapper.CurrListMapper
import spiridonov.currency.domain.CurrItem
import spiridonov.currency.domain.CurrListRepository
import spiridonov.currency.workers.RefreshDataWorker
import javax.inject.Inject

class CurrListRepositoryImpl @Inject constructor(
    private val application: Application,
    private val currListDao: CurrListDao,
    private val mapper: CurrListMapper
) : CurrListRepository {

    override suspend fun editCurrItem(currItem: CurrItem) {
        currListDao.addCurrItem(mapper.mapEntityToDbModel(currItem))
    }

    override fun getCurrItem(CurrItemCode: String): LiveData<CurrItem> {
        return Transformations.map(currListDao.getCurrItemDb(CurrItemCode)) {
            mapper.mapDbModelToEntity(it ?: throw RuntimeException("CurrItemDbModel was not found"))
        }
    }

    override fun getCurrList() = Transformations.map(
        currListDao.getCurrListLiveData()
    ) {
        mapper.mapListDbModelToListEntity(it)
    }

    override fun loadData() {
        WorkManager.getInstance(application)
            .enqueueUniqueWork(
                RefreshDataWorker.WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                RefreshDataWorker.makeRequest()
            )
    }
}