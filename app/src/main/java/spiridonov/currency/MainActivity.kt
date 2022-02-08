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
    private var needToUpdate = true
    private lateinit var nameValueList: HashMap<String, Double>
    private lateinit var nameCodeList: HashMap<String, String>
    private lateinit var listNameCurr: MutableList<String>
    private lateinit var listnameValue: Array<Array<String?>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listnameValue = Array(2) { arrayOfNulls(2) }
        msp = getSharedPreferences("AppMemory", Context.MODE_PRIVATE)
        nameValueList = hashMapOf()
        nameCodeList = hashMapOf()
        listNameCurr = mutableListOf()
        var currString = ""
        if (msp.contains(KEY_CURRENCY)) currString = msp.getString(KEY_CURRENCY, "").toString()
        if (needToUpdate || currString.isEmpty()) GetCurrency().execute()
        else parsingData(jsonString = currString)


        spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                listnameValue[0][0] = listNameCurr[p2]
                listnameValue[0][1] = nameValueList.getOrDefault(listNameCurr[p2], 1.0).toString()
                convertCurr()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                listnameValue[1][0] = listNameCurr[p2]
                listnameValue[1][1] = nameValueList.getOrDefault(listNameCurr[p2], 1.0).toString()
                convertCurr()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun convertCurr() {
        val enter = txtEnter.text.toString()
        val firstNumb = listnameValue[0][1]?.toDouble()
        val scndNumb = listnameValue[1][1]?.toDouble()
        val firstValute = nameCodeList[listnameValue[0][0].toString()].toString()
        val scndValute = nameCodeList[listnameValue[1][0].toString()].toString()
        if (firstNumb != null && scndNumb != null && firstNumb != 0.0 && scndNumb != 0.0 && enter.isNotEmpty() && enter.toDouble() > 0.0) {
            var enterSum = enter.toDouble()
            when {
                firstNumb == -1.0 -> enterSum /= scndNumb
                scndNumb == -1.0 -> enterSum *= firstNumb
                else -> enterSum = (enterSum * firstNumb) / scndNumb
            }
            val str = "$enter $firstValute = ${String.format("%.3f", enterSum)} $scndValute"
            txtCurr.text = str
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
                val code = myObj.getString("CharCode")
                var value = myObj.getDouble("Value")
                val nominal = myObj.getDouble("Nominal")
                value /= nominal
                currencyMap["code"] = code
                currencyMap["name"] = name
                currencyMap["value"] = value.toString()
                currencyMap["previous"] = myObj.getString("Previous")
                currList.add(currencyMap)
                nameValueList[name] = value
                nameCodeList[name] = code
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
        listNameCurr = nameValueList.keys.toMutableList()
        listNameCurr.add("Российский рубль")
        val adapterCurrency: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, listNameCurr
            )

        adapterCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = adapterCurrency
        spinner2.adapter = adapterCurrency
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