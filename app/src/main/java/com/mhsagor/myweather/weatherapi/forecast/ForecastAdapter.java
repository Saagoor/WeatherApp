package com.mhsagor.myweather.weatherapi.forecast;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mhsagor.myweather.R;
import com.mhsagor.myweather.pref.WeatherPreference;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder> {
    private List<com.mhsagor.myweather.weatherapi.forecast.List> weatherList;
    private String units;

    public ForecastAdapter(Context context, List<com.mhsagor.myweather.weatherapi.forecast.List> weatherList) {
        this.weatherList = weatherList;
        WeatherPreference wp = new WeatherPreference(context);
        this.units = wp.getUnits();
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_forecast_weather, viewGroup, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder forecastViewHolder, int i) {
        com.mhsagor.myweather.weatherapi.forecast.List weather = weatherList.get(i);
        String tempUnit = units.equals("metric") ? "°C" : "°F";
        long date = weather.getDt() * 1000;
        forecastViewHolder.timeTV.setText(getTime(date));
        forecastViewHolder.dayTV.setText(getDay(date));
        forecastViewHolder.dateTV.setText(getDate(date));
        forecastViewHolder.maxTempTV.setText(String.valueOf(Math.round(weather.getMain().getTempMax())+tempUnit));
        forecastViewHolder.minTempTV.setText(String.valueOf(Math.round(weather.getMain().getTempMin())+tempUnit));
        String desc = weather.getWeather().get(0).getDescription();
        desc = String.valueOf(desc.charAt(0)).toUpperCase() + desc.subSequence(1, desc.length());
        forecastViewHolder.descTV.setText(desc);
        forecastViewHolder.humidityTV.setText(String.valueOf(weather.getMain().getHumidity()+"%"));
        Picasso.get().load("https://openweathermap.org/img/w/"+weather.getWeather().get(0).getIcon()+".png")
                .into(forecastViewHolder.iconIV);
    }

    @Override
    public int getItemCount() {
        return weatherList.size();
    }

    class ForecastViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconIV;
        private TextView timeTV, dayTV, dateTV, maxTempTV, minTempTV, descTV, humidityTV;
        ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            iconIV = itemView.findViewById(R.id.row_icon);
            timeTV = itemView.findViewById(R.id.row_time);
            dayTV = itemView.findViewById(R.id.row_day);
            dateTV = itemView.findViewById(R.id.row_date);
            maxTempTV = itemView.findViewById(R.id.row_maxTemp);
            minTempTV = itemView.findViewById(R.id.row_minTemp);
            descTV = itemView.findViewById(R.id.row_description);
            humidityTV = itemView.findViewById(R.id.row_humidity);
        }
    }

    private String getTime(long millisecond){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date date = new Date(millisecond);
        return sdf.format(date);
    }
    private String getDay(long millisecond){
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date date = new Date(millisecond);
        return sdf.format(date);
    }
    private String getDate(long millisecond){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
        Date date = new Date(millisecond);
        return sdf.format(date);
    }
}
