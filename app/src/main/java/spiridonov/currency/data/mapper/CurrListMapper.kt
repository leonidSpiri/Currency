package spiridonov.currency.data.mapper

import spiridonov.currency.data.database.CurrListDbModel
import spiridonov.currency.domain.CurrItem

class CurrListMapper {
    fun mapEntityToDbModel(entity: CurrItem) = CurrListDbModel(
        code = entity.code,
        name = entity.name,
        value = entity.value,
        previous = entity.previous,
        star = entity.star
    )

    fun mapDbModelToEntity(currListDbModel: CurrListDbModel) = CurrItem(
        code = currListDbModel.code,
        name = currListDbModel.name,
        value = currListDbModel.value,
        previous = currListDbModel.previous,
        star = currListDbModel.star
    )

    fun mapListDbModelToListEntity(list: List<CurrListDbModel>) = list.map {
        mapDbModelToEntity(it)
    }
}