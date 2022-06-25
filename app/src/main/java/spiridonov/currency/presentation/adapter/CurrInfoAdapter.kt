package spiridonov.currency.presentation.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import spiridonov.currency.R
import spiridonov.currency.databinding.EachCurrencyBinding
import spiridonov.currency.domain.CurrInfo

// класс адаптера для списка валют
class CurrInfoAdapter(private val context: Context) :
    ListAdapter<CurrInfo, CurrInfoViewHolder>(CurrInfoDiffCallback) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrInfoViewHolder {
        val binding = EachCurrencyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CurrInfoViewHolder(binding)
    }


    override fun onBindViewHolder(holder: CurrInfoViewHolder, position: Int) {
        val currency = getItem(position)
        with(holder.binding) {
            with(currency) {
                txtName.text = name
                txtID.text = code
                txtValue.text = context.resources.getString(R.string.value, value)
                txtPrevious.text = context.resources.getString(R.string.previous_value, previous)
                imgStar.setImageResource(R.drawable.star_no_rate)

            }
        }
        val msp = context.getSharedPreferences("AppMemory", Context.MODE_PRIVATE)
        // добавление и удаление избранных пользователем валют
        var favouriteList = arrayListOf<String>()
        if (msp.contains(KEY_FAVOURITE) && msp.getString(KEY_FAVOURITE, " , ") != "")
            favouriteList = msp.getString(KEY_FAVOURITE, "")?.split(",") as ArrayList<String>
        if (favouriteList.contains(currency.code)) {
            holder.binding.imgStar.setImageResource(R.drawable.star_rate)
        }

        holder.binding.imgStar.setOnClickListener {
            if (msp.contains(KEY_FAVOURITE) && msp.getString(KEY_FAVOURITE, " , ") != "")
                favouriteList = msp.getString(KEY_FAVOURITE, "")?.split(",") as ArrayList<String>

            if (favouriteList.contains(currency.code)) {
                favouriteList.remove(currency.code)
                holder.binding.imgStar.setImageResource(R.drawable.star_no_rate)
            } else {
                favouriteList.add(currency.code)
                holder.binding.imgStar.setImageResource(R.drawable.star_rate)
            }
            var buff = ""
            favouriteList.forEach {
                if (it != "") buff += "$it,"
            }
            val editor = msp.edit()
            editor.putString(KEY_FAVOURITE, buff)
            editor.apply()

        }
    }

    companion object {
        const val KEY_FAVOURITE = "favourite"
    }
}