package spiridonov.currency.domain

class AddCurrItemUseCase(
    private val currItem:CurrItem,
    private val repository: CurrListRepository
) {
    suspend operator fun invoke() = repository.addCurrItem(currItem)
}