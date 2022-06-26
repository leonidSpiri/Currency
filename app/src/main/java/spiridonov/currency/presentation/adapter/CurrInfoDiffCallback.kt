package spiridonov.currency.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import spiridonov.currency.domain.CurrInfo

object CurrInfoDiffCallback : DiffUtil.ItemCallback<CurrInfo>() {
    override fun areItemsTheSame(oldItem: CurrInfo, newItem: CurrInfo) =
        oldItem.code == newItem.code

    override fun areContentsTheSame(oldItem: CurrInfo, newItem: CurrInfo) = oldItem == newItem
}