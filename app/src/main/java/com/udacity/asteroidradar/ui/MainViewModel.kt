package com.udacity.asteroidradar.ui

import android.app.Application
import androidx.lifecycle.*
import com.udacity.asteroidradar.domain.Asteroid
import com.udacity.asteroidradar.db.getDatabase
import com.udacity.asteroidradar.repository.AsteroidRepository
import kotlinx.coroutines.launch

enum class ApiStatus { LOADING, ERROR, DONE }

enum class Filter { TODAY, WEEK, SAVED }

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _navigationToDetail = MutableLiveData<Asteroid?>()
    val navigationToDetail: MutableLiveData<Asteroid?>
        get() = _navigationToDetail

    private val database = getDatabase(application)
    private val asteroidRepository = AsteroidRepository(database)

    val pictureOfDay = asteroidRepository.pictureOfDay
    val pictureOfDayApiStatus = asteroidRepository.pictureOfDayApiStatus

        private val asteroidType = MutableLiveData(Filter.TODAY)
    val asteroidList = Transformations.switchMap(asteroidType) { type ->
        when (type) {
            Filter.WEEK -> {
                asteroidRepository.asteroidsWeek
            }
            Filter.TODAY -> {
                asteroidRepository.asteroidsToday
            }
            Filter.SAVED -> {
                asteroidRepository.asteroidsSave
            }
            else -> MutableLiveData<List<Asteroid>>(emptyList())
        }
    }

    init {
        viewModelScope.launch {
            asteroidRepository.refreshAsteroids()
            asteroidRepository.getImageOfDay()
        }

    }

    fun updateFiler(filer: Filter) {
        asteroidType.value = filer
    }

    fun onAsteroidClicked(asteroid: Asteroid) {
        _navigationToDetail.value = asteroid
    }

    fun onAsteroidNavigated() {
        _navigationToDetail.value = null
    }

    /**
     * Factory for constructing DevByteViewModel with parameter
     */
    class Factory(val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(app) as T
            }
            throw IllegalArgumentException("Unable to construct viewmodel")
        }
    }

}