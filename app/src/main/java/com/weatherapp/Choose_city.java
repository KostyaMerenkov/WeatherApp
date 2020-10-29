package com.weatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

public class Choose_city extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "CITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_city);


        AutoCompleteTextView editText = findViewById(R.id.actv);


        String[] cities = getResources().getStringArray(R.array.cities);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, cities);
        editText.setAdapter(adapter);

    }

    public void weather(View view) {
        // действия, совершаемые после нажатия на кнопку
        // Создаем объект Intent для вызова новой Activity
        Intent intent = new Intent(this, MainActivity.class);
        // Получаем текстовое поле в текущей Activity
        EditText editText = (EditText) findViewById(R.id.actv);
        // Получае текст данного текстового поля
        String message = editText.getText().toString();
        // Добавляем с помощью свойства putExtra объект - первый параметр - ключ,
        // второй параметр - значение этого объекта
        // запуск activity
        startActivity(intent);
    }
}