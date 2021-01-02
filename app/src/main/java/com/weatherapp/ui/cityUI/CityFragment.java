package com.weatherapp.ui.cityUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
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

import com.weatherapp.R;
import com.weatherapp.model.App;
import com.weatherapp.model.Constants;
import com.weatherapp.model.database.City;
import com.weatherapp.model.database.CityDao;
import com.weatherapp.model.database.CitySource;
import com.weatherapp.ui.MainActivity;

import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment representing a list of Items.
 */
public class CityFragment extends Fragment {
    private MainActivity activity;

    private String[] listCity;
    private View rootView;

    private AutoCompleteTextView actv;
    private SharedPreferences sharedPref;

    private RecyclerView listViewCity;
    private MycityRecyclerViewAdapter adapter;
    private CitySource citySource;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_city_list, container, false);

        activity = (MainActivity) getActivity();

        sharedPref = activity.getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE);

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
                    if(Arrays.asList(cities).contains(actv.getText().toString().trim())) {
                        //TODO: check city for valid
                        String chosen_city = actv.getText().toString().trim();
                        addCityToHistory(chosen_city);
                        activity.OnCityChoose(chosen_city);
                    } else {
                        Toast.makeText(getContext(), "Введен неверный город!", Toast.LENGTH_SHORT).show();
                    }
                    return true;

                }
                return false;
            }
        });
    }

}