package com.weatherapp.model.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

// @Entity - это признак табличного объекта, то есть объект будет сохраняться
// в базе данных в виде строки
// indices указывает на индексы в таблице
@Entity(indices = {@Index(value = {"city_name"})})
public class City {

    public void setId(long id) {

    }

    // @PrimaryKey - указывает на ключевую запись,
    // autoGenerate = true - автоматическая генерация ключа
    @PrimaryKey(autoGenerate = true)
    private long id;

    // Название города
    // @ColumnInfo позволяет задавать параметры колонки в БД
    // name = "city_name" - имя колонки
    @ColumnInfo(name = "city_name")
    private String cityName;

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public long getId() {
        return id;
    }
}

