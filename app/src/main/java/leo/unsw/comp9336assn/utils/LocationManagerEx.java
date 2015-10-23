package leo.unsw.comp9336assn.utils;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;

/**
 * Created by LeoPC on 2015/9/1.
 */
abstract public class LocationManagerEx {

    final int TYPE_NETWORK = 0x0001;
    final int TYPE_GPS = 0x0002;
    final int TYPE_BOTH = 0x0003;

    private LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    Context context;
    private LocationListener networkListener;
    private LocationListener gpsListener;
    LogManagerEx logManager;
    Location location;

    public LocationManagerEx(Context context) {
        this.context = context;
        logManager = LogManagerEx.getInstance();
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        initListener();
    }


    private void initListener() {
        networkListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                LocationManagerEx.this.onLocationChanged(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        break;
                }
                LocationManagerEx.this.onStatusChanged(provider, status, extras);
            }

            /**
             * GPS开启时触发
             */
            public void onProviderEnabled(String provider) {
                location = locationManager.getLastKnownLocation(provider);
                LocationManagerEx.this.onProviderEnabled(provider);
            }

            /**
             * GPS禁用时触发
             */
            public void onProviderDisabled(String provider) {
                LocationManagerEx.this.onProviderDisabled(provider);
            }


        };

        gpsListener = new LocationListener() {

            public void onLocationChanged(Location location) {
                LocationManagerEx.this.onLocationChanged(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        break;
                }
                LocationManagerEx.this.onStatusChanged(provider, status, extras);
            }

            /**
             * GPS开启时触发
             */
            public void onProviderEnabled(String provider) {
                location = locationManager.getLastKnownLocation(provider);
                LocationManagerEx.this.onProviderEnabled(provider);
            }

            /**
             * GPS禁用时触发
             */
            public void onProviderDisabled(String provider) {
                LocationManagerEx.this.onProviderDisabled(provider);
            }
        };
    }

    public Location getLocation() {
        Location networktLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location gpsLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(gpsLocation != null){
            return gpsLocation;
        }else{
            return networktLocation;
        }
    }

    public void requestLocationUpdates() {
        requestLocationUpdates(5000, 0, TYPE_BOTH);
    }


    public void requestLocationUpdates(int mimTime, int mimDistance, int type) {
        switch (type) {
            case TYPE_BOTH:
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mimTime, mimDistance, networkListener);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mimTime, mimDistance, gpsListener);
                break;
            case TYPE_GPS:
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, mimTime, mimDistance, gpsListener);
                break;
            case TYPE_NETWORK:
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, mimTime, mimDistance, networkListener);
                break;
        }
    }

    public void removeUpdates(){
        locationManager.removeUpdates(gpsListener);
        locationManager.removeUpdates(networkListener);
    }


    public boolean isGPSEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public boolean isNetworkEnabled() {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    abstract public void onLocationChanged(Location location);

    abstract public void onStatusChanged(String provider, int status, Bundle extras);

    abstract public void onProviderEnabled(String provider);

    abstract public void onProviderDisabled(String provider);
}
