package spiridonov.currency.domain

class EditCurrItemUseCase(
    private val currItem:CurrItem,
    private val repository: CurrListRepository
) {
    suspend operator fun invoke() = repository.editCurrItem(currItem)
}