package spiridonov.currency.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import spiridonov.currency.databinding.ActivityMainBinding
import spiridonov.currency.presentation.adapter.CurrInfoAdapter


class MainActivity : AppCompatActivity() {
    private lateinit var nameValueList: HashMap<String, Double> // HashMap для хранение пары название валюты - ее цена
    private lateinit var nameCodeList: HashMap<String, String> // HashMap для хранение пары название валюты - ее кода
    private lateinit var listForSpinner: MutableList<String> // динамический массив для хранения названий валют. используется для списка
    private lateinit var arrayNameValue: Array<Array<String?>> // двумерный массив для хранения имени и цены выбранной пользователем валюты


    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var viewModel: MainViewModel
    private lateinit var currInfoAdapter: CurrInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecycleView()
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        viewModel.currList.observe(this) {
            currInfoAdapter.submitList(it)
        }


        setupRefreshListener()
        //TODO("make currency converter")
        /*
         arrayNameValue = Array(2) { arrayOfNulls(2) }
         msp = getSharedPreferences("AppMemory", Context.MODE_PRIVATE)
         nameValueList = hashMapOf()
         nameCodeList = hashMapOf()
         listForSpiner = mutableListOf()
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
         })*/
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


    private fun setupRecycleView() {
        binding.rvCurrList.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvCurrList.layoutManager = linearLayoutManager
        currInfoAdapter = CurrInfoAdapter()
        binding.rvCurrList.adapter = currInfoAdapter
        setupClickListener()
    }

    private fun setupRefreshListener() {
        binding.pullToRefresh.setOnRefreshListener {
            viewModel.refreshData()
            binding.pullToRefresh.isRefreshing = false
        }
    }

    private fun setupClickListener() {
        currInfoAdapter.onCurrItemClickListener = {
            viewModel.changeStarValue(it)
        }
    }
}