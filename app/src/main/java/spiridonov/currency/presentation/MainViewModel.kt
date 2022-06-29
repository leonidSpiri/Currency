package spiridonov.currency.presentation

import android.app.Application
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import spiridonov.currency.data.repository.CurrListRepositoryImpl
import spiridonov.currency.domain.*
import java.text.DecimalFormat

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CurrListRepositoryImpl(application)
    private val getCurrListUseCase = GetCurrListUseCase(repository)
    private val getCurrItemUseCase = GetCurrItemUseCase(repository)
    private val editCurrItemUseCase = EditCurrItemUseCase(repository)
    private val loadDataUseCase = LoadDataUseCase(repository)

    val currList = getCurrListUseCase()

    val spinnerOneSelectedPosition = MutableLiveData<String>()
    val editTextCurr = MutableLiveData<String>()
    val spinnerTwoSelectedPosition = MutableLiveData<String>()
    private val _convertValute = MutableLiveData<String>()
    val convertValute: LiveData<String>
        get() = _convertValute

    fun getDetailCurrInfo(name: String) = getCurrItemUseCase(name)

    fun refreshData() = loadDataUseCase()

    fun getDataForConvertor() {
        val valueFirstSpinner = spinnerOneSelectedPosition.value ?: ""
        val valueSecondSpinner = spinnerTwoSelectedPosition.value ?: ""
        val valueEditText = editTextCurr.value ?: ""
        if (valueFirstSpinner != "" && valueSecondSpinner != "" && valueEditText != "")
            _convertValute.value = convertCurr(valueFirstSpinner, valueSecondSpinner, valueEditText)

    }

    private fun convertCurr(
        firstCurr: String,
        secondCurr: String,
        valueEditText: String
    ): String {
        val firstValute = getValuteValueFromString(firstCurr)
        val secondValute = getValuteValueFromString(secondCurr)
        val value = valueEditText.toDouble()
        val result = value * firstValute / secondValute
        return DECIMAL_VALUE_FORMAT.format(result)
    }

    fun changeStarValue(currItem: CurrItem) =
        viewModelScope.launch {
            val newItem = currItem.copy(star = !currItem.star)
            editCurrItemUseCase(newItem)
        }

    private fun getValuteValueFromString(string: String): Double {
        currList.value?.forEach {
            if (it.name == string) {
                return it.value.toDouble()
            }
        }
        return 1.0
    }

    fun getColorByState(value: Double) = ContextCompat.getColor(
        getApplication(), if (value >= 0) android.R.color.holo_green_light
        else android.R.color.holo_red_light
    )

    init {
        refreshData()
    }
    companion object{
           private val DECIMAL_VALUE_FORMAT = DecimalFormat("#.###")
    }
}