package com.cs4530.a4lyfe.models.viewModels

import android.app.Application
import android.content.Context
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.cs4530.a4lyfe.models.Repository
import com.cs4530.a4lyfe.models.Repository.Companion.getInstance
import com.cs4530.a4lyfe.models.WeatherData

class AppViewModel(application: Application?) : AndroidViewModel(application!!) {
    private val jsonWeatherData: MutableLiveData<WeatherData>
    private val jsonHikeData: MutableLiveData<String>
    private val mRepository: Repository? = getInstance(application!!)

    //Weather
    fun setWeatherLocation(location: Location?) {
        mRepository!!.setWeatherLocation(location)
    }

    val weatherData: LiveData<WeatherData>
        get() = jsonWeatherData

    //Hike
    suspend fun setHikeLocation(context: Context, location: Location?) {
        mRepository!!.setHikeLocation(context, location)
    }

    val hikeData: LiveData<String>
        get() = jsonHikeData

    init {
        jsonWeatherData = mRepository!!.weatherData
        jsonHikeData = mRepository.hikeData
    }
}