package com.mhsagor.myweather.weatherapi;

import com.mhsagor.myweather.weatherapi.current.WeatherApi;
import com.mhsagor.myweather.weatherapi.forecast.ForecastResource;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherApiService {
    @GET
    Call<WeatherApi> getWeatherResource(@Url String url);
    @GET
    Call<ForecastResource> getForecastResource(@Url String url);
}
