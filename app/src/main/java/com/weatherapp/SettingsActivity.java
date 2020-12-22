package com.weatherapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;
import androidx.preference.SwitchPreferenceCompat;

import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends BaseActivity {

    private static SharedPreferences sharedPref;

    private static Boolean isSmthChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = getSharedPreferences(getPackageName(), MODE_PRIVATE);
        applySetting();


        loadState();


        setContentView(R.layout.settings_activity);
        Snackbar.make(findViewById(R.id.settings), "Вы зашли в настройки" , Snackbar.LENGTH_LONG).show();

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //Добавление кнопки "назад" в ActionBar
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void applySetting() {
        if (sharedPref.getBoolean(Constants.DARK_THEME, false)) {
            if (sharedPref.getBoolean(Constants.BLACK_THEME, false)) {
                //TODO: BLACK_THEME
                setTheme(R.style.WeatherAppNight);
            }
            setTheme(R.style.WeatherAppNight);
        } else setTheme(R.style.WeatherAppNight);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (isSmthChanged) {
            isSmthChanged = false;
            setResult(RESULT_OK, intent);
        } else setResult(RESULT_CANCELED);
        finish();
        super.onBackPressed();
    }

    private static void setSetting(String key, boolean bool) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(key, bool);
        editor.apply();
        isSmthChanged = true;
    }

    private static void setSetting(String key, String s) {
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, s);
        editor.apply();
    }

    //Обработка нажатия кнопки "назад" в ActionBar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //запуск экрана настроек
    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            SwitchPreferenceCompat darkmode_switch = (SwitchPreferenceCompat) findPreference("dark_mode");
            darkmode_switch.setChecked(sharedPref.getBoolean(Constants.DARK_THEME, false));

            SwitchPreferenceCompat blackmode_switch = (SwitchPreferenceCompat) findPreference("black_mode");
            blackmode_switch.setChecked(sharedPref.getBoolean(Constants.BLACK_THEME, false));

            ListPreference temperature = (ListPreference) findPreference("units");
            temperature.setValue(sharedPref.getString(Constants.TEMP_UNIT, getString(R.string.celcius)));

            darkmode_switch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    setSetting(Constants.DARK_THEME, (Boolean) newValue);
                    return true;
                }
            });
            blackmode_switch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    setSetting(Constants.BLACK_THEME, (Boolean) newValue);
                    return true;
                }
            });
            //TODO: сделать переключение единиц измерения по кнопке
            temperature.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    setSetting(Constants.TEMP_UNIT, (String) newValue);
                    return true;
                }
            });
        }
    }

    //Проверка переключателя темной темы
    //TODO: доделать после изучения
    private Boolean loadState() {
        SharedPreferences sharedPreferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        return sharedPreferences.getBoolean("DARK_MODE", false);
    }
}