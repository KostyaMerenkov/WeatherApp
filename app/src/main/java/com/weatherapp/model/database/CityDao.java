package com.weatherapp.model.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

// Описание объекта, обрабатывающего данные
// @Dao - доступ к данным
// В этом классе описывается, как будет происходить обработка данных
@Dao
public interface CityDao {

    // Метод для добавления в базу данных
    // @Insert - признак добавления
    // onConflict - что делать, если такая запись уже есть
    // В данном случае просто заменим её
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCity(City city);

    // Метод для замены данных
    @Update
    void updateCity(City student);

    // Удаляем данные
    @Delete
    void deleteCity(City student);

    // Удаляем данные
    @Query("DELETE FROM City WHERE id = :id")
    void deteleCityById(long id);

    // Забираем данные
    @Query("SELECT * FROM City")
    List<City> getAllCities();

    // Получаем данные одного студента по id
    @Query("SELECT * FROM City WHERE id = :id")
    City getCityById(long id);

    //Получаем количество записей в таблице
    @Query("SELECT COUNT() FROM City")
    long getCountCities();
}

