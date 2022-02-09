package spiridonov.currency

import android.app.AlertDialog
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
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var pDialog: ProgressDialog
    private lateinit var msp: SharedPreferences
    private val url =
        "https://www.cbr-xml-daily.ru/daily_json.js" //"https://www.cbr-xml-daily.ru//archive//2021//12//08//daily_json.js"
    private lateinit var nameValueList: HashMap<String, Double>
    private lateinit var nameCodeList: HashMap<String, String>
    private lateinit var listNameCurr: MutableList<String>
    private lateinit var listnameValue: Array<Array<String?>>
    private lateinit var lastUpdate: Calendar
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
        if (currString.isEmpty()) GetCurrency().execute()
        else {
            parsingData(jsonString = currString)
            checkForUpdates()
        }



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

        txtEnter.setOnClickListener { convertCurr() }

        pullToRefresh.setOnRefreshListener {
            GetCurrency().execute()
            pullToRefresh.isRefreshing = false
        }

    }

    private fun checkForUpdates() {
        val calNow = Calendar.getInstance()
        var month: Int = lastUpdate.get(Calendar.MONTH)
        month++
        calNow.time = Date()
        if (calNow.after(lastUpdate)) {
            AlertDialog.Builder(this)
                .setTitle("Базы устарели")
                .setMessage(
                    "Последнее обновление: ${lastUpdate.get(Calendar.DAY_OF_MONTH)}.$month.${
                        lastUpdate.get(
                            Calendar.YEAR
                        )
                    }Обновить базы?"
                )
                .setPositiveButton("Да") { _, _ -> GetCurrency().execute() }
                .setNegativeButton("Отмена", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
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
            enterSum = (enterSum * firstNumb) / scndNumb
            val str = "$enter $firstValute = ${String.format("%.4f", enterSum)} $scndValute"
            txtCurr.text = str
        }
    }

    private fun parsingData(jsonString: String) {
        val currList: ArrayList<HashMap<String, String>> = ArrayList()
        try {
            val jsonObjects = JSONObject(jsonString)
            lastUpdate = Calendar.getInstance()
            lastUpdate.time =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(jsonObjects.getString("Date"))
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
                currencyMap["value"] = String.format("%.4f", value)
                currencyMap["previous"] = myObj.getString("Previous")
                currList.add(currencyMap)
                nameValueList[name] = value
                nameCodeList[name] = code
            }
            nameCodeList["Российский рубль"] = "RUB"
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