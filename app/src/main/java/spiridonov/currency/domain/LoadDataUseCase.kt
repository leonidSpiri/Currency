package spiridonov.currency.domain

class LoadDataUseCase(
    private val repository: CurrListRepository
) {

    operator fun invoke() = repository.loadData()
}