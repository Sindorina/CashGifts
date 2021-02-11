package com.sin.cashgift.data

import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CashDao {

    @Insert
    fun saveCash(cashBean: CashBean)

    @Query("SELECT * FROM CASH")
    fun getCashes(): List<CashBean>
}