package spiridonov.currency.domain

class GetCurrItemUseCase(
    private val repository: CurrListRepository
) {
    operator fun invoke(CurrItemCode:String) = repository.getCurrItem(CurrItemCode)
}