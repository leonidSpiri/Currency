package spiridonov.currency.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import spiridonov.currency.data.repository.CurrListRepositoryImpl
import spiridonov.currency.domain.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CurrListRepositoryImpl(application)
    private val getCurrListUseCase = GetCurrListUseCase(repository)
    private val editCurrItemUseCase = EditCurrItemUseCase(repository)
    private val loadDataUseCase = LoadDataUseCase(repository)

    val currList = getCurrListUseCase()

    fun changeStarValue(currItem: CurrItem){
        viewModelScope.launch {
            val newItem = currItem.copy(star = !currItem.star)
            editCurrItemUseCase(newItem)
        }
    }

    fun refreshData() = loadDataUseCase()


    init {
        refreshData()
    }


}