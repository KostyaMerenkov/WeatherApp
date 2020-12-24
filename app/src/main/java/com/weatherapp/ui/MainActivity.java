package com.weatherapp.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.ui.AppBarConfiguration;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import com.weatherapp.BuildConfig;
import com.weatherapp.model.Constants;
import com.weatherapp.R;
import com.weatherapp.ui.settingsUI.SettingsActivity;
import com.weatherapp.model.weatherData.ApiHolder;
import com.weatherapp.model.weatherData.WeatherRequest;

import java.io.BufferedReader;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private final static String TAG = MainActivity.class.getSimpleName();

    private SharedPreferences sharedPref;

    private String city;
    private AppBarConfiguration mAppBarConfiguration;
    private CoordinatorLayout coordinatorlayout;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        checkForFirstRun();

        //putFragment(R.id.fragment_main_container, new MainFragment());

        restorePreferences();
        setContentView(R.layout.activity_main);

        Snackbar.make(findViewById(R.id.main_layout), "Вы выбрали: " + city, Snackbar.LENGTH_LONG).show();

        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);

        setBackground();

        initPreferences();
        //requestRetrofit(city, BuildConfig.WEATHER_API_KEY);

        NavigationView navigationView = findViewById(R.id.nav_view);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_weather, R.id.nav_choose, R.id.nav_settings)
                .setDrawerLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.fragment_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        navigationView.setNavigationItemSelectedListener(this);

        if (Constants.DEBUG) {
            Toast.makeText(getApplicationContext(), "onCreate()", Toast.LENGTH_SHORT).show();
            detectOrientation();
        }
    }

    private void checkForFirstRun() {
        if (sharedPref.getString(Constants.CITY_MESSAGE, null) != null){
            city = sharedPref.getString(Constants.CITY_MESSAGE, null);
        } else startGreetingActivity();
    }

    private void startGreetingActivity() {
        Intent intent = new Intent(this, GreetingActivity.class);
        startActivity(intent);
    }

    private void restorePreferences() {
        if (sharedPref.getBoolean(Constants.DARK_THEME, true)) {
            if (sharedPref.getBoolean(Constants.BLACK_THEME, false)) {
                //TODO: BLACK_THEME
                setTheme(R.style.WeatherAppNight);
            }
            setTheme(R.style.WeatherAppNight);
        } else setTheme(R.style.WeatherAppNight);
    }

    public SharedPreferences getSharedPref() {
        return sharedPref;
    }

    private void setBackground() {
        String url;
        CoordinatorLayout mainLayout = findViewById(R.id.main_layout);
        ImageView img = new ImageView(this);
        if (sharedPref.getBoolean(Constants.DARK_THEME, false)) {
            url = "https://images.unsplash.com/photo-1606156114499-f44bbb400363?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1301&q=80";
        } else {
            url = "https://images.unsplash.com/photo-1607275667966-5923aacbba8f?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1234&q=80";
        }
        Picasso.get().load(url).into(img, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                mainLayout.setBackground(img.getDrawable());
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




    public void getDateInfo(MenuItem item) {
        Uri address = Uri.parse("https://www.calend.ru/narod/");
        Intent linkInet = new Intent(Intent.ACTION_VIEW, address);
        startActivity(linkInet);
    }

    public void startSettings() {
        // Создаем объект Intent для вызова новой Activity
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivityForResult(intent, RESULT_CANCELED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if (resultCode == RESULT_OK) recreate();
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        navController.navigate(id);
        switch (id) {
            case R.id.nav_weather:
                //TODO:
                break;
            case R.id.nav_choose:
                break;
            case R.id.nav_settings:
                //startSettings();
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

//    public void putFragment(int id, Fragment fragment){
//        //TODO: решить проблему с накладыванием фрагмента!
//        Fragment fragment = (Fragment) findViewById(R.id.fragment_main);
//        getSupportFragmentManager().beginTransaction()
//        getSupportFragmentManager().beginTransaction().replace(id, fragment).addToBackStack(null).commit();
//
//    }

}