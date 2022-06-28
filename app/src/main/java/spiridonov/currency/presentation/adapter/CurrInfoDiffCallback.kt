package spiridonov.currency.presentation.adapter

import androidx.recyclerview.widget.DiffUtil
import spiridonov.currency.domain.CurrItem

object CurrInfoDiffCallback : DiffUtil.ItemCallback<CurrItem>() {
    override fun areItemsTheSame(oldItem: CurrItem, newItem: CurrItem) =
        oldItem.code == newItem.code

    override fun areContentsTheSame(oldItem: CurrItem, newItem: CurrItem) = oldItem == newItem
}