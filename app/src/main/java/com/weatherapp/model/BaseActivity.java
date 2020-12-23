package com.weatherapp.model;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

// Базовый класс для активити, здесь сохраняем и читаем настройки
// Устанавливаем выбранную тему
public abstract class BaseActivity extends AppCompatActivity {

    // Имя настроек
    private static final String NameSharedPreference = "THEME";

    // Имя параметра в настройках
    private static final String IsDarkTheme = "IS_DARK_THEME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO: переключение темы
//        if (isDarkTheme()) {
//            setTheme(R.style.Theme_WeatherApp_Dark);
//        } else {
//            setTheme(R.style.Theme_WeatherApp);
//        }
    }

    // Чтение настроек, параметр тема
    protected boolean isDarkTheme() {
        // Работаем через специальный класс сохранения и чтения настроек
        SharedPreferences sharedPref = getSharedPreferences(NameSharedPreference, MODE_PRIVATE);
        //Прочитать тему, если настройка не найдена - взять по умолчанию true
        return sharedPref.getBoolean(IsDarkTheme, true);
    }

    // Сохранение настроек
    protected void setDarkTheme(boolean isDarkTheme) {
        SharedPreferences sharedPref = getSharedPreferences(NameSharedPreference, MODE_PRIVATE);
        // Настройки сохраняются посредством специального класса editor.
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IsDarkTheme, isDarkTheme);
        editor.apply();
    }
}
