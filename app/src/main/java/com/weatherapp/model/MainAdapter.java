package com.weatherapp.model;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.weatherapp.R;

// Адаптер
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private SocialDataSource dataSource;

    private OnItemClickListener itemClickListener;  // Слушатель будет устанавливаться извне


    // Передаем в конструктор источник данных
    // В нашем случае это массив, но может быть и запросом к БД
    public MainAdapter(SocialDataSource dataSource){
        this.dataSource = dataSource;
    }

    // Создать новый элемент пользовательского интерфейса
    // Запускается менеджером
    @NonNull
    @Override
    public MainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        // Создаем новый элемент пользовательского интерфейса
        // Через Inflater
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.daily_weather, viewGroup, false);
        // Здесь можно установить всякие параметры
        ViewHolder vh = new ViewHolder(v);
        if (itemClickListener != null) {
            vh.setOnClickListener(itemClickListener);
        }
        if (Constants.DEBUG) {
            Log.d("SocnetAdapter", "onCreateViewHolder");
        }
        return vh;

    }

    // Заменить данные в пользовательском интерфейсе
    // Вызывается менеджером
    @Override
    public void onBindViewHolder(@NonNull MainAdapter.ViewHolder viewHolder, int i) {
        // Получить элемент из источника данных (БД, интернет...)
        // Вынести на экран используя ViewHolder
        MainSoc mainSoc = dataSource.getSoc(i);
        viewHolder.setData(mainSoc.getDate(), mainSoc.getPicture(), mainSoc.getTemp());
        if (Constants.DEBUG) {
            Log.d("SocnetAdapter", "onBindViewHolder");
        }

    }

    // Вернуть размер данных, вызывается менеджером
    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    // Интерфейс для обработки нажатий как в ListView
    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    // Сеттер слушателя нажатий
    public void SetOnItemClickListener(OnItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }


    // Этот класс хранит связь между данными и элементами View
    // Сложные данные могут потребовать несколько View на
    // один пункт списка
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView date;
        private ImageView image;
        private TextView temp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.textDate);
            image = itemView.findViewById(R.id.rv_image);
            temp = itemView.findViewById(R.id.textTemp);

        }

        public void setOnClickListener(final OnItemClickListener listener) {
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Получаем позицию адаптера
                    int adapterPosition = getAdapterPosition();
                    // Проверяем ее на корректность
                    if (adapterPosition == RecyclerView.NO_POSITION) return;
                    listener.onItemClick(v, adapterPosition);
                }
            });
        }

        public void setData(String date, int picture, String temp) {
            getTemp().setText(temp);
            getImage().setImageResource(picture);
            getDate().setText(date);
        }

        public TextView getTemp() {
            return temp;
        }

        public TextView getDate() {
            return date;
        }

        public ImageView getImage() {
            return image;
        }
    }
}
