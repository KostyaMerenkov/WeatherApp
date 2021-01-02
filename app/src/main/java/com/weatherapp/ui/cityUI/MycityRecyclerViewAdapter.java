package com.weatherapp.ui.cityUI;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weatherapp.R;
import com.weatherapp.model.Constants;
import com.weatherapp.model.database.City;
import com.weatherapp.model.database.CitySource;

import java.util.List;

public class MycityRecyclerViewAdapter extends RecyclerView.Adapter<MycityRecyclerViewAdapter.ViewHolder> {

    private Activity activity;
    //private final List<String> cities;
    private OnItemClickListener itemClickListener;
    // Источник данных
    private CitySource dataSource;
    // Позиция в списке, на которой было нажато меню
    private int selectedPosition;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MycityRecyclerViewAdapter(CitySource dataSource, Activity activity) {
        this.dataSource = dataSource;
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // Заполняем данными записи на экране
        List<City> cities = dataSource.getCities();
        if (cities != null) {
            City city = cities.get(position);
            holder.cityView.setText(city.getCityName());

            // Тут определим, в каком пункте меню было нажато
            holder.cardView.setOnLongClickListener(view -> {
                selectedPosition = position;
                return false;
            });

            // Регистрируем контекстное меню
            activity.registerForContextMenu(holder.cardView);


        }
    }

    @Override
    public void onViewRecycled(@NonNull ViewHolder holder) {
        holder.cardView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return (int) dataSource.getCountCities();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {

        public final TextView cityView;
        View cardView;

        public ViewHolder(View view) {
            super(view);
            cardView = view;
            cityView = (TextView) view.findViewById(R.id.content);

            cityView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onItemClick(v, getAdapterPosition());
                    }
                }
            });
        }

        private void onLongClick() {
            itemView.showContextMenu();
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, Constants.CONTEXT_MENU_DELETE, Menu.NONE, R.string.delete_item);
        }
    }
}