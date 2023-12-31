package com.taba.tastevin.ui.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.taba.tastevin.database.asDomainModel
import com.taba.tastevin.domain.Wine
import com.taba.tastevin.domain.asDatabaseModel
import com.taba.tastevin.network.NetworkWine
import com.taba.tastevin.network.WineApi
import com.taba.tastevin.network.asDomainModel
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {

    val recommendWines = MutableLiveData<List<Wine>>()

    fun recommendWine(item: Wine) {
        viewModelScope.launch {
            try {
                val networkWines: List<NetworkWine> = listOf(
                    WineApi.retrofitService.getWineById(item.recommend1),
                    WineApi.retrofitService.getWineById(item.recommend2),
                    WineApi.retrofitService.getWineById(item.recommend3)
                )
                val wines = networkWines.map { it.asDomainModel() }
                recommendWines.postValue(wines)
            } catch (_: Exception) {
            }
        }
    }

    fun getBookmarkList(db: com.taba.tastevin.database.WineDao): List<Wine> {
        val list = db.getAllWines().map {
            it.asDomainModel()
        }
        return list
    }

    /**
     * wineId를 통해 해당 와인이 북마크에 있는 와인인지 리턴
     */
    fun isBookmarked(bookmark: List<Wine>, id: Int): Boolean {
        for (i in bookmark) {
            if (id == i.id) {
                return true
            }
        }
        return false
    }

    /**
     * 해당 와인을 북마크 데이터베이스에 추가
     */
    fun addToBookmarkList(db: com.taba.tastevin.database.WineDao, item: Wine) {
        try {
            db.insertWine(item.asDatabaseModel())
        } catch (_: Exception) {
        }
    }

    /**
     * 해당 와인을 북마크 데이터베이스에서 제거
     */
    fun deleteToBookmarkList(db: com.taba.tastevin.database.WineDao, item: Wine) {
        try {
            db.deleteWine(item.asDatabaseModel())
        } catch (_: Exception) {
        }
    }
}