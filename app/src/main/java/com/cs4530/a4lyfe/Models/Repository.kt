package com.cs4530.a4lyfe.models

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.location.Geocoder
import android.location.Location
import androidx.lifecycle.MutableLiveData
import com.cs4530.a4lyfe.NetworkUtils
import com.cs4530.a4lyfe.models.database.AppDao
import com.cs4530.a4lyfe.models.database.tableBuilder.HikeTableBuilder
import com.cs4530.a4lyfe.models.database.tableBuilder.UserTableBuilder
import com.cs4530.a4lyfe.models.database.tableBuilder.WeatherTableBuilder
import com.cs4530.a4lyfe.pages.Weather.JSONWeatherUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

private val json = Json { ignoreUnknownKeys = true }

class Repository private constructor(val application: Application) {

    //Weather
    val weatherData = MutableLiveData<WeatherData>()
    private var mJsonWeatherString: String? = null
    private var mWeatherLocation: Location? = null

    //Hiking
    val hikeData = MutableLiveData<String>()
    private var mJsonHikeString: String? = null
    private var mHikeLocation: Location? = null

    // User
    val mUserDao: AppDao
    val userData = MutableLiveData<User>()

    fun setWeatherLocation(location: Location?) {
        runBlocking {
            loadWeatherData(location)
        }
        insertWeather()
    }


    private fun insertWeather() {
        if (weatherData.value != null) {
            val weatherTable = WeatherTableBuilder()
                .setWeatherJson(encodeWeatherData(weatherData.value!!)).createWeatherTable()
            RoomDatabase.databaseExecutor.execute { mUserDao.insertWeather(weatherTable) }
        }
    }

    fun deleteWeather() {
        RoomDatabase.databaseExecutor.submit { mUserDao.deleteAllWeather() }
    }

    private suspend fun loadWeatherData(location: Location?) {
        loadWeatherFromUrl(location)
    }

    fun createUser() {
        val userData = userData
        val user = userData.value
        if (user != null) {
            val future = RoomDatabase.databaseExecutor.submit { mUserDao.deleteAllUsers() }
            future.get() // waits for it
            val userTable =
                UserTableBuilder()
                    .setBirthday(user.birthday).setFirstName(user.firstName)
                    .setLastName(user.lastName).setFeet(user.feet)
                    .setInches(user.inches).setGoal(user.goal).setWeight(user.weight)
                    .setHeight(user.height).setRegistered(true).setSex(user.sex)
                    .setProfileImage(user.profileImage).setLifestyle(user.lifestyle)
                    .createUserTable()
            val future2 = RoomDatabase.databaseExecutor.submit { mUserDao.insertUser(userTable) }
            future2.get()
        }
    }

    suspend fun setHikeLocation(context: Context, location: Location?) {
        loadHikeTaskFromUrl(context, location)
        insertHike()
    }

    private fun insertHike() {
        if (mHikeLocation != null) {
            val hikeTable = HikeTableBuilder()
                .setHikeJson(mJsonHikeString).createHikeTable()
            RoomDatabase.databaseExecutor.execute { mUserDao.insertHike(hikeTable) }
        }
    }



    private suspend fun loadWeatherFromUrl(weatherLocation: Location?) {
        withContext(Dispatchers.IO) {
            launch(Dispatchers.IO) {
                val weatherDataURL = NetworkUtils.buildURLFromStringWeather(
                    weatherLocation!!.latitude, weatherLocation.longitude
                )
                try {
                    val jsonWeatherData = NetworkUtils.getDataFromURL(weatherDataURL)
                    if (jsonWeatherData != null) {
                            launch(Dispatchers.Main) {
                                weatherData.value = JSONWeatherUtils.getWeatherData(jsonWeatherData)
                            }

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

    private suspend fun loadHikeTaskFromUrl(context: Context, location: Location?) {
        withContext(Dispatchers.IO) {
            launch(Dispatchers.IO) {
                val geo = Geocoder(context)
                try {
                    val temp = geo.getFromLocation(
                        location!!.latitude, location.longitude, 5
                    )
                    val hikeDataURL = NetworkUtils.buildURLFromStringHike(temp[0].locality)
                    val jsonHikeData = NetworkUtils.getDataFromURL(hikeDataURL)
                    if (jsonHikeData != null) {
                        val jsonString = JSONObject(jsonHikeData).getJSONArray("results").toString()
                        launch(Dispatchers.Main) {
                            hikeData.value = jsonString
                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: Repository? = null

        @JvmStatic
        @Synchronized
        fun getInstance(application: Application): Repository? {
            if (instance == null) {
                instance = Repository(application)
            }
            return instance
        }
    }

    init {
        val db = RoomDatabase.getDatabase(application)
        mUserDao = db.appDao()
    }

}