package com.weatherapp.model;


import android.content.res.Resources;

public class MainSourceBuilder {
    private Resources resources;

    public MainSourceBuilder setResources(Resources resources){
        this.resources = resources;
        return this;
    }

    public SocialDataSource build(){
        MainSource socSourceMain = new MainSource(resources);
        socSourceMain.init();
        return socSourceMain;
    }
}
