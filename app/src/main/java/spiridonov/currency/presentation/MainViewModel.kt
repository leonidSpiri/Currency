package spiridonov.currency.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import spiridonov.currency.data.repository.CurrListRepositoryImpl
import spiridonov.currency.domain.CurrItem
import spiridonov.currency.domain.EditCurrItemUseCase
import spiridonov.currency.domain.GetCurrListUseCase
import spiridonov.currency.domain.LoadDataUseCase

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CurrListRepositoryImpl(application)
    private val getCurrListUseCase = GetCurrListUseCase(repository)
    private val editCurrItemUseCase = EditCurrItemUseCase(repository)
    private val loadDataUseCase = LoadDataUseCase(repository)

    val currList = getCurrListUseCase()


    val spinnerOneSelectedPosition = MutableLiveData<String>()
    val editTextCurr = MutableLiveData<String>()
    val spinnerTwoSelectedPosition = MutableLiveData<String>()
    private val _convertValute = MutableLiveData<String>()
    val convertValute: LiveData<String>
        get() = _convertValute

    fun convertCurrency() {
        val valueFirstSpinner = spinnerOneSelectedPosition.value ?: ""
        val valueSecondSpinner = spinnerTwoSelectedPosition.value ?: ""
        val valueEditText = editTextCurr.value ?: ""
        if (valueFirstSpinner != "" && valueSecondSpinner != "" && valueEditText != "") {
            val firstValute = getValuteValueFromString(valueFirstSpinner)
            val secondValute = getValuteValueFromString(valueSecondSpinner)
            val value = valueEditText.toDouble()
            val result = value * firstValute / secondValute
            _convertValute.value = result.toString()
        }
    }


    fun changeStarValue(currItem: CurrItem) {
        viewModelScope.launch {
            val newItem = currItem.copy(star = !currItem.star)
            editCurrItemUseCase(newItem)
        }
    }

    fun refreshData() = loadDataUseCase()

    private fun getValuteValueFromString(string: String): Double {
        currList.value?.forEach {
            if (it.name == string) {
                return it.value.toDouble()
            }
        }
        return 1.0
    }

    init {
        refreshData()
    }
}