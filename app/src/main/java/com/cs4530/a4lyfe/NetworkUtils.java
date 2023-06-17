package com.cs4530.a4lyfe;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/*
    Creates a NetworkUtils object. This class creates a connection to the URL and gets data
    from the connection
    Author: Varun Shunkar
 */
public class NetworkUtils {
    private static final String BASE_URL_WEATHER = "https://api.openweathermap.org/data/2.5/weather?lat=";
    private static final String APPIDQUERY = "&appid=";
    private static final String app_id_weather = "59c26624790d4e6a526ffce873d46fc2";
    private static final String app_id_hike = "AIzaSyBK26fQ5sfaiPoc2z7XOYt8SZudToR_vJE";

    public static URL buildURLFromStringWeather(Double lat, Double lon) {
        URL myURL = null;
        try {
            myURL = new URL(BASE_URL_WEATHER + lat.toString() + "&lon=" + lon.toString() + APPIDQUERY + app_id_weather);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return myURL;
    }

    public static URL buildURLFromStringHike(String location) {
        URL myURL = null;
        try {
            myURL = new URL("https://maps.googleapis.com/maps/api/place/textsearch/json?query=hikes+" + location
                    + "+UnitedStates&key=" + app_id_hike);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return myURL;
    }

    public static String getDataFromURL(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();

            // The scanner trick: search for the next "beginning" of the input stream
            // No need to user BufferedReader
            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }

        } finally {
            urlConnection.disconnect();
        }
    }
}