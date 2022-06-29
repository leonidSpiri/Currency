package spiridonov.currency.data.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CurrListDao {

    @Query("SELECT * FROM currencies ORDER BY star DESC, name ASC")
    fun getCurrListLiveData(): LiveData<List<CurrItemDbModel>>

    @Query("SELECT * FROM currencies ORDER BY star DESC, name ASC")
    suspend fun getSuspendCurrList(): List<CurrItemDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCurrItem(currItemDbModel: CurrItemDbModel)

    @Query("SELECT * FROM currencies WHERE code == :currItemCode LIMIT 1")
    fun getCurrItemDb(currItemCode: String): LiveData<CurrItemDbModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrList(currList: List<CurrItemDbModel>)

}