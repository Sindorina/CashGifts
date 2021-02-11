package com.sin.cashgift.viewmodels

import androidx.lifecycle.*
import com.sin.cashgift.data.CashBean
import com.sin.cashgift.repository.CashRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CashViewModel (private val cashRepository: CashRepository)
    :ViewModel(){
    val keyword = MutableLiveData<String>().apply { value = NO_KEY_WORD }
    private var storeCashList = MutableLiveData<List<CashBean>>()

    fun getStoreCashList(){
        viewModelScope.launch (Dispatchers.Main){
            storeCashList = cashRepository.getCashList()
            keyword.value = NO_KEY_WORD
        }
    }

    val cashList = keyword.switchMap {
        if (it == NO_KEY_WORD){
            storeCashList
        }else{
            storeCashList.map {
                it.filter {
                    it.name.contains(keyword.value!!)
                }
            }
        }
    }

    companion object{
        const val NO_KEY_WORD = "-100000"
    }
}