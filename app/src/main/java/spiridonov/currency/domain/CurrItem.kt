package spiridonov.currency.domain
data class CurrItem(
    val name: String,
    val code: String,
    val value: String,
    val previous: String,
    val star: Boolean = false
)
