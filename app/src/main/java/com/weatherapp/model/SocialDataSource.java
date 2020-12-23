package com.weatherapp.model;

import com.weatherapp.model.MainSoc;

public interface SocialDataSource {
    MainSoc getSoc(int position);
    int size();
}

