package com.path.safe.safepath;

import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.kevinsawicki.http.HttpRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.path.safe.safepath.util.Configuration;
import com.path.safe.safepath.util.GPSclass;
import com.path.safe.safepath.util.Zona;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class PathsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    public List<Zona> list_zonas;
    private LatLng locationDefault;
    private GPSclass gps;

    private Marker markerInicio;
    private Marker markerMeta;


    private AutoCompleteTextView autoCompleteInicio;
    private AutoCompleteTextView autoCompleteMeta;
    private List<String> names;
    private ArrayAdapter<String> adapter;
    private List<String> place_ids;

    private String keyMap;

    private static int posicion;

    private Polyline ruta;
    private List<Polyline> segPeligrosos;
    private List<Marker> marPeligrosos;
    private double radioTierra;

    private final double distanciaMinima10 = 0.0000920368;
    private final double distanciaMinima20 = 4*distanciaMinima10;
    private final double distanciaMinima = 30;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paths);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mi_mapa);
        mapFragment.getMapAsync(this);
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
    public void onMapReady(GoogleMap googleMap_) {
        googleMap = googleMap_;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        autoCompleteInicio = (AutoCompleteTextView) findViewById(R.id.autoCompleteInicio);
        autoCompleteMeta = (AutoCompleteTextView) findViewById(R.id.autoCompleteMeta);
        radioTierra = 6372.795477;// Aprox

        ruta = null;
        segPeligrosos = new ArrayList<>();
        marPeligrosos = new ArrayList<>();
        list_zonas = new ArrayList<>();

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

}
