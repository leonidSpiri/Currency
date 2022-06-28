package spiridonov.currency.domain

class AddCurrItemUseCase(
    private val repository: CurrListRepository
) {
    suspend operator fun invoke(currItem:CurrItem) = repository.addCurrItem(currItem)
}