package com.cs4530.a4lyfe.models.database.tableBuilder

import com.cs4530.a4lyfe.models.database.tables.WeatherTable

class WeatherTableBuilder {
    private var weatherJson: String? = null
    fun setWeatherJson(weatherJson: String?): WeatherTableBuilder {
        this.weatherJson = weatherJson
        return this
    }

    fun createWeatherTable(): WeatherTable {
        return WeatherTable(weatherJson!!)
    }
}