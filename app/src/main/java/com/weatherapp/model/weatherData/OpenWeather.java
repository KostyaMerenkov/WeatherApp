package com.weatherapp.model.weatherData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface OpenWeather {
    @GET("data/2.5/weather")
    Call<WeatherRequest> loadWeather(@Query("q") String cityCountry, @Query("appid") String keyApi);

    // https://api.openweathermap.org/data/2.5/weather?q=Moscow&appid=dfsdfdsfsdfsd
    // https://api.openweathermap.org/data/2.5/weather?lat=55.75&lon=37.62&appid=sddsfsdfdsfsd
}
