package com.mhsagor.myweather;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mhsagor.myweather.pref.WeatherPreference;

public class MainActivity extends AppCompatActivity implements
        ForecastWeatherFragment.OnFragmentInteractionListener,
        CurrentWeatherFragment.OnFragmentInteractionListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private WeatherPagerAdapter adapter;
    private boolean isLocationPermissionGranted = false;
    private FusedLocationProviderClient client;
    private double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        tabLayout.addTab(tabLayout.newTab().setText("Current").setIcon(R.drawable.ic_sun));
        tabLayout.addTab(tabLayout.newTab().setText("Forecast").setIcon(R.drawable.ic_list));
        tabLayout.setBackgroundColor(Color.WHITE);
        tabLayout.setSelectedTabIndicatorColor(Color.GRAY);
        adapter = new WeatherPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        client = LocationServices.getFusedLocationProviderClient(this);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        WeatherPreference wp = new WeatherPreference(this);
        if(wp.getUnits().equals("imperial")){
            menu.getItem(0).setTitle("°F");
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_units:
                changeUnits(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void changeUnits(MenuItem item) {
        WeatherPreference wp = new WeatherPreference(this);
        if(item.getTitle().equals("°C")){
            wp.setUnits("imperial");
            item.setTitle("°F");
        }else{
            wp.setUnits("metric");
            item.setTitle("°C");
        }
        //viewPager.setAdapter(adapter);
        viewPager.getAdapter().notifyDataSetChanged();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 7);
        } else {
            isLocationPermissionGranted = true;
            getDeviceCurrentLocation();
        }
    }

    private void getDeviceCurrentLocation() {
        if (isLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                Toast.makeText(this, "Failed to find location", Toast.LENGTH_SHORT).show();
                return;
            }
            client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if(location == null){
                        Toast.makeText(MainActivity.this, "Unable to find location", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    WeatherPreference weatherPreference = new WeatherPreference(MainActivity.this);
                    weatherPreference.setLatLng(String.valueOf(latitude), String.valueOf(longitude));
                }
            });
        }
    }


    private class WeatherPagerAdapter extends FragmentPagerAdapter{

        WeatherPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i){
                case 0:
                    return new CurrentWeatherFragment();
                case 1:
                    return new ForecastWeatherFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabLayout.getTabCount();
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 7 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            isLocationPermissionGranted = true;
            getDeviceCurrentLocation();
            viewPager.getAdapter().notifyDataSetChanged();
        }else{
            Toast.makeText(this, "Please allow location permission", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFragmentInteraction() {
        checkLocationPermission();
        Log.e("MainActivity", "Fragments callback called.");
    }
}
