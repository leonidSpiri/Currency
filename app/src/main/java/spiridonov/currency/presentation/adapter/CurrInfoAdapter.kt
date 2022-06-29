package spiridonov.currency.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.ListAdapter
import spiridonov.currency.R
import spiridonov.currency.databinding.EachCurrencyDisabledBinding
import spiridonov.currency.databinding.EachCurrencyEnabledBinding
import spiridonov.currency.domain.CurrItem
import spiridonov.currency.presentation.binding

class CurrInfoAdapter :
    ListAdapter<CurrItem, CurrItemViewHolder>(CurrInfoDiffCallback) {

    var onCurrItemStarClickListener: ((CurrItem) -> Unit)? = null
    var onCurrItemClickListener: ((CurrItem) -> Unit)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrItemViewHolder {
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
        return CurrItemViewHolder(binding)
    }

    override fun getItemViewType(position: Int) =
        if (getItem(position).star) CURR_STAR_ENABLED
        else CURR_STAR_DISABLED

    override fun onBindViewHolder(holder: CurrItemViewHolder, position: Int) {
        val currency = getItem(position)
        with(holder.binding) {
            when (this) {
                is EachCurrencyDisabledBinding -> {
                    currItem = currency
                    imgStar.setOnClickListener {
                        onCurrItemStarClickListener?.invoke(currency)
                    }
                }
                is EachCurrencyEnabledBinding -> {
                    currItem = currency
                    imgStar.setOnClickListener {
                        onCurrItemStarClickListener?.invoke(currency)
                    }
                }
            }
            root.setOnClickListener {
                onCurrItemClickListener?.invoke(currency)
            }
        }
    }

    companion object {
        private const val CURR_STAR_ENABLED = 1
        private const val CURR_STAR_DISABLED = 0
    }
}