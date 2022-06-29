package spiridonov.currency.domain
data class CurrItem(
    val name: String,
    val code: String,
    val digitalCode:String,
    val value: String,
    val previous: String,
    val star: Boolean,
    val date: String
)
