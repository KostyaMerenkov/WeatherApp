package com.weatherapp.model.mainWeather;


import android.content.res.Resources;

public class MainSourceBuilder {
    private Resources resources;

    public MainSourceBuilder setResources(Resources resources){
        this.resources = resources;
        return this;
    }

    public WeatherDataSource build(){
        MainWeatherSource socSourceMain = new MainWeatherSource(resources);
        socSourceMain.init();
        return socSourceMain;
    }
}
