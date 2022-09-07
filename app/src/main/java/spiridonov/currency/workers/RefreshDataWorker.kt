package spiridonov.currency.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import kotlinx.coroutines.delay
import spiridonov.currency.data.database.CurrListDao
import spiridonov.currency.data.mapper.CurrListMapper
import java.io.BufferedInputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class RefreshDataWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val currListDao: CurrListDao,
    private val mapper: CurrListMapper
) : CoroutineWorker
    (context, workerParameters) {

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
            delay(ONE_HOUR_IN_MILLIS)
        }
    }

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
        private const val ONE_HOUR_IN_MILLIS = 1000 * 60 * 60L
        private val URL = URL("https://www.cbr-xml-daily.ru/daily_json.js")

        fun makeRequest() =
            OneTimeWorkRequestBuilder<RefreshDataWorker>().build()
    }

    class Factory @Inject constructor(
        private val currListDao: CurrListDao,
        private val mapper: CurrListMapper
    ) : ChildWorkerFactory {
        override fun create(
            context: Context,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return RefreshDataWorker(context, workerParameters, currListDao, mapper)
        }
    }
}