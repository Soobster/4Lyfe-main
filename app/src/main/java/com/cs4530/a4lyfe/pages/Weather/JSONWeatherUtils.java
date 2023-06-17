package com.cs4530.a4lyfe.pages.Weather;

import com.cs4530.a4lyfe.models.Clouds;
import com.cs4530.a4lyfe.models.CurrentCondition;
import com.cs4530.a4lyfe.models.Temperature;
import com.cs4530.a4lyfe.models.WeatherData;
import com.cs4530.a4lyfe.models.Wind;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//Declare methods as static. We don't want to create objects of this class.
public class JSONWeatherUtils {
    public static WeatherData getWeatherData(String data) throws JSONException {
        WeatherData weatherData = new WeatherData();

        // Start parsing JSON data
        JSONObject jsonObject = new JSONObject(data); // Must throw JSONException

        CurrentCondition currentCondition = weatherData.getCurrentCondition();

        JSONArray jsonWeather = jsonObject.getJSONArray("weather");
        currentCondition.setDescr(jsonWeather.getJSONObject(0).getString("description"));

        JSONObject jsonMain = jsonObject.getJSONObject("main");
        currentCondition.setHumidity(jsonMain.getInt("humidity"));
        currentCondition.setPressure(jsonMain.getInt("pressure"));

        weatherData.setCurrentCondition(currentCondition);

        // Get the temperature, wind and cloud data.
        Temperature temperature = weatherData.getTemperature();
        Wind wind = weatherData.getWind();
        Clouds clouds = weatherData.getClouds();
        temperature.setMaxTemp(jsonMain.getDouble("temp_max"));
        temperature.setMinTemp(jsonMain.getDouble("temp_min"));
        temperature.setTemp(jsonMain.getDouble("temp"));
        weatherData.setTemperature(temperature);
        weatherData.setCity(jsonObject.getString("name"));

        return weatherData;
    }
}
