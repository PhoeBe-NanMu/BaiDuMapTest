package com.example.baidumaptest;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MapView mapView;
    private BaiduMap baiduMap;
    private LocationManager locationManager;
    private String provider;
    private boolean isFirstLocate = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        mapView = (MapView) findViewById(R.id.mapView);
        baiduMap = mapView.getMap();
        baiduMap.setMyLocationEnabled(true);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        if (providerList.contains(locationManager.GPS_PROVIDER)) {
            provider = locationManager.GPS_PROVIDER;
        } else if (providerList.contains(locationManager.NETWORK_PROVIDER)) {
            provider = locationManager.NETWORK_PROVIDER;
        } else {
            Toast.makeText(this, "Could not find location provider", Toast.LENGTH_SHORT).show();
            return;
        }
        /*注意添加 Location 对应权限*/
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            showLocation(location);
        } else {
            Log.i("info" ,"location is null");
        }

        locationManager.requestLocationUpdates(provider,5000,1,locationListener);
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                showLocation(location);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };


    private void showLocation(Location location) {
        if (isFirstLocate) {
            Log.i("info","纬度："+location.getLatitude()+"\n经度："+location.getLongitude());
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            MapStatusUpdate mapStatusUpdate = MapStatusUpdateFactory.newLatLng(latLng);
            baiduMap.animateMapStatus(mapStatusUpdate);
            mapStatusUpdate = MapStatusUpdateFactory.zoomTo(14f);
            baiduMap.animateMapStatus(mapStatusUpdate);
            isFirstLocate = false;
        }
        MyLocationData.Builder myLocationDataBuilder = new MyLocationData.Builder();
        myLocationDataBuilder.latitude(location.getLatitude());
        myLocationDataBuilder.longitude(location.getLongitude());
        MyLocationData myLocationData = myLocationDataBuilder.build();
        baiduMap.setMyLocationData(myLocationData);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        baiduMap.setBuildingsEnabled(false);
        mapView.onDestroy();
        //关闭时将监听器移除
        if (locationManager !=null ) {
            locationManager.removeUpdates(locationListener);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }
}
