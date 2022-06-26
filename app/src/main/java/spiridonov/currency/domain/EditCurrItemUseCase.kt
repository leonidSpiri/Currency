package spiridonov.currency.domain

class EditCurrItemUseCase(
    private val repository: CurrListRepository
) {
    suspend operator fun invoke(currItem:CurrItem) = repository.editCurrItem(currItem)
}