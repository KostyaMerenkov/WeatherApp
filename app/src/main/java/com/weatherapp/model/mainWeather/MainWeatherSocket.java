package com.weatherapp.model.mainWeather;

// данные для карточки
public class MainWeatherSocket {
    private String date; // дата
    private int picture;        // изображение
    private String temp;       // температура

    public MainWeatherSocket(String date, int picture, String temp) {
        this.date = date;
        this.picture = picture;
        this.temp = temp;
    }

    // геттеры
    public String getDate() {
        return date;
    }

    public int getPicture() {
        return picture;
    }

    public String getTemp() {
        return temp;
    }
}
