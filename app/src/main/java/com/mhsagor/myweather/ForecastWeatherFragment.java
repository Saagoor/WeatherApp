package com.mhsagor.myweather;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mhsagor.myweather.pref.WeatherPreference;
import com.mhsagor.myweather.weatherapi.RetrofitClient;
import com.mhsagor.myweather.weatherapi.WeatherApiService;
import com.mhsagor.myweather.weatherapi.forecast.ForecastAdapter;
import com.mhsagor.myweather.weatherapi.forecast.ForecastResource;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ForecastWeatherFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private RecyclerView forecastRV;
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";
    private String units;

    public ForecastWeatherFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_forecast_weather, container, false);
    }


    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListener.onFragmentInteraction();
        forecastRV = view.findViewById(R.id.forecastRV);
        getForecastWeatherData();
    }

    private void getForecastWeatherData(){
        WeatherPreference weatherPreference = new WeatherPreference(getContext());
        units = weatherPreference.getUnits();
        Map<String, String> latLng = weatherPreference.getLatLng();
        Log.e("Forecast weather", latLng.get("lat")+", "+latLng.get("lng"));
        String apiKey = getString(R.string.weather_api_key);
        String queryUrl = String.format("forecast?lat=%s&lon=%s&cnt=40&units=%s&appid=%s", latLng.get("lat"), latLng.get("lng"), units, apiKey);

        WeatherApiService weatherApiService = RetrofitClient
                .getClient(BASE_URL)
                .create(WeatherApiService.class);
        weatherApiService.getForecastResource(queryUrl)
                .enqueue(new Callback<ForecastResource>() {
                    @Override
                    public void onResponse(Call<ForecastResource> call, Response<ForecastResource> response) {
                        if (response.isSuccessful()){
                            Log.e("Forecast weather", "Api request success!");
                            ForecastResource forecastResource = response.body();
                            if(forecastResource.getList() != null){
                                LinearLayoutManager llm = new LinearLayoutManager(getContext());
                                ForecastAdapter adapter = new ForecastAdapter(getActivity(), forecastResource.getList());
                                forecastRV.setLayoutManager(llm);
                                forecastRV.setAdapter(adapter);
                            }else{
                                Toast.makeText(getActivity(), "Weather forecast data not found!", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Log.e("Forecast weather", "Response not successful");
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastResource> call, Throwable t) {
                        Toast.makeText(getContext(), "Api request failed", Toast.LENGTH_SHORT).show();
                        Log.e("Forecast weather", "Api req failed! "+t.getLocalizedMessage());
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction();
    }
}
