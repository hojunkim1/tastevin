package com.taba.tastevin.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taba.tastevin.domain.Wine
import com.taba.tastevin.network.WineApi
import com.taba.tastevin.network.asDomainModel
import kotlinx.coroutines.launch

class HomeViewModel() : ViewModel() {
    val topWines = MutableLiveData<List<Wine>>()

    init {
        getTopWineList()
    }

    private fun getTopWineList() {
        viewModelScope.launch {
            try {
                val networkWines = WineApi.retrofitService.getTopWines()
                val wines = networkWines.map { it.asDomainModel() }
                topWines.postValue(wines)
            } catch (_: Exception) {
            }
        }
    }
}