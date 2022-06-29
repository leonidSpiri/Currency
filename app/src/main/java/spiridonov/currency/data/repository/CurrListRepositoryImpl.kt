package spiridonov.currency.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import spiridonov.currency.data.database.AppDatabase
import spiridonov.currency.data.mapper.CurrListMapper
import spiridonov.currency.domain.CurrItem
import spiridonov.currency.domain.CurrListRepository
import spiridonov.currency.workers.RefreshDataWorker
import java.lang.RuntimeException

class CurrListRepositoryImpl(
    private val context: Context
) : CurrListRepository {
    private val currListDao = AppDatabase.getInstance(context).currListDao()
    private val mapper = CurrListMapper()

    override suspend fun addCurrItem(currItem: CurrItem) {
        currListDao.addCurrItem(mapper.mapEntityToDbModel(currItem))
    }

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
        WorkManager.getInstance(context)
            .enqueueUniqueWork(
                RefreshDataWorker.WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                RefreshDataWorker.makeRequest()
            )
    }
}