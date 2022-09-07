package spiridonov.currency.domain

import javax.inject.Inject

class EditCurrItemUseCase @Inject constructor(
    private val repository: CurrListRepository
) {
    suspend operator fun invoke(currItem: CurrItem) = repository.editCurrItem(currItem)
}