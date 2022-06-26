package spiridonov.currency.domain

class DeleteCurrItemUseCase(
    private val currItem:CurrItem,
    private val repository: CurrListRepository
) {
    suspend operator fun invoke(currItem:CurrItem) = repository.deleteCurrItem(currItem)
}