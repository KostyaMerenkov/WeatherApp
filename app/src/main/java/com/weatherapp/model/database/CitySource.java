package com.weatherapp.model.database;

import java.util.List;

// Вспомогательный класс, развязывающий зависимость между Room и RecyclerView
public class CitySource {

    private final CityDao cityDao;

    // Буфер с данными: сюда будем подкачивать данные из БД
    private List<City> cities;

    public CitySource(CityDao cityDao){
        this.cityDao = cityDao;
    }

    // Получить всех
    public List<City> getCities(){
        // Если объекты еще не загружены, загружаем их.
        // Это сделано для того, чтобы не делать запросы к БД каждый раз
        if (cities == null){
            LoadCities();
        }
        return cities;
    }

    // Загружаем в буфер
    public void LoadCities(){
        cities = cityDao.getAllCities();
    }

    // Получаем количество записей
    public long getCountCities(){
        return cityDao.getCountCities();
    }

    // Добавляем
    public void addCity(City city){
        cityDao.insertCity(city);
        // После изменения БД надо повторно прочесть данные из буфера
        LoadCities();
    }

    // Заменяем
    public void updateCity(City city){
        cityDao.updateCity(city);
        LoadCities();
    }

    // Удаляем из базы
    public void removeCity(long id){
        cityDao.deteleCityById(id);
        LoadCities();
    }

}
