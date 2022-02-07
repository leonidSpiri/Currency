package spiridonov.currency

import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() /*, CoroutineScope by MainScope()*/ {
    private lateinit var pDialog: ProgressDialog
    private lateinit var listView: ListView
    private val url = "https://www.cbr-xml-daily.ru/daily_json.js"
    lateinit var currList: ArrayList<HashMap<String, String>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        currList = ArrayList()
        listView = currencyList

        GetCurrency().execute()

    }


    inner class GetCurrency : AsyncTask<Void?, Void?, Void?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            pDialog = ProgressDialog(this@MainActivity)
            pDialog.setMessage("Базы обновляются...")
            pDialog.show()
        }

        override fun doInBackground(vararg p0: Void?): Void? {
            val downloadJSON = DownloadJSON()
            val JSONString = downloadJSON.connection(url)
            try {
                val jsonObjects = JSONObject(JSONString)
                val valute = jsonObjects.getJSONObject("Valute")
                val allKeys = valute.keys()

                for (i in 0 until valute.length()) {
                    Log.d("Valute ID", valute.getString(allKeys.next()).toString())

                }
            } catch (e: JSONException) {
                Log.e("JSONException", "Json parsing error: " + e.message.toString());
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "Json parsing error: " + e.message.toString(),
                        Toast.LENGTH_LONG
                    ).show()
                }

            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (pDialog.isShowing)
                pDialog.dismiss();
        }
    }
}

