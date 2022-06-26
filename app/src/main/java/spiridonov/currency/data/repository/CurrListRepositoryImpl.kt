package spiridonov.currency.data.repository

import android.app.Application
import androidx.lifecycle.Transformations
import spiridonov.currency.data.database.AppDatabase
import spiridonov.currency.data.mapper.CurrListMapper
import spiridonov.currency.domain.CurrItem
import spiridonov.currency.domain.CurrListRepository

class CurrListRepositoryImpl(
    application: Application
) : CurrListRepository {
    private val currListDao = AppDatabase.getInstance(application).currListDao()
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
        TODO("Not yet implemented")
    }

}