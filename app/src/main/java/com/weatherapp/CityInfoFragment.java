package com.weatherapp;

import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class CityInfoFragment extends Fragment {


    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_city_info, container, false);


        TextView temp = (TextView) view.findViewById(R.id.temperature_info);
        TextView city = (TextView) view.findViewById(R.id.city_info);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String frag_city = bundle.getString(Constants.FRAGMENT_CITY);
            String frag_temp = bundle.getString(Constants.FRAGMENT_TEMP);
            temp.setText(frag_temp);
            city.setText(frag_city);
        }

        return view;
    }

}