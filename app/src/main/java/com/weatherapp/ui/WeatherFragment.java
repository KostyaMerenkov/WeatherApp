package com.weatherapp.ui;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.weatherapp.BuildConfig;
import com.weatherapp.R;
import com.weatherapp.model.App;
import com.weatherapp.model.Constants;
import com.weatherapp.model.mainWeather.WeatherAdapter;
import com.weatherapp.model.mainWeather.MainSourceBuilder;
import com.weatherapp.model.mainWeather.WeatherDataSource;
import com.weatherapp.model.weatherData.ApiHolder;
import com.weatherapp.model.weatherData.WeatherRequest;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherFragment extends Fragment {
    private View rootView;
    private MainActivity activity;

    private String city_name;
    private TextView city;
    private TextView temperature;
    private TextView windSpeed;
    private TextView humidity;
    private TextView pressure;
    private TextView clouds;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);



        // RecyclerView
        // строим источник данных
        WeatherDataSource sourceData = new MainSourceBuilder()
                .setResources(getResources())
                .build();
        initRecyclerView(rootView, sourceData);

        //String city = rootView.sharedPref.getString(Constants.CITY_MESSAGE, null);
        requestRetrofit(((MainActivity) getActivity()).getSharedPref().getString(Constants.CITY_MESSAGE, "Moscow"), BuildConfig.WEATHER_API_KEY);
        //activity.getSharedPref().getString(Constants.CITY_MESSAGE, "Moscow")
        // Inflate the layout for this fragment
        return rootView;
    }

    private void initRecyclerView(View rootView, WeatherDataSource sourceData){
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);



        requestRetrofit(city_name, BuildConfig.WEATHER_API_KEY);

        // Эта установка служит для повышения производительности системы
        recyclerView.setHasFixedSize(true);

        // Добавим разделитель карточек
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getContext(),  LinearLayoutManager.HORIZONTAL);
        itemDecoration.setDrawable(getResources().getDrawable(R.drawable.separator));
        recyclerView.addItemDecoration(itemDecoration);


        // Будем работать со встроенным менеджером
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Установим адаптер
        WeatherAdapter adapter = new WeatherAdapter(sourceData);

        recyclerView.setAdapter(adapter);

        //SharedPreferences sharedPref =



        if (Constants.DEBUG) {
            // Установим слушателя
            adapter.SetOnItemClickListener(new WeatherAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {

                    Toast.makeText(getActivity(), String.format("Позиция - %d", position), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Инициализируем пользовательские элементы
    private void initGui() {
        city = (TextView) rootView.findViewById(R.id.cityView);
        temperature = (TextView) rootView.findViewById(R.id.main_tempView);
        windSpeed = (TextView) rootView.findViewById(R.id.wind_info);
        humidity = (TextView) rootView.findViewById(R.id.humidity_info);
        pressure = (TextView) rootView.findViewById(R.id.pressure_info);
        clouds = (TextView) rootView.findViewById(R.id.textView3);
    }

    private void requestRetrofit(String city, String keyApi) {
        ApiHolder apiHolder = App.getApiHolder();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                                getActivity().runOnUiThread(new Runnable()
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

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("Temp", (String) temperature.getText());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (MainActivity) getActivity();
        initGui();
        if (temperature == null) {
            temperature.setText(savedInstanceState.getInt("Temp"));
        }

    }

    public void setWeather(Response<WeatherRequest> response) {
        city.setText(response.body().getName());
        temperature.setText(String.format("%d + \"°\"", (int) response.body().getMain().getTemp()-273));
        pressure.setText(String.format(getString(R.string.pressure), response.body().getMain().getPressure()));
        humidity.setText(String.format(getString(R.string.humidity), response.body().getMain().getHumidity())+"%");
        windSpeed.setText(String.format(getString(R.string.wind), (int) response.body().getWind().getSpeed()));
        clouds.setText(response.body().getWeather()[0].getDescription());
    }

}