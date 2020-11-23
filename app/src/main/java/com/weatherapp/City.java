package com.weatherapp;

public class City {
    private int id;

    private String name;

    private String state;

    private String country;

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setState(String state){
        this.state = state;
    }
    public String getState(){
        return this.state;
    }
    public void setCountry(String country){
        this.country = country;
    }
    public String getCountry(){
        return this.country;
    }

}
