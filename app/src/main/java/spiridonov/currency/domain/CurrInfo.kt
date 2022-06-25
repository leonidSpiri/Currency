package spiridonov.currency.domain
data class CurrInfo(
    val name: String,
    val code: String,
    val value: String,
    val previous: String,
    val star: Boolean = false
)
