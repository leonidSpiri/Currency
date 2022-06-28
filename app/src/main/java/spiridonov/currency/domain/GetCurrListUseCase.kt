package spiridonov.currency.domain

class GetCurrListUseCase(
    private val repository: CurrListRepository
) {

    operator fun invoke() = repository.getCurrList()
}