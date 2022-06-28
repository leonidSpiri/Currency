package spiridonov.currency.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "currencies")
data class CurrItemDbModel(
    @PrimaryKey
    val code: String,
    val name: String,
    val value: String,
    val previous: String,
    val star: Boolean
)
