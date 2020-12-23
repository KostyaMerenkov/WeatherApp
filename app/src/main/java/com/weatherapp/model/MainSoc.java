package com.weatherapp.model;

// данные для карточки
public class MainSoc {
    private String date; // дата
    private int picture;        // изображение
    private String temp;       // температура

    public MainSoc(String date, int picture, String temp) {
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
