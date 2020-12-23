package com.weatherapp.model;

import android.content.res.Resources;
import android.content.res.TypedArray;

import com.weatherapp.R;

import java.util.ArrayList;
import java.util.List;

public class MainSource implements SocialDataSource {
    private List<MainSoc> dataSource;   // строим этот источник данных
    private Resources resources;    // ресурсы приложения

    public MainSource(Resources resources) {
        dataSource = new ArrayList<>(6);
        this.resources = resources;
    }

    public MainSource init(){
        // строки описаний из ресурсов
        String[] dates = resources.getStringArray(R.array.rv_days);
        //строки температур
        String[] temp = resources.getStringArray(R.array.rv_temp);
        // изображения
        int[] pictures = getImageArray();
        // заполнение источника данных
        for (int i = 0; i < dates.length; i++) {
            dataSource.add(new MainSoc(dates[i], pictures[i], temp[i]));
        }
        return this;
    }

    public MainSoc getSoc(int position) {
        return dataSource.get(position);
    }

    public int size(){
        return dataSource.size();
    }

    // Механизм вытаскивания идентификаторов картинок (к сожалению просто массив не работает)
    // https://stackoverflow.com/questions/5347107/creating-integer-array-of-resource-ids
    private int[] getImageArray(){
        TypedArray pictures = resources.obtainTypedArray(R.array.pictures);
        int length = pictures.length();
        int[] answer = new int[length];
        for(int i = 0; i < length; i++){
            answer[i] = pictures.getResourceId(i, 0);
        }
        return answer;
    }
}

