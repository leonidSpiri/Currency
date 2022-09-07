package spiridonov.currency.presentation

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import spiridonov.currency.CurrencyApp
import spiridonov.currency.R
import spiridonov.currency.databinding.ActivityMainBinding
import spiridonov.currency.presentation.adapter.CurrInfoAdapter
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    private var currListSpinner = mutableListOf("Российский рубль")
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var viewModel: MainViewModel

    private val component by lazy {
        (application as CurrencyApp).component
    }

    private lateinit var currInfoAdapter: CurrInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupRecycleView()
        viewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        observeViewModels()
        setupRefreshListener()
        inputTextCurrListener()
        setupStarClickListener()
        setupCurrItemClickListener()
    }

    private fun observeViewModels() {
        viewModel.currList.observe(this) {
            currListSpinner = mutableListOf(RUB_CURR)
            currInfoAdapter.submitList(it)
            it.forEach { value ->
                currListSpinner.add(value.name)
            }
            setupSpinnerListener()
        }
        viewModel.convertValute.observe(this) {
            binding.txtCurr.text = it.toString()
        }
    }

    private fun setupRecycleView() {
        binding.rvCurrList.setHasFixedSize(true)
        val linearLayoutManager = LinearLayoutManager(this)
        binding.rvCurrList.layoutManager = linearLayoutManager
        currInfoAdapter = CurrInfoAdapter()
        binding.rvCurrList.adapter = currInfoAdapter
    }

    private fun setupRefreshListener() {
        binding.pullToRefresh.setOnRefreshListener {
            viewModel.refreshData()
            binding.pullToRefresh.isRefreshing = false
        }
    }

    private fun setupCurrItemClickListener() {
        currInfoAdapter.onCurrItemClickListener = {
            if (isOnePaneMode())
                startActivity(CurrDetailActivity.newIntent(this@MainActivity, it.code))
            else
                launchFragment(CurrDetailFragment.newInstance(it.code))
        }
    }

    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.curr_item_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun setupStarClickListener() {
        currInfoAdapter.onCurrItemStarClickListener = {
            viewModel.changeStarValue(it)
        }
    }

    private fun inputTextCurrListener() {
        binding.txtEnter.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                viewModel.editTextCurr.value = s.toString()
                viewModel.getDataForConvertor()
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

            }
        })
    }

    private fun setupSpinnerListener() {
        binding.spinner1.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currListSpinner)
        binding.spinner2.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currListSpinner)
        binding.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.spinnerOneSelectedPosition.value = currListSpinner[p2]
                viewModel.getDataForConvertor()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
        binding.spinner2.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.spinnerTwoSelectedPosition.value = currListSpinner[p2]
                viewModel.getDataForConvertor()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }
    }

    private fun isOnePaneMode(): Boolean = binding.currItemContainer == null

    companion object {
        private const val RUB_CURR = "Российский рубль"
    }
}