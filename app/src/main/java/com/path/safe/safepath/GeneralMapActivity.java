package com.path.safe.safepath;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.path.safe.safepath.util.Configuration;
import com.path.safe.safepath.util.GPSclass;

public class GeneralMapActivity extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng locationDefault;

    private GPSclass gps;

    private Location location;
    private LocationManager locationManager;
    private LocationListener locationListener;

    MediaPlayer alarma_notification;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstancestate)
    {
        return  inflater.inflate(R.layout.activity_general_map,container,false);
    }

    //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment fragment = (MapFragment)getChildFragmentManager().findFragmentById(R.id.mapB);
        fragment.getMapAsync(this);

        locationDefault = new LatLng(-16.411141,-71.540515);

        gps = new GPSclass(getActivity());

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        int zoom_ = 10;
        mMap = googleMap;
        verificarGPS();
        Configuration.miPosicion  = mMap.addMarker(new MarkerOptions().position(locationDefault).title("Tu estas aqui!").icon(BitmapDescriptorFactory.fromAsset("iconos/inicio.png")));

        Configuration.miPosicion.showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationDefault, zoom_));

        mMap.animateCamera(CameraUpdateFactory.zoomTo(zoom_ + 5), 2000, null);
    }

    public void verificarGPS()
    {
        Location lo = gps.getPosition();
        if(lo != null){
            locationDefault = new LatLng(lo.getLatitude(), lo.getLongitude());
        }
    }
}
