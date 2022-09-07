package spiridonov.currency.domain

import javax.inject.Inject

class LoadDataUseCase @Inject constructor(
    private val repository: CurrListRepository
) {

    operator fun invoke() = repository.loadData()
}