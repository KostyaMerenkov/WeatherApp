package com.weatherapp.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weatherapp.R;
import com.weatherapp.model.Constants;


public class cityInfoFragment extends Fragment {


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