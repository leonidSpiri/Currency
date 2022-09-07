package spiridonov.currency.domain

import javax.inject.Inject

class GetCurrListUseCase @Inject constructor(
    private val repository: CurrListRepository
) {

    operator fun invoke() = repository.getCurrList()
}