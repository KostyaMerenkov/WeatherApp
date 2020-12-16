package com.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.weatherapp.weatherData.GetWeather;
import com.weatherapp.weatherData.Weather;
import com.weatherapp.weatherData.WeatherRequest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private TextView tempTextView;

    private final static String TAG = GetWeather.class.getSimpleName();

    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?lat=55.75&lon=37.62&appid=";

    private TextView city;
    private TextView temperature;
    private TextView windSpeed;
    private TextView humidity;
    private TextView pressure;
    private TextView clouds;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        String message = intent.getStringExtra(Constants.CITY_MESSAGE);
        Snackbar.make(findViewById(R.id.constraintLayout), "Вы выбрали: " + message, Snackbar.LENGTH_LONG).show();
        String city_id = String.valueOf(Arrays.asList(getResources().getStringArray(R.array.city_ids)).get(Arrays.asList(getResources().getStringArray(R.array.cities)).indexOf(message)));
        GetWeather getWeather = new GetWeather(city_id, MainActivity.this,MainActivity.this, new Handler());
        String date = intent.getStringExtra(Constants.DATE_MESSAGE);
        Button date_button = findViewById(R.id.date_button);
        date_button.setText(date);

        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);

        //RecyclerView
        // строим источник данных
        SocialDataSource sourceData = new SocSourceBuilder()
                .setResources(getResources())
                .build();
        initRecyclerView(sourceData);


        if (Constants.DEBUG) {
            Toast.makeText(getApplicationContext(), "onCreate()", Toast.LENGTH_SHORT).show();
            detectOrientation();
        }
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    public void setConnectionTimeout(String city_id) {
        //Snackbar.make(findViewById(R.id.main_tempView), "Ошибка подключения к серверу", Snackbar.LENGTH_LONG).show();
        // Создаём билдер и передаём контекст приложения
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        // В билдере указываем заголовок окна (можно указывать как ресурс,
        // так и строку)
        builder.setTitle(R.string.server_error)
                // Указываем сообщение в окне (также есть вариант со
                // строковым параметром)
                .setMessage(R.string.try_again)
                // Можно указать и пиктограмму
                .setIcon(android.R.drawable.ic_dialog_alert)
                // Из этого окна нельзя выйти кнопкой Back
                .setCancelable(false)
                // Устанавливаем кнопку (название кнопки также можно
                // задавать строкой)
                .setPositiveButton(R.string.button_yes,
                        // Ставим слушатель, нажатие будем обрабатывать
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.runOnUiThread(new Runnable()
                                {
                                    public void run()
                                    {
                                        GetWeather getWeather = new GetWeather(city_id, MainActivity.this,MainActivity.this, new Handler());
                                    }
                                });
                            }
                        }).setNegativeButton(R.string.button_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setWeather(WeatherRequest weatherRequest) {
        temperature = (TextView) findViewById(R.id.main_tempView);
        windSpeed = (TextView) findViewById(R.id.wind_info);
        humidity = (TextView) findViewById(R.id.humidity_info);
        pressure = (TextView) findViewById(R.id.pressure_info);
        clouds = (TextView) findViewById(R.id.textView3);
        Toolbar city = findViewById(R.id.toolbar);
        city.setTitle(weatherRequest.getName());
        temperature.setText(String.format("%d + \"°\"", (int) weatherRequest.getMain().getTemp()-273));
        pressure.setText(String.format(getString(R.string.pressure), weatherRequest.getMain().getPressure()));
        humidity.setText(String.format(getString(R.string.humidity), weatherRequest.getMain().getHumidity())+"%");
        windSpeed.setText(String.format(getString(R.string.wind), weatherRequest.getWind().getSpeed()));
        clouds.setText(weatherRequest.getWeather()[0].getDescription());
    }

    private void initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private String getLines(BufferedReader in) {
        return in.lines().collect(Collectors.joining("\n"));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startSettings(this.tempTextView);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Constants.DEBUG) {
            Toast.makeText(getApplicationContext(), "onPause()", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onPause()");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (Constants.DEBUG) {
            Toast.makeText(getApplicationContext(), "onRestart()", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onRestart()");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Constants.DEBUG) {
            Toast.makeText(getApplicationContext(), "onResume()", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onResume()");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Constants.DEBUG) {
            Toast.makeText(getApplicationContext(), "onDestroy()", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onDestroy()");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putInt("Temp", Integer.parseInt((String) temperature.getText()));
    }

    @Override
    public void onRestoreInstanceState(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        temperature.setText(savedInstanceState.getInt("Temp"));
    }

    public void detectOrientation() {
        Context appContext = getApplicationContext();
        Configuration configuration = getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(appContext, "Портретная ориентация", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Портретная ориентация");
        }
        if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(appContext, "Альбомная ориентация", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Альбомная ориентация");
        }
    }

    private void initRecyclerView(SocialDataSource sourceData){
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        // Эта установка служит для повышения производительности системы
        recyclerView.setHasFixedSize(true);

        // Добавим разделитель карточек
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this,  LinearLayoutManager.HORIZONTAL);
        itemDecoration.setDrawable(getDrawable(R.drawable.separator));
        recyclerView.addItemDecoration(itemDecoration);


        // Будем работать со встроенным менеджером
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Установим адаптер
        SocnetAdapter adapter = new SocnetAdapter(sourceData);
        recyclerView.setAdapter(adapter);


        if (Constants.DEBUG) {
            // Установим слушателя
            adapter.SetOnItemClickListener(new SocnetAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    Toast.makeText(MainActivity.this, String.format("Позиция - %d", position), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    public void date_info(View view) {
        Uri address = Uri.parse("https://www.calend.ru/narod/");
        Intent linkInet = new Intent(Intent.ACTION_VIEW, address);
        startActivity(linkInet);
    }

    public void startSettings(View view) {
        // действия, совершаемые после нажатия на кнопку
        // Создаем объект Intent для вызова новой Activity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_weather_now:
                //TODO:
                break;
            case R.id.nav_choose_city:
                Intent intent = new Intent(this, ChooseCityActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                startSettings(this.tempTextView);
                break;
            case R.id.nav_dev_info:
                //TODO:
                break;
            case R.id.nav_help:
                //TODO:
                break;

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}