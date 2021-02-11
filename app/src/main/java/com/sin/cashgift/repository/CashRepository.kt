package com.sin.cashgift.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sin.cashgift.data.CashBean
import com.sin.cashgift.data.CashDao
import com.sin.cashgift.utils.ExcelParseUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CashRepository private constructor(private val cashDao: CashDao) {

    companion object {
        @Volatile
        private var instance: CashRepository? = null
        fun getInstance(cashDao: CashDao): CashRepository {
            return instance ?: synchronized(this) {
                CashRepository(cashDao).also {
                    instance = it
                }
            }
        }
    }

    suspend fun getCashList() :MutableLiveData<List<CashBean>>{
        return withContext(Dispatchers.IO){
            val localList = ExcelParseUtils.readExcel()
            val dbList = cashDao.getCashes()
            if (dbList.isNullOrEmpty()){
                MutableLiveData(localList)
            }else{
                MutableLiveData(dbList)
            }
        }
    }
}