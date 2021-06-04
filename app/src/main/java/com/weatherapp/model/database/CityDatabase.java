package com.weatherapp.model.database;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {City.class}, version = 1)
public abstract class CityDatabase extends RoomDatabase {
    public abstract CityDao getCityDao();
}

