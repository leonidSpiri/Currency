package spiridonov.currency.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import spiridonov.currency.domain.CurrItem
import spiridonov.currency.domain.CurrListRepository

class CurrListRepositoryImpl(
    application: Application
) :CurrListRepository{
    override suspend fun addCurrItem(currItem: CurrItem) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteCurrItem(currItem: CurrItem) {
        TODO("Not yet implemented")
    }

    override suspend fun editCurrItem(currItem: CurrItem) {
        TODO("Not yet implemented")
    }

    override suspend fun getCurrItem(CurrItemId: Int): CurrItem {
        TODO("Not yet implemented")
    }

    override fun getCurrList(): LiveData<List<CurrItem>> {
        TODO("Not yet implemented")
    }

    override fun loadData() {
        TODO("Not yet implemented")
    }

}