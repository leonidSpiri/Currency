package spiridonov.currency.data.repository

import android.content.Context
import androidx.lifecycle.Transformations
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import spiridonov.currency.data.database.AppDatabase
import spiridonov.currency.data.mapper.CurrListMapper
import spiridonov.currency.domain.CurrItem
import spiridonov.currency.domain.CurrListRepository
import spiridonov.currency.workers.RefreshDataWorker

class CurrListRepositoryImpl(
   private val context: Context
) : CurrListRepository {
    private val currListDao = AppDatabase.getInstance(context).currListDao()
    private val mapper = CurrListMapper()
    override suspend fun addCurrItem(currItem: CurrItem) {
        currListDao.addCurrItem(mapper.mapEntityToDbModel(currItem))
    }

    override suspend fun deleteCurrItem(currItem: CurrItem) {
        currListDao.deleteCurrItem(currItem.code)
    }

    override suspend fun editCurrItem(currItem: CurrItem) {
        currListDao.addCurrItem(mapper.mapEntityToDbModel(currItem))
    }


    override suspend fun getCurrItem(CurrItemCode: String): CurrItem {
        val dbModel = currListDao.getCurrItem(CurrItemCode)
        return mapper.mapDbModelToEntity(dbModel)
    }

    override fun getCurrList() = Transformations.map(
        currListDao.getCurrList()
    ) {
        mapper.mapListDbModelToListEntity(it)
    }

    override fun loadData() {
        val workManager = WorkManager.getInstance(context)
        workManager.enqueueUniqueWork(
            RefreshDataWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            RefreshDataWorker.makeRequest()
        )
    }

}