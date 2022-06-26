package spiridonov.currency.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import spiridonov.currency.data.database.AppDatabase
import spiridonov.currency.data.mapper.CurrListMapper
import spiridonov.currency.data.repository.CurrListRepositoryImpl
import spiridonov.currency.domain.GetCurrListUseCase
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

class RefreshDataWorker(context: Context, workerParameters: WorkerParameters) : CoroutineWorker
    (context, workerParameters) {
    private val currListDao = AppDatabase.getInstance(context).currListDao()
    private val mapper = CurrListMapper()
    private val repository = CurrListRepositoryImpl(context)
    private val getCurrListUseCase = GetCurrListUseCase(repository)

    override suspend fun doWork(): Result {
        while (true) {
            try {
                //TODO("Do something with this errors (suspend functions && getCurrListUseCase)")
                val url = URL(URL)
                val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                val scanner = BufferedInputStream(conn.inputStream)
                val currJsonObject = mapper.mapInputStreamToJsonObject(scanner)
                val currList = getCurrListUseCase()
                val jsonObject = currJsonObject.getJSONObject(JSON_OBJECT_KEY)
                val currListDbModel = mapper.mapDtoToListDbModel(jsonObject, currList)
                currListDao.insertCurrList(currListDbModel)

            } catch (e: Exception) {
            }
            delay(HALF_DAY_IN_MILLIS)
        }
    }

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
        private const val HALF_DAY_IN_MILLIS = 1000 * 60 * 60 * 12L
        private const val URL = "https://www.cbr-xml-daily.ru/daily_json.js"
        private const val JSON_OBJECT_KEY = "Valute"

        fun makeRequest() =
            OneTimeWorkRequestBuilder<RefreshDataWorker>().setConstraints(makeConstraints()).build()

        private fun makeConstraints() =
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
    }
}