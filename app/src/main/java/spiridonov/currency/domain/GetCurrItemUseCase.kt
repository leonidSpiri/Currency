package spiridonov.currency.domain

class GetCurrItemUseCase(
    private val CurrItemCode:String,
    private val repository: CurrListRepository
) {
    suspend operator fun invoke() = repository.getCurrItem(CurrItemCode)
}