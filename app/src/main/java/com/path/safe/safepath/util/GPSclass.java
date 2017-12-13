package com.path.safe.safepath.util;

/**
 * Created by Lupscita on 10/12/2017.
 */

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.location.LocationListener;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.karan.churi.PermissionManager.PermissionManager;
import com.path.safe.safepath.MainActivityLogin;
import com.path.safe.safepath.R;

import java.util.List;

public class GPSclass {
    public Context context;
    private LocationManager locationManager;
    private Location currentLocation;
    private  String providerGPS;


    public GPSclass(Context context) {
        this.context = context;
        Configuration.miPosicion = null;
        currentLocation = getLastKnownLocation();
        if (currentLocation != null) {
            Log.e("Ultima Localización: ", currentLocation.toString());
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.e("GPS ","No esta activado el GPS!!");
            Toast.makeText(context, "Se necesita darle permisos a tu GPS!!", Toast.LENGTH_LONG).show();
            return;
        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 0, changeLocation());
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(" Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("ّyes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("no", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public Location getPosition() {
        return currentLocation;
    }

    private Location getLastKnownLocation() {
        locationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location mejorPosicion = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;}
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) continue;
            if (mejorPosicion == null || l.getAccuracy() < mejorPosicion.getAccuracy()) {
                mejorPosicion = l;
                providerGPS = provider;
            }
        }
        boolean isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!isGPSenabled){
            buildAlertMessageNoGps();
        }
        return mejorPosicion;
    }

    //Puede ser usado en el constructor de la clase por el provider
    public Location initService(){
        locationManager = (LocationManager)this.context.getSystemService(Context.LOCATION_SERVICE);
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);
        Location mejorPosicion = null;
        String provider = locationManager.getBestProvider(c,true);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;}
        boolean isGPSenabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(isGPSenabled){
            Location l = locationManager.getLastKnownLocation(provider);
            mejorPosicion = l;
        }else{
            buildAlertMessageNoGps();
        }
        return mejorPosicion;
    }

    private LocationListener changeLocation(){

        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.e("onLocationChanged", location.toString());
                currentLocation = location;
                if(Configuration.miPosicion  != null)
                    Configuration.miPosicion.setPosition(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                //Log.e("onStatusChanged", "Cambio el estado: " + provider + "Estado: " + status);
            }
            @Override
            public void onProviderEnabled(String provider) {
                //Log.e("onProviderEnabled", "Proveedor Habilitado: " + provider);
            }
            @Override
            public void onProviderDisabled(String provider) {
                //Log.e("onProviderDisabled", "Proveedor Desabilitado: " + provider);
            }
        };
    }
}