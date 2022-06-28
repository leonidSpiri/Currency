package spiridonov.currency.domain

class GetCurrItemUseCase(
    private val repository: CurrListRepository
) {
    suspend operator fun invoke(CurrItemCode:String) = repository.getCurrItem(CurrItemCode)
}