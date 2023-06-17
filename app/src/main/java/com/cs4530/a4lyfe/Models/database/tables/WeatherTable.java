package com.cs4530.a4lyfe.models.database.tables;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "weather_table")
public class WeatherTable {
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "weather_data")
    private String weatherJson;

    public WeatherTable(@NonNull String weatherJson) {
        this.weatherJson = weatherJson;
    }


    public String getWeatherJson() {
        return weatherJson;
    }

    public void setWeatherJson(String weatherdata) {
        this.weatherJson = weatherdata;
    }
}