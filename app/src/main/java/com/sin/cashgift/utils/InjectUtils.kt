package com.sin.cashgift.utils

import android.content.Context
import com.sin.cashgift.data.CashDatabase
import com.sin.cashgift.data.CashViewModelFactory
import com.sin.cashgift.repository.CashRepository

object InjectUtils {

    private fun getCashRepository(context: Context):CashRepository{
        val cashDao = CashDatabase.getInstance(context.applicationContext)
                .cashDao()
       return CashRepository.getInstance(cashDao)
    }

    fun getCashViewModelFactory(context: Context)
            = CashViewModelFactory(getCashRepository(context))
}