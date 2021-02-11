package com.sin.cashgift.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sin.cashgift.repository.CashRepository
import com.sin.cashgift.viewmodels.CashViewModel

class CashViewModelFactory(private val cashRepository: CashRepository)
    :ViewModelProvider.NewInstanceFactory(){
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>) =
            CashViewModel(cashRepository) as T
}