package spiridonov.currency.domain

import androidx.lifecycle.LiveData

interface CurrListRepository {

    suspend fun addCurrItem(currItem: CurrItem)

    suspend fun deleteCurrItem(currItem: CurrItem)

    suspend fun editCurrItem(currItem: CurrItem)

    suspend fun getCurrItem(CurrItemId: Int): CurrItem

    fun getCurrList(): LiveData<List<CurrItem>>

    fun loadData()
}