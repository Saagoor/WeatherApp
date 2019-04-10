package com.mhsagor.myweather;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mhsagor.myweather.databinding.FragmentCurrentWeatherBinding;
import com.mhsagor.myweather.pref.WeatherPreference;
import com.mhsagor.myweather.weatherapi.RetrofitClient;
import com.mhsagor.myweather.weatherapi.WeatherApiService;
import com.mhsagor.myweather.weatherapi.current.WeatherApi;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrentWeatherFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private static final String WEATHER_BASE_URL = "http://api.openweathermap.org/data/2.5/";
    private String latitude, longitude;
    private FragmentCurrentWeatherBinding binding;
    private String units;

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener){
            mListener = (OnFragmentInteractionListener) context;
        }else{
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false);

        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_current_weather, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mListener.onFragmentInteraction();
        WeatherPreference weatherPreference = new WeatherPreference(getContext());
        units = weatherPreference.getUnits();
        Map<String, String> latLng = weatherPreference.getLatLng();
        latitude = latLng.get("lat");
        longitude = latLng.get("lng");
        getCurrentWeatherData();
    }

    private void getCurrentWeatherData(){
        String apiKey = getString(R.string.weather_api_key);
        String queryUrl = String.format("weather?lat=%s&lon=%s&units=%s&appid=%s", latitude, longitude, units, apiKey);
        Log.e("Current weather", "Location: "+latitude+", "+longitude);

        WeatherApiService weatherApiService = RetrofitClient.getClient(WEATHER_BASE_URL)
                .create(WeatherApiService.class);
        weatherApiService.getWeatherResource(queryUrl)
                .enqueue(new Callback<WeatherApi>() {
                    @Override
                    public void onResponse(Call<WeatherApi> call, Response<WeatherApi> response) {
                        if (response.isSuccessful()){
                            WeatherApi weatherApi = response.body();
                            if (weatherApi != null){
                                String tempUnit = units.equals("metric") ? "°C" : "°F";
                                binding.headingTV.setText(String.format("Weather in %s, %s",
                                        weatherApi.getName(), weatherApi.getSys().getCountry()));
                                binding.tempTV.setText(String.valueOf(Math.round(weatherApi.getMain().getTemp())+tempUnit));
                                binding.descriptionTV.setText(weatherApi.getWeather().get(0).getMain());
                                SimpleDateFormat sdf = new SimpleDateFormat("hh:mma - EEE, d MMM yyyy", Locale.getDefault());
                                binding.timeTV.setText(sdf.format(new Date(weatherApi.getDt() * 1000)));
                                binding.maxTempTV.setText(String.valueOf(weatherApi.getMain().getTempMax()+tempUnit));
                                binding.minTempTV.setText(String.valueOf(weatherApi.getMain().getTempMin()+tempUnit));
                                binding.windTV.setText(String.format("%s m/s, (%sdeg)", weatherApi.getWind().getSpeed(), weatherApi.getWind().getDeg()));
                                binding.cloudinessTV.setText(weatherApi.getWeather().get(0).getDescription());
                                binding.pressureTV.setText(String.valueOf(weatherApi.getMain().getPressure()+" hpa"));
                                binding.humidityTV.setText(String.valueOf(weatherApi.getMain().getHumidity()+"%"));
                                SimpleDateFormat sunRSdf = new SimpleDateFormat("hh:mma", Locale.getDefault());
                                binding.sunriseTV.setText(sunRSdf.format(new Date(weatherApi.getSys().getSunrise() * 1000)));
                                binding.sunsetTV.setText(sunRSdf.format(new Date(weatherApi.getSys().getSunset() * 1000)));
                                binding.geoCoordsTV.setText(String.format("[%s, %s]", weatherApi.getCoord().getLat(), weatherApi.getCoord().getLon()));
                                Picasso.get()
                                        .load("https://openweathermap.org/img/w/"+weatherApi.getWeather().get(0).getIcon()+".png")
                                        .into(binding.iconIV);

                                //Toast.makeText(getActivity(), "Success", Toast.LENGTH_SHORT).show();
                                Log.e("Current weather", "Current weather data found."+weatherApi.getCoord().getLat());
                            }else{
                                Log.e("Current weather", "Couldn't found weather data.");
                            }
                        }else{
                            Log.e("Current weather", "Response failed.");
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherApi> call, Throwable t) {
                        Log.e("Current weather", "Api request failed. "+t.getLocalizedMessage());
                        //Toast.makeText(getActivity(), "Api request failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public interface OnFragmentInteractionListener{
        void onFragmentInteraction();
    }

}
