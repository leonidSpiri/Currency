package spiridonov.currency.presentation

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import org.json.JSONException
import org.json.JSONObject
import spiridonov.currency.data.network.DownloadJSON
import spiridonov.currency.data.repository.CurrListRepositoryImpl
import spiridonov.currency.databinding.ActivityMainBinding
import spiridonov.currency.domain.CurrItem
import spiridonov.currency.domain.LoadDataUseCase
import spiridonov.currency.presentation.adapter.CurrInfoAdapter
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var pDialog: ProgressDialog // прогресс скачки базы данных
    private lateinit var msp: SharedPreferences // SharedPreferences для сохрание данных в памяти
    private val url = "https://www.cbr-xml-daily.ru/daily_json.js" // ссылка для скачивания json
    private lateinit var nameValueList: HashMap<String, Double> // HashMap для хранение пары название валюты - ее цена
    private lateinit var nameCodeList: HashMap<String, String> // HashMap для хранение пары название валюты - ее кода
    private lateinit var listForSpiner: MutableList<String> // динамический массив для хранения названий валют. используется для списка
    private lateinit var arrayNameValue: Array<Array<String?>> // двумерный массив для хранения имени и цены выбранной пользователем валюты
    private lateinit var lastUpdate: Calendar
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        val repository = CurrListRepositoryImpl(application)
        val loadDataUseCase = LoadDataUseCase(repository)
        loadDataUseCase()
        arrayNameValue = Array(2) { arrayOfNulls(2) }
        msp = getSharedPreferences("AppMemory", Context.MODE_PRIVATE)
        nameValueList = hashMapOf()
        nameCodeList = hashMapOf()
        listForSpiner = mutableListOf()
        var currString = ""
        if (msp.contains(KEY_CURRENCY)) currString =
            msp.getString(KEY_CURRENCY, "").toString() // считывание данных из памяти (при наличие)
        if (currString.isEmpty()) GetCurrency().execute() // если пермененная пустая, то скачиваем данные с сайта
        else {
            parsingData(jsonString = currString) // иначе сразу обрабатываем данные из памяти
            checkForUpdates() // и проверяем нужно ли обновить данные
        }
        // два слушателя пользовательских списков. Записывают в массив выбранные пользователем данные. Вызывают функцию перевода валюты
        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                arrayNameValue[0][0] = listForSpiner[p2]
                arrayNameValue[0][1] = nameValueList.getOrDefault(listForSpiner[p2], 1.0).toString()
                convertCurr()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                arrayNameValue[1][0] = listForSpiner[p2]
                arrayNameValue[1][1] = nameValueList.getOrDefault(listForSpiner[p2], 1.0).toString()
                convertCurr()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        // слушатель текстового поля. Вызывают функцию перевода валюты
        binding.txtEnter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                convertCurr()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })

        // слушатель списка. если пользователь потянет вниз список, то базы обновятся
        binding.pullToRefresh.setOnRefreshListener {
            GetCurrency().execute()
            binding.pullToRefresh.isRefreshing = false
        }
    }

    // функция проверки обновления. Если прошло более одного дня с последнего обновления, пользователю будет предложено обновить базы.
    // При этом учитывается что в воскресенье и понедельник курс не обновляется
    private fun checkForUpdates() {
        var month = lastUpdate.get(Calendar.MONTH)
        month++
        val buffCalendar = lastUpdate.clone() as Calendar
        var timeOut = 24
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        if (day == Calendar.SUNDAY || day == Calendar.MONDAY) timeOut = 72
        buffCalendar.add(Calendar.HOUR_OF_DAY, timeOut)
        if (calendar.after(buffCalendar)) {
            AlertDialog.Builder(this)
                .setTitle("Базы устарели")
                .setMessage(
                    "Последнее обновление: ${lastUpdate.get(Calendar.DAY_OF_MONTH)}.$month.${
                        lastUpdate.get(
                            Calendar.YEAR
                        )
                    }\nОбновить базы?"
                )
                .setPositiveButton("Да") { _, _ -> GetCurrency().execute() }
                .setNegativeButton("Отмена", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }

    // функция подсчета конвертации валюты. Берутся данные из массива и текстового поля
    private fun convertCurr() {
        val enter = binding.txtEnter.text.toString()
        val firstNumb = arrayNameValue[0][1]?.toDouble()
        val scndNumb = arrayNameValue[1][1]?.toDouble()
        val firstValute = nameCodeList[arrayNameValue[0][0].toString()].toString()
        val scndValute = nameCodeList[arrayNameValue[1][0].toString()].toString()
        if (firstNumb != null && scndNumb != null && firstNumb != 0.0 && scndNumb != 0.0 && enter.isNotEmpty() && enter.toDouble() > 0.0) {
            var enterSum = enter.toDouble()
            enterSum = (enterSum * firstNumb) / scndNumb
            val str = "$enter $firstValute → ${String.format("%.2f", enterSum)} $scndValute"
            binding.txtCurr.text = str
        }
    }

    //обработка строки входных данных.
    private fun parsingData(jsonString: String) {
        val currList: ArrayList<CurrItem> = ArrayList()
        var currFavourite = arrayListOf<String>()
        if (msp.contains(CurrInfoAdapter.KEY_FAVOURITE) && msp.getString(
                CurrInfoAdapter.KEY_FAVOURITE,
                ""
            ) != ""
        )
            currFavourite =
                msp.getString(CurrInfoAdapter.KEY_FAVOURITE, " , ")?.split(",") as ArrayList<String>
        try {
            val jsonObjects = JSONObject(jsonString)
            lastUpdate = Calendar.getInstance()
            lastUpdate.time =
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(jsonObjects.getString("Date"))
            val valute = jsonObjects.getJSONObject("Valute")
            val allKeys = valute.keys()
            listForSpiner.clear()
            listForSpiner.add("Российский рубль")
            // сначала в списке идут избранные пользователем валюты
            currFavourite.forEach {
                if (it != "")
                    currList.add(parsing(valute = valute, it = it))
            }

            // после этого все оставшиеся
            while (allKeys.hasNext()) {
                val nextKey = allKeys.next()
                if (currFavourite.contains(nextKey)) continue
                currList.add(parsing(valute = valute, it = nextKey))
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

        val adapterCurrency: ArrayAdapter<String> =
            ArrayAdapter<String>(
                this,
                androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,
                listForSpiner
            )

        adapterCurrency.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinner1.adapter = adapterCurrency
        binding.spinner2.adapter = adapterCurrency
        showCurrency(dataList = currList)
    }

    // функция обработки каждой отдельной валюты
    private fun parsing(valute: JSONObject, it: String): CurrItem {
        val myObj = valute.getJSONObject(it)
        val name = myObj.getString("Name")
        val code = myObj.getString("CharCode")
        val nominal = myObj.getDouble("Nominal")
        val value = myObj.getDouble("Value") / nominal
        val previous = myObj.getDouble("Previous") / nominal
        var star = true
        val msp = getSharedPreferences("AppMemory", Context.MODE_PRIVATE)

        if (msp.contains(CurrInfoAdapter.KEY_FAVOURITE) && msp.getString(
                CurrInfoAdapter.KEY_FAVOURITE, " , "
            ) != ""
        ) {
            var favouriteList = msp.getString(CurrInfoAdapter.KEY_FAVOURITE, "")
                ?.split(",") as ArrayList<String>

            if (!favouriteList.contains(code))
                star = false
        }
        val currencyMap = CurrItem(
            code = code,
            name = name,
            value = String.format("%.2f", value),
            previous = String.format("%.2f", previous),
            star = star
        )

        nameValueList[name] = value
        nameCodeList[name] = code
        listForSpiner.add(name)
        return currencyMap
    }


    // функция генерации списка валют и вывода его на экран
    private fun showCurrency(dataList: ArrayList<CurrItem>) {
        binding.recycleView.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        binding.recycleView.layoutManager = linearLayoutManager
        val adapter = CurrInfoAdapter()
        binding.recycleView.adapter = adapter
        adapter.submitList(dataList)
        adapter.onCurrItemClickListener = {
            val msp = getSharedPreferences("AppMemory", Context.MODE_PRIVATE)
            var favouriteList = arrayListOf<String>()
            Log.d("Currency", "Star clicked: ${it.name}")
            if (msp.contains(CurrInfoAdapter.KEY_FAVOURITE) && msp.getString(
                    CurrInfoAdapter.KEY_FAVOURITE, " , "
                ) != ""
            )
                favouriteList = msp.getString(CurrInfoAdapter.KEY_FAVOURITE, "")
                    ?.split(",") as ArrayList<String>

            if (favouriteList.contains(it.code))
                favouriteList.remove(it.code)
            else
                favouriteList.add(it.code)

            var buff = ""
            favouriteList.forEach {
                if (it != "") buff += "$it,"
            }
            val editor = msp.edit()
            editor.putString(CurrInfoAdapter.KEY_FAVOURITE, buff)
            editor.apply()
            GetCurrency().execute()
            binding.pullToRefresh.isRefreshing = false
        }

        binding.txtEnter.setText("")
        binding.txtCurr.text = ""
    }


    // асинхронный класс для скачивания JSON данных
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

    // константа для SharedPreferences
    companion object {
        const val KEY_CURRENCY = "currency"
    }
}