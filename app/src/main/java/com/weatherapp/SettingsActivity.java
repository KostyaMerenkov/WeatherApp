package com.weatherapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.google.android.material.snackbar.Snackbar;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            Preference darkmode_switch = (Preference) findPreference("dark_mode");
            //TODO: сделать переключение темы приложения по выключателю

            //TODO: сделать переключение единиц измерения по кнопке
        }
    }

    //Проверка переключателя темной темы
    //TODO: доделать после изучения
    private Boolean loadState() {
        SharedPreferences sharedPreferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        return sharedPreferences.getBoolean("DARK_MODE", false);
    }
}