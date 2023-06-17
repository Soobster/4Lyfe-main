package com.cs4530.a4lyfe.models.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.cs4530.a4lyfe.models.User
import com.cs4530.a4lyfe.models.database.tables.HikeTable
import com.cs4530.a4lyfe.models.database.tables.UserTable
import com.cs4530.a4lyfe.models.database.tables.WeatherTable
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    //Weather
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(weatherTable: WeatherTable?)

    @Query("DELETE FROM weather_table")
    fun deleteAllWeather()

    @get:Query("SELECT * from weather_table ORDER BY weather_data ASC")
    val allWeather: Flow<List<String>>

    //Hike
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHike(hikeTable: HikeTable?)

    @Query("DELETE FROM hike_table")
    fun deleteAllHikes()

    @get:Query("SELECT * from hike_table ORDER BY hikedata ASC")
    val allHikes: Flow<String>

    //User
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(userTable: UserTable?)

    @Query("DELETE FROM user_table")
    fun deleteAllUsers()

    @get:Query("SELECT * from user_table ORDER BY lastName ASC")
    val allUsers: Flow<List<User>>
}