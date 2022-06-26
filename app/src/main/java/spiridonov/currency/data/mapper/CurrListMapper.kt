package spiridonov.currency.data.mapper

import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import spiridonov.currency.data.database.CurrItemDbModel
import spiridonov.currency.domain.CurrItem
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class CurrListMapper {
    fun mapEntityToDbModel(entity: CurrItem) = CurrItemDbModel(
        code = entity.code,
        name = entity.name,
        value = entity.value,
        previous = entity.previous,
        star = entity.star
    )

    fun mapDbModelToEntity(currItemDbModel: CurrItemDbModel) = CurrItem(
        code = currItemDbModel.code,
        name = currItemDbModel.name,
        value = currItemDbModel.value,
        previous = currItemDbModel.previous,
        star = currItemDbModel.star
    )

    fun mapListDbModelToListEntity(list: List<CurrItemDbModel>) = list.map {
        mapDbModelToEntity(it)
    }

    fun mapInputStreamToJsonObject(stream: BufferedInputStream): JSONObject {
        val reader = BufferedReader(InputStreamReader(stream))
        val sb = StringBuilder()
        var line = reader.readLine()
        try {
            while (line != null) {
                sb.append(line).append('\n')
                line = reader.readLine()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            stream.close()
        }
        return JSONObject(sb.toString())
    }

    fun mapDtoToListDbModel(dto: JSONObject, oldCurrList: LiveData<List<CurrItem>>): List<CurrItemDbModel> {
        val currList = ArrayList<CurrItemDbModel>()
        CoroutineScope(Dispatchers.Default).launch {
            val allKeys = dto.keys()
            allKeys.forEach { key ->
                val myObj = dto.getJSONObject(key)
                val name = myObj.getString("Name")
                val code = myObj.getString("CharCode")
                val nominal = myObj.getDouble("Nominal")
                val value = myObj.getDouble("Value") / nominal
                val previous = myObj.getDouble("Previous") / nominal
                var star = false
                oldCurrList.value?.forEach {
                    if (it.code == code) {
                        star = it.star == true
                    }
                }
                currList.add(
                    CurrItemDbModel(
                        code = code,
                        name = name,
                        value = String.format("%.2f", value),
                        previous = String.format("%.2f", previous),
                        star = star
                    )
                )
            }

        }
        return currList
    }
}