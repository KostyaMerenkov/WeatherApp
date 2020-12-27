package com.weatherapp.ui.cityUI;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.material.snackbar.Snackbar;
import com.weatherapp.R;
import com.weatherapp.model.Constants;
import com.weatherapp.model.WeatherAdapter;
import com.weatherapp.model.weatherData.City;
import com.weatherapp.ui.MainActivity;

import java.util.ArrayList;
import java.util.Arrays;

import static android.content.Context.MODE_PRIVATE;

/**
 * A fragment representing a list of Items.
 */
public class CityFragment extends Fragment {
    private MainActivity activity;

    private String[] listCity;
    private View rootView;
    private MycityRecyclerViewAdapter adapter;
    private SearchView searchView;
    private SharedPreferences sharedPref;

    private RecyclerView listViewCity;

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

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_city_list, container, false);

        activity = (MainActivity) getActivity();

        sharedPref = activity.getSharedPreferences(getActivity().getPackageName(), MODE_PRIVATE);

        listViewCity = rootView.findViewById(R.id.ListViewCity);
        listCity = getResources().getStringArray(R.array.cities);
        listViewCity.setHasFixedSize(true);
        listViewCity.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MycityRecyclerViewAdapter(Arrays.asList(listCity));
        adapter.SetOnItemClickListener(new MycityRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                searchView.setQuery("", false);
                searchView.clearFocus();
                searchView.setIconified(true);
                activity.OnCityChoose(listCity[position]);
            }
        });
        listViewCity.setAdapter(adapter);


        searchView = (SearchView) ((MainActivity)getActivity()).getToolbar().getMenu().findItem(R.id.app_bar_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String searchingCity = query.trim();
                if (Arrays.asList(listCity).contains(searchingCity)) {
                    int i = Arrays.asList(listCity).indexOf(searchingCity);
                    listViewCity.smoothScrollToPosition(i);
                    adapter.setSelectedPosition(i);
                    adapter.notifyDataSetChanged();
                } else {
                    Snackbar.make(rootView, "Город не найден!", Snackbar.LENGTH_LONG)
                            .show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return rootView;
    }

}