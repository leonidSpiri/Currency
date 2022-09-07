package spiridonov.currency.domain

import javax.inject.Inject

class GetCurrItemUseCase @Inject constructor(
    private val repository: CurrListRepository
) {
    operator fun invoke(CurrItemCode: String) = repository.getCurrItem(CurrItemCode)
}