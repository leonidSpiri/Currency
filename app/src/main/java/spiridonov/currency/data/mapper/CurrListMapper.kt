package spiridonov.currency.data.mapper

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
        star = entity.star,
        digitalCode = entity.digitalCode,
        date = entity.date
    )

    fun mapDbModelToEntity(currItemDbModel: CurrItemDbModel) = CurrItem(
        code = currItemDbModel.code,
        name = currItemDbModel.name,
        value = currItemDbModel.value,
        previous = currItemDbModel.previous,
        star = currItemDbModel.star,
        digitalCode = currItemDbModel.digitalCode,
        date = currItemDbModel.date
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
        } finally {
            stream.close()
        }
        return JSONObject(sb.toString())
    }

    fun mapDtoToListDbModel(
        dto: JSONObject,
        oldCurrList: List<CurrItemDbModel>
    ): List<CurrItemDbModel> {
        val currListDbModel = ArrayList<CurrItemDbModel>()
        var date = dto.getString(JSON_OBJECT_TIME_KEY)
        date = date.replace("T", " ").replace("+03:00", "")
        val valute = dto.getJSONObject(JSON_OBJECT_VALUTE_KEY)
        val jsonKeys = valute.keys()
        jsonKeys.forEach { key ->
            val myObj = valute.getJSONObject(key)
            val name = myObj.getString("Name")
            val code = myObj.getString("CharCode")
            val nominal = myObj.getDouble("Nominal")
            val value = myObj.getDouble("Value") / nominal
            val previous = myObj.getDouble("Previous") / nominal
            val digitalCode = myObj.getString("NumCode")
            var star = false
            oldCurrList.forEach {
                if (it.code == code) {
                    star = it.star
                }
            }
            currListDbModel.add(
                CurrItemDbModel(
                    code = code,
                    name = name,
                    value = stringFormat(value),
                    previous = stringFormat(previous),
                    star = star,
                    digitalCode = digitalCode,
                    date = date
                )
            )
        }
        return currListDbModel
    }

    private fun stringFormat(input:Double) = String.format("%.2f", input).replace(",", ".")

    companion object{
        private const val JSON_OBJECT_VALUTE_KEY = "Valute"
        private const val JSON_OBJECT_TIME_KEY = "Timestamp"
    }
}