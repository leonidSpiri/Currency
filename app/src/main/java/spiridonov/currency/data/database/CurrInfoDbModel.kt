package spiridonov.currency.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

class CurrInfoDbModel {
    @Entity(tableName = "currencies")
    data class CoinInfoDbModel(
        @PrimaryKey
        val code: String,
        val name: String,
        val value: String,
        val previous: String,
        val star: Boolean = false
    )
}