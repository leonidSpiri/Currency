package spiridonov.currency

import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var pDialog: ProgressDialog
    private lateinit var msp: SharedPreferences
    private val url = "https://www.cbr-xml-daily.ru/daily_json.js"
    private var needToUpdate = false
    private lateinit var listToConvert: HashMap<String, Double>
    private var firstCurr = 0.0
    private var scndCurr = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        msp = getSharedPreferences("AppMemory", Context.MODE_PRIVATE)
        listToConvert = hashMapOf()
        var currString = ""
        if (msp.contains(KEY_CURRENCY)) currString = msp.getString(KEY_CURRENCY, "").toString()
        if (needToUpdate || currString.isEmpty()) GetCurrency().execute()
        else parsingData(jsonString = currString)
        val listNameCurr: MutableList<String> = listToConvert.keys.toMutableList()
        listNameCurr.add("Российский рубль")
        val adapterCurrency: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listNameCurr)

        adapterCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = adapterCurrency
        spinner2.adapter = adapterCurrency

        spinner1.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                firstCurr = listToConvert.getOrDefault(listNameCurr[p2], -1.0)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        spinner2.onItemSelectedListener= object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                scndCurr = listToConvert.getOrDefault(listNameCurr[p2], -1.0)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun convertCurr(){
        /*Если РУБ первый, то ввод / на value второго
        Если РУБ второй, то ввод * на value первого
        Если не РУБ, то (ввод * value первого) / на value второго*/
        if (firstCurr != 0.0 && scndCurr !=0.0){
            if (firstCurr == -1.0){

            }
            if (scndCurr == -1.0){

            }
        }
    }

    private fun parsingData(jsonString: String) {
        val currList: ArrayList<HashMap<String, String>> = ArrayList()
        try {
            val jsonObjects = JSONObject(jsonString)
            val valute = jsonObjects.getJSONObject("Valute")
            val allKeys = valute.keys()

            while (allKeys.hasNext()) {
                val myObj = valute.getJSONObject(allKeys.next())
                val currencyMap = HashMap<String, String>()
                val name = myObj.getString("Name")
                var value = myObj.getDouble("Value")
                val nominal = myObj.getDouble("Nominal")
                value /= nominal
                currencyMap["code"] = myObj.getString("CharCode")
                currencyMap["name"] = name
                currencyMap["value"] = value.toString()
                currencyMap["previous"] = myObj.getString("Previous")
                currList.add(currencyMap)
                listToConvert[name] = value
            }
            Log.d("parsingData", "parsingData")
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
        showCurrency(dataList = currList)
    }


    private fun showCurrency(dataList: ArrayList<HashMap<String, String>>) {
        val adapter: ListAdapter = SimpleAdapter(
            this@MainActivity,
            dataList,
            R.layout.each_currency,
            arrayOf("code", "name", "value", "previous"),
            intArrayOf(R.id.txtID, R.id.txtName, R.id.txtValue, R.id.txtPrevious)
        )
        currencyList.adapter = adapter
    }


    inner class GetCurrency : AsyncTask<Void?, Void?, Void?>() {
        private var jsonString = ""
        override fun onPreExecute() {
            super.onPreExecute()
            pDialog = ProgressDialog(this@MainActivity)
            pDialog.setMessage("Базы обновляются...")
            pDialog.show()
        }

        override fun doInBackground(vararg p0: Void?): Void? {
            Log.d("doInBackground", "download currency")
            val downloadJSON = DownloadJSON()
            jsonString = downloadJSON.connection(url)
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            parsingData(jsonString)
            val editor = msp.edit()
            editor.putString(KEY_CURRENCY, jsonString)
            editor.apply()
            if (pDialog.isShowing) pDialog.dismiss();
        }
    }

    companion object {
        const val KEY_CURRENCY = "currency"
    }
}

