package spiridonov.currency.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrInfoDao {

    @Query("SELECT * FROM currencies ORDER BY star DESC")
    fun getCurrList(): LiveData<List<CurrInfoDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCurrItem(currItemDbModel: CurrInfoDbModel)

    @Query("DELETE FROM currencies WHERE code=:currItemCode")
    suspend fun deleteCurrItem(currItemCode: String)

    @Query("SELECT * FROM currencies WHERE code=:currItemCode LIMIT 1")
    suspend fun getCurrItem(currItemCode: String): CurrInfoDbModel

}