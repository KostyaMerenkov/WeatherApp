package com.weatherapp.model.mainWeather;

public interface WeatherDataSource {
    MainWeatherSocket getSoc(int position);
    int size();
}

