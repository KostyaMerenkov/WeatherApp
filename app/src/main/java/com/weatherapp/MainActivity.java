package com.weatherapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.weatherapp.weatherData.ApiHolder;
import com.weatherapp.weatherData.OpenWeather;
import com.weatherapp.weatherData.WeatherRequest;

import java.io.BufferedReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private SharedPreferences sharedPref;

    private TextView city_name;
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
        String city = intent.getStringExtra(Constants.CITY_MESSAGE);
        Snackbar.make(findViewById(R.id.constraintLayout), "Вы выбрали: " + city, Snackbar.LENGTH_LONG).show();
        String city_id = String.valueOf(Arrays.asList(getResources().getStringArray(R.array.city_ids)).get(Arrays.asList(getResources().getStringArray(R.array.cities)).indexOf(city)));
        //GetWeather getWeather = new GetWeather(city_id, MainActivity.this,MainActivity.this, new Handler());
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

        initGui();
        initPreferences();
        requestRetrofit(city, BuildConfig.WEATHER_API_KEY);

        setBackground();

        if (Constants.DEBUG) {
            Toast.makeText(getApplicationContext(), "onCreate()", Toast.LENGTH_SHORT).show();
            detectOrientation();
        }
    }

    private void setBackground() {
        ConstraintLayout constraintLayout = findViewById(R.id.constraintLayout);
        ImageView img = new ImageView(this);
        Picasso.get().load("https://images.unsplash.com/photo-1607275667966-5923aacbba8f?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1234&q=80").into(img, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                constraintLayout.setBackground(img.getDrawable());
            }

            @Override
            public void onError(Exception e) {
                Log.d(TAG, "IMAGE FAILED");
            }
        });
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void initPreferences() {
        sharedPref = getPreferences(MODE_PRIVATE);
        loadPreferences();                   // Загружаем настройки
    }

    // Инициализируем пользовательские элементы
    private void initGui() {
        temperature = (TextView) findViewById(R.id.main_tempView);
        windSpeed = (TextView) findViewById(R.id.wind_info);
        humidity = (TextView) findViewById(R.id.humidity_info);
        pressure = (TextView) findViewById(R.id.pressure_info);
        clouds = (TextView) findViewById(R.id.textView3);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        savePreferences();
    }

    // Сохраняем настройки
    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPref.edit();
        //editor.putString("apiKey", editApiKey.getText().toString());
        editor.commit();
    }

    // Загружаем настройки
    private void loadPreferences() {
        String loadedApiKey = sharedPref.getString("apiKey", BuildConfig.WEATHER_API_KEY);
        //editApiKey.setText(loadedApiKey);
    }


    private void requestRetrofit(String city, String keyApi) {
        ApiHolder apiHolder = new ApiHolder();
        apiHolder.getOpenWeather().loadWeather(city, keyApi)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null) {
                            setWeather(response);
                        }
                    }
                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                        setConnectionTimeout(city);
                    }
                });
    }

    public void setConnectionTimeout(String city) {
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
                                        requestRetrofit(city, BuildConfig.WEATHER_API_KEY);
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

    public void setWeather(Response<WeatherRequest> response) {
        Toolbar city = findViewById(R.id.toolbar);
        city.setTitle(response.body().getName());
        temperature.setText(String.format("%d + \"°\"", (int) response.body().getMain().getTemp()-273));
        pressure.setText(String.format(getString(R.string.pressure), response.body().getMain().getPressure()));
        humidity.setText(String.format(getString(R.string.humidity), response.body().getMain().getHumidity())+"%");
        windSpeed.setText(String.format(getString(R.string.wind), (int) response.body().getWind().getSpeed()));
        clouds.setText(response.body().getWeather()[0].getDescription());
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
            startSettings();
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

    public void startSettings() {
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
                startSettings();
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