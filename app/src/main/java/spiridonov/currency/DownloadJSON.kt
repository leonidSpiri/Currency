package spiridonov.currency

import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class DownloadJSON {
    fun connection(userURL: String): String {
        val responce: String
        val url = URL(userURL)
        val conn: HttpURLConnection = url.openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        val scanner = BufferedInputStream(conn.inputStream)
        responce = convertScanToString(scanner)
        return responce
    }

    private fun convertScanToString(stream: BufferedInputStream): String {
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

        return sb.toString()
    }
}