package com.weatherapp.model;
import android.app.Application;

import androidx.room.Room;

import com.weatherapp.model.database.CityDao;
import com.weatherapp.model.database.CityDatabase;
import com.weatherapp.model.weatherData.ApiHolder;


// Паттерн Singleton, наследуем класс Application, создаём базу данных
// в методе onCreate
public class App extends Application {
    private static ApiHolder apiHolder;
    private static App instance;

    // База данных
    private CityDatabase db;

    public static ApiHolder getApiHolder() {
        return apiHolder;
    }

    // Получаем объект приложения
    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        apiHolder = new ApiHolder();

        // Сохраняем объект приложения (для Singleton’а)
        instance = this;

        // Строим базу
        db = Room.databaseBuilder(
                getApplicationContext(),
                CityDatabase.class,
                "city_history").allowMainThreadQueries() //Только для примеров и тестирования.
                .build();
    }

    // Получаем EducationDao для составления запросов
    public CityDao getCityDao() {
        return db.getCityDao();
    }
}

