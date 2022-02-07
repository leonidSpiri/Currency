package spiridonov.currency

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private val pDialog: ProgressDialog? = null
    private lateinit var listView: ListView
    private val url = "https://www.cbr-xml-daily.ru/daily_json.js"
    lateinit var contactList: ArrayList<HashMap<String, String>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        contactList = ArrayList()
        listView = currencyList

    }

    private class GetCurrency : AsyncTask<Void?, Void?, Void?>(){
        override fun onPreExecute() {
            super.onPreExecute()
            // Showing progress dialog
        }

        override fun doInBackground(vararg p0: Void?): Void? {
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)

        }
    }
}

