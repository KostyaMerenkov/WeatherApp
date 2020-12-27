package com.weatherapp.ui.cityUI;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.weatherapp.R;

import java.util.List;

public class MycityRecyclerViewAdapter extends RecyclerView.Adapter<MycityRecyclerViewAdapter.ViewHolder> {

    private final List<String> cities;
    private OnItemClickListener itemClickListener;
    private int selectedPosition;

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void SetOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setSelectedPosition(int i) {
        selectedPosition = i;
    }

    public MycityRecyclerViewAdapter(List<String> items) {
        cities = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.cityView.setText(cities.get(position));
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView cityView;

        public ViewHolder(View view) {
            super(view);
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

    }
}