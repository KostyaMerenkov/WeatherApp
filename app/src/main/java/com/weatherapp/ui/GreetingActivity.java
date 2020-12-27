package com.weatherapp.ui;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.weatherapp.R;
import com.weatherapp.model.Constants;
import com.weatherapp.ui.cityUI.CtyInfoFragment;

import java.util.Arrays;

public class GreetingActivity extends AppCompatActivity {

    public final static String TAG = "CHOOSE_CITY";
    private Boolean isExistChooseCityFragment = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);
        AutoCompleteTextView editText = findViewById(R.id.actv);
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.showDropDown();
                editText.requestFocus();
                return false;
            }
        });
        String[] cities = getResources().getStringArray(R.array.cities);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, cities);
        editText.setAdapter(adapter);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH))
                        || (actionId == EditorInfo.IME_ACTION_SEARCH)) {
                    if(Constants.DEBUG){
                        Toast.makeText(getApplicationContext(), "Нажата кнопка SEARCH", Toast.LENGTH_SHORT).show();
                    }
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(editText.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) putFragment(findViewById(R.id.actv));
                    else {
                        weather(editText);
                    }
                    return true;

                }
                return false;
            }
        });

        if (Constants.DEBUG) {
            Toast.makeText(getApplicationContext(), "onCreate()", Toast.LENGTH_SHORT).show();
            detectOrientation();
        }
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

    public void weather(View view) {

        // Получаем текстовое поле в текущей Activity
        EditText editText = (EditText) findViewById(R.id.actv);
        // Получаем текст данного текстового поля
        String message = editText.getText().toString().trim();
        //Проверяем на правильно введенный город
        if(isCityCorrect(message)) {
            // Создаем объект Intent для вызова новой Activity
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(Constants.CITY_MESSAGE, message);
            // запуск activity
            SharedPreferences sharedPref = getSharedPreferences(getPackageName(), MODE_PRIVATE);
            sharedPref.edit().putString(Constants.CITY_MESSAGE, message).apply();
            startActivity(intent);
        }
        else setErrorDialog(message);
    }

    private void setErrorDialog(String message) {
        // Создаём билдер и передаём контекст приложения
        AlertDialog.Builder builder = new AlertDialog.Builder(GreetingActivity.this);
        // В билдере указываем заголовок окна (можно указывать как ресурс,
        // так и строку)
        builder.setTitle(R.string.city_not_found)
                // Указываем сообщение в окне (также есть вариант со
                // строковым параметром)
                .setMessage(R.string.try_again)
                // Можно указать и пиктограмму
                .setIcon(android.R.drawable.ic_dialog_alert)
                // Из этого окна нельзя выйти кнопкой Back
                .setCancelable(false)
                // Устанавливаем кнопку (название кнопки также можно
                // задавать строкой)
                .setNeutralButton(R.string.button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private Boolean isCityCorrect(String message) {
        return Arrays.asList(getResources().getStringArray(R.array.cities)).contains(message);
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

    public void putFragment(View view){

            Fragment fragment = new CtyInfoFragment();
            EditText editText = (EditText) findViewById(R.id.actv);
            Bundle bundle = new Bundle();
            bundle.putString(Constants.FRAGMENT_CITY, editText.getText().toString());
            //TODO: set temp from URL
            bundle.putString(Constants.FRAGMENT_TEMP, "+18°");
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.fr_choose_info, fragment).commit();
            isExistChooseCityFragment = true;
    }
}