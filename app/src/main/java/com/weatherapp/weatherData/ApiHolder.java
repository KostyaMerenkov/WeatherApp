package com.weatherapp.weatherData;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import com.weatherapp.weatherData.OpenWeather;

public class ApiHolder {
    private final OpenWeather openWeather;

    public ApiHolder() {
        Retrofit retrofit;

        retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create(gson()))
                .build();

        openWeather = retrofit.create(OpenWeather.class);

    }

    private Gson gson() {
        return new GsonBuilder().setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .excludeFieldsWithoutExposeAnnotation().create();
    }

    public OpenWeather getOpenWeather() {
        return openWeather;
    }

}
