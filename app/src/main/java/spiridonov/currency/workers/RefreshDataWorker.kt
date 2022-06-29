package spiridonov.currency.workers

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import spiridonov.currency.data.database.AppDatabase
import spiridonov.currency.data.mapper.CurrListMapper
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL

class RefreshDataWorker(
    context: Context, workerParameters: WorkerParameters
) : CoroutineWorker
    (context, workerParameters) {
    private val currListDao = AppDatabase.getInstance(context).currListDao()
    private val mapper = CurrListMapper()

    override suspend fun doWork(): Result {
        while (true) {
            try {
                runCatching {
                    val conn: HttpURLConnection = URL.openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"
                    val scanner = BufferedInputStream(conn.inputStream)
                    val currJsonObject = mapper.mapInputStreamToJsonObject(scanner)
                    val oldCurrList = currListDao.getSuspendCurrList()
                    val currListDbModel = mapper.mapDtoToListDbModel(currJsonObject, oldCurrList)
                    currListDao.insertCurrList(currListDbModel)
                }

            } catch (e: Exception) {
            }
            delay(HALF_DAY_IN_MILLIS)
        }
    }

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
        private const val HALF_DAY_IN_MILLIS = 1000 * 60 * 60 * 12L
        private val URL = URL("https://www.cbr-xml-daily.ru/daily_json.js")


        fun makeRequest() =
            OneTimeWorkRequestBuilder<RefreshDataWorker>().setConstraints(makeConstraints()).build()

        private fun makeConstraints() =
            Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
    }
}