package spiridonov.currency.domain

class DeleteCurrItemUseCase(
    private val currItem:CurrItem,
    private val repository: CurrListRepository
) {
    suspend operator fun invoke() = repository.deleteCurrItem(currItem)
}