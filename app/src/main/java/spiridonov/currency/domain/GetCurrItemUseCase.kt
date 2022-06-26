package spiridonov.currency.domain

class GetCurrItemUseCase(
    private val currItemId:Int,
    private val repository: CurrListRepository
) {
    suspend operator fun invoke() = repository.getCurrItem(currItemId)
}