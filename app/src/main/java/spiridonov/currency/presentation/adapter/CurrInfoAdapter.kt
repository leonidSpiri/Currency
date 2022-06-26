package spiridonov.currency.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import spiridonov.currency.R
import spiridonov.currency.databinding.EachCurrencyDisabledBinding
import spiridonov.currency.databinding.EachCurrencyEnabledBinding
import spiridonov.currency.domain.CurrInfo

class CurrInfoAdapter :
    ListAdapter<CurrInfo, CurrInfoViewHolder>(CurrInfoDiffCallback) {

    var onCurrItemClickListener: ((CurrInfo) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrInfoViewHolder {
        val layoutID =
            when (viewType) {
                CURR_STAR_ENABLED -> R.layout.each_currency_enabled
                CURR_STAR_DISABLED -> R.layout.each_currency_disabled
                else -> throw RuntimeException("Unknown view type: $viewType")
            }
        val binding = DataBindingUtil.inflate<ViewDataBinding>(
            LayoutInflater.from(parent.context),
            layoutID,
            parent,
            false
        )
        return CurrInfoViewHolder(binding)
    }

    override fun getItemViewType(position: Int) =
        if (getItem(position).star) CURR_STAR_ENABLED
        else CURR_STAR_DISABLED

    override fun onBindViewHolder(holder: CurrInfoViewHolder, position: Int) {
        val currency = getItem(position)
        with(holder.binding) {
            when (this) {
                is EachCurrencyDisabledBinding -> {
                    currInfo = currency
                    imgStar.setOnClickListener {
                        onCurrItemClickListener?.invoke(currency)
                    }
                }
                is EachCurrencyEnabledBinding -> {
                    currInfo = currency
                    imgStar.setOnClickListener {
                        onCurrItemClickListener?.invoke(currency)
                    }
                }
            }
        }
    }

    companion object {
        const val KEY_FAVOURITE = "favourite"
        private const val CURR_STAR_ENABLED = 1
        private const val CURR_STAR_DISABLED = 0
    }
}