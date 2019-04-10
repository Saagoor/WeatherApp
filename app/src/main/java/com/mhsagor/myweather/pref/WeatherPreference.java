package com.mhsagor.myweather.pref;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class WeatherPreference {
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String UNITS = "units";
    private SharedPreferences sp;

    public WeatherPreference(Context context){
        sp = context.getSharedPreferences("weatherPref", Context.MODE_PRIVATE);
    }
    public void setLatLng(String lat, String lng){
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(LATITUDE, lat);
        spEditor.putString(LONGITUDE, lng);
        spEditor.apply();
    }
    public Map<String, String> getLatLng(){
        Map<String, String> latLng = new HashMap<>();
        latLng.put("lat", sp.getString(LATITUDE, "0.00"));
        latLng.put("lng", sp.getString(LONGITUDE, "0.00"));
        return latLng;
    }

    public void setUnits(String u){
        SharedPreferences.Editor spEditor = sp.edit();
        spEditor.putString(UNITS, u);
        spEditor.apply();
    }
    public String getUnits(){
        return sp.getString(UNITS, "metric");
    }
}
