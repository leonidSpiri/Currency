package spiridonov.currency.presentation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import spiridonov.currency.R
import spiridonov.currency.databinding.ActivityCurrDetailBinding

class CurrDetailActivity : AppCompatActivity() {

    private val binding by lazy{
        ActivityCurrDetailBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        Log.d("CurrDetailActivity", "onCreate")
        if (!intent.hasExtra(ARG_PARAM_ITEM))
            finish()
        val currCode = intent.getStringExtra(ARG_PARAM_ITEM) ?: EMPTY_STRING
        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CurrDetailFragment.newInstance(currCode))
                .commit()
    }



    companion object {
        private const val ARG_PARAM_ITEM = "curr_name"
        private const val EMPTY_STRING = ""
        fun newIntent(context: Context, currCode: String):Intent {
            val intent = Intent(context, CurrDetailActivity::class.java)
            intent.putExtra(ARG_PARAM_ITEM, currCode)
            return intent
        }
    }
}