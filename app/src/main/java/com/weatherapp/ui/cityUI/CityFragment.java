package com.weatherapp.ui.cityUI;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.LocationSource;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.weatherapp.R;
import com.weatherapp.model.App;
import com.weatherapp.model.Constants;
import com.weatherapp.model.database.City;
import com.weatherapp.model.database.CityDao;
import com.weatherapp.model.database.CitySource;
import com.weatherapp.ui.MainActivity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment representing a list of Items.
 */
public class CityFragment extends Fragment {
    private MainActivity activity;
    private static final int PERMISSION_REQUEST_CODE = 10;

    private String[] listCity;
    private View rootView;

    private AutoCompleteTextView actv;
    private SharedPreferences sharedPref;

    private RecyclerView listViewCity;
    private MycityRecyclerViewAdapter adapter;
    private CitySource citySource;
    private FloatingActionButton fb;

    private LocationManager locationManager;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CityFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CityFragment newInstance(int columnCount) {
        CityFragment fragment = new CityFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case Constants.CONTEXT_MENU_DELETE:
                // Удаляем запись из базы
                City cityForRemove = citySource
                        .getCities()
                        .get((int) adapter.getSelectedPosition());
                citySource.removeCity(cityForRemove.getId());
                adapter.notifyItemRemoved((int) adapter.getSelectedPosition());

                return true;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    // Запрос пермиссий
    private void requestPemissions() {
        // Проверим на пермиссии, и если их нет, запросим у пользователя
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // запросим координаты
            requestLocation();
        } else {
            // пермиссии нет, будем запрашивать у пользователя
            requestLocationPermissions();
        }
    }

    // Запрос координат
    private void requestLocation() {
        // Если пермиссии все таки нет - то просто выйдем, приложение не имеет смысла
        if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        // Получить менеджер геолокаций
        LocationManager locationManager = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);

        // получим наиболее подходящий провайдер геолокации по критериям
        // Но можно и самому назначать какой провайдер использовать.
        // В основном это LocationManager.GPS_PROVIDER или LocationManager.NETWORK_PROVIDER
        // но может быть и LocationManager.PASSIVE_PROVIDER, это когда координаты уже кто-то недавно получил.
        String provider = locationManager.getBestProvider(criteria, true);
        if (provider != null) {
            Toast.makeText(activity.getApplicationContext(), getString(R.string.Define_your_location), Toast.LENGTH_LONG).show();
            // Будем получать геоположение через каждые 10 секунд или каждые 10 метров
            locationManager.requestSingleUpdate(provider, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double lat = location.getLatitude(); // Широта
                    double lng = location.getLongitude(); // Долгота
                    Log.d(MainActivity.class.getSimpleName(), "We are getting lat and long");

                    String latitude = Double.toString(lat);
                    Log.d(Constants.TAG, "textLatitude" + latitude);

                    String longitude = Double.toString(lng);
                    Log.d(Constants.TAG, "textLongitude" + longitude);

                    Locale aLocale;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        aLocale = new Locale.Builder().setLanguage("RU").setScript("Latn").setRegion("RS").build();
                    } else {
                        aLocale = new Locale("RU");
                    }
                    Geocoder geocoder = new Geocoder(activity.getApplicationContext(), aLocale);

                    try {
                        List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
                        String cityName = addresses.get(0).getLocality();

                    locationManager.removeUpdates(this);
                    Log.d(MainActivity.class.getSimpleName(), "The city is: " + cityName);
                    startNewCityFragment(cityName);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Snackbar.make(activity.findViewById(R.id.ListViewCity), R.string.Location_error, Snackbar.LENGTH_SHORT);
                    }

                    String accuracy = Float.toString(location.getAccuracy());   // Точность
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            }, Looper.getMainLooper());
        }
    }

    // Запрос пермиссии для геолокации
    private void requestLocationPermissions() {
        if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                || !ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            // Запросим эти две пермиссии у пользователя
            ActivityCompat.requestPermissions(activity,
                    new String[]{
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
        }
    }

    // Это результат запроса у пользователя пермиссии
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {   // Это та самая пермиссия, что мы запрашивали?
            if (grantResults.length == 2 &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                // Все препоны пройдены и пермиссия дана
                // Запросим координаты
                requestLocation();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_city_list, container, false);

        activity = (MainActivity) getActivity();
        sharedPref = activity.getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE);

        fb = rootView.findViewById(R.id.floatingActionButton);
        fb.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                  // Запрашиваем координаты
                    requestLocation();
                }
                else {
                    // Permission’ов нет, запрашиваем их у пользователя
                    requestLocationPermissions();
                }
            }
        });

        listViewCity = rootView.findViewById(R.id.ListViewCity);
        listViewCity.setHasFixedSize(true);
        listCity = getResources().getStringArray(R.array.cities);
        listViewCity.setLayoutManager(new LinearLayoutManager(getContext()));

        CityDao cityDao = App
                .getInstance()
                .getCityDao();
        citySource = new CitySource(cityDao);

        adapter = new MycityRecyclerViewAdapter(citySource, activity);
        adapter.SetOnItemClickListener(new MycityRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                actv.clearFocus();
                activity.OnCityChoose(citySource.getCities().get((int) adapter.getItemViewType(position)).getCityName());
            }
        });
        listViewCity.setAdapter(adapter);
        actvSetup();
        return rootView;
    }

    private void addCityToHistory(String city) {
        City cityForInsert = new City();
        cityForInsert.setCityName(city);
        // Добавляем студента
        citySource.addCity(cityForInsert);
        adapter.notifyDataSetChanged();

    }


    private void actvSetup() {
        //actv = (AutoCompleteTextView) ((MainActivity)getActivity()).getToolbar().getMenu().findItem(R.id.autoCompleteTextView).getActionView();
        actv = (AutoCompleteTextView) rootView.findViewById(R.id.autoCompleteTextView);


        actv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                actv.showDropDown();
                actv.requestFocus();
                return false;
            }
        });

        String[] cities = getResources().getStringArray(R.array.cities);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, cities);
        actv.setAdapter(adapter);
        actv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_SEARCH))
                        || (actionId == EditorInfo.IME_ACTION_SEARCH)) {
                    if(Constants.DEBUG){
                        Toast.makeText(getContext(), "Нажата кнопка SEARCH", Toast.LENGTH_SHORT).show();
                    }
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(actv.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                    startNewCityFragment(actv.getText().toString().trim());
                    return true;

                }
                return false;
            }
        });
    }

    private void startNewCityFragment(String city){
        String[] cities = getResources().getStringArray(R.array.cities);
        Log.d(Constants.TAG, "Ваш город: ."+city+".");
        if(Arrays.asList(cities).contains(city.trim())) {
            //TODO: check city for valid
            addCityToHistory(city);
            activity.OnCityChoose(city);
        } else {
            Toast.makeText(getContext(), city + " пока еще не добавлен! ^^", Toast.LENGTH_SHORT).show();
        }
    }

}