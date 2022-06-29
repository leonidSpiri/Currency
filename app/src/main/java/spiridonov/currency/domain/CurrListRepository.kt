package spiridonov.currency.domain

import androidx.lifecycle.LiveData


interface CurrListRepository {

    suspend fun editCurrItem(currItem: CurrItem)

    fun getCurrItem(CurrItemCode: String): LiveData<CurrItem>

    fun getCurrList(): LiveData<List<CurrItem>>

    fun loadData()
}