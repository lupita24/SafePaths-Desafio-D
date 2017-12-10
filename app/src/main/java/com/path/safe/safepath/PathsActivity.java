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

        gps = new GPSclass(this);
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

        keyMap = "AIzaSyCEe6drWG6MYO2UNUjr9pil1exRHHEoCyY";

        googleMap = googleMap_;

        autoCompleteInicio = (AutoCompleteTextView) findViewById(R.id.autoCompleteInicio);
        autoCompleteMeta = (AutoCompleteTextView) findViewById(R.id.autoCompleteMeta);
        radioTierra = 6372.795477;// Aprox

        ruta = null;
        segPeligrosos = new ArrayList<>();
        marPeligrosos = new ArrayList<>();
        list_zonas = new ArrayList<>();

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                //cleanMap();
            }
            @Override
            public void onMarkerDrag(Marker marker) {
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {}
        });

        names = new ArrayList<>();
        place_ids = new ArrayList<>();

        posicion = 1;

        autoCompleteInicio.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                posicion = 1;
                if (s.length() > 1) getPlaces(s.toString());
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        //Toast.makeText(getApplicationContext(), "" + posicion, Toast.LENGTH_SHORT).show();
        autoCompleteMeta.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                posicion = 2;
                if (s.length() > 1) getPlaces(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        locationDefault = new LatLng(-16.411141,-71.540515);
        verificarGPS();

        markerInicio = googleMap.addMarker(new MarkerOptions().position(locationDefault)
                .title("Origen").icon(BitmapDescriptorFactory.fromAsset("iconos/inicio.png"))
                .draggable(true));
        markerInicio.showInfoWindow();

        markerMeta = googleMap.addMarker(new MarkerOptions().position(new LatLng(-16.4030657, -71.5258617))
                .title("Destino").icon(BitmapDescriptorFactory.fromAsset("iconos/fin.png"))
                .draggable(true));
        markerMeta.showInfoWindow();

        load_zones();

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationDefault, 10));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);
        //Localiza el pais y restrin
    }


    //Si activa el GPS deberia de haber un boton de actualizar para mostrar su ubicacion
    public void verificarGPS()
    {
        Location lo = gps.getPosition();
        if(lo != null){
            //Toast.makeText(getActivity(), lo.toString(), Toast.LENGTH_LONG).show();
            locationDefault = new LatLng(lo.getLatitude(), lo.getLongitude());}
    }
    //**************************Zona para manipular GET POST**************************+++
    private class Places extends AsyncTask<String, Long, String> {
        protected String doInBackground(String... urls) {
            try {
                return HttpRequest.get(urls[0]).accept("application/json")
                        .body();
            } catch (HttpRequest.HttpRequestException exception) {
                return null;
            }
        }
        protected void onPostExecute(String response) {
            try {
                place_ids.clear();
                names.clear();
                JSONObject obj = new JSONObject(response);
                final JSONArray data = obj.getJSONArray("predictions");
                for (int i = 0; i < data.length(); ++i) {
                    final JSONObject registro = data.getJSONObject(i);
                    names.add(registro.getString("description"));
                    place_ids.add(registro.getString("place_id"));
                }

                adapter = new ArrayAdapter<String>(getApplicationContext(),android.R.layout.test_list_item, names) {
                    @Override
                    public View getView(final int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                        text.setTextColor(Color.BLACK);
                        text.setTextSize(13);
                        text.setPadding(20, 15, 20, 15);
                        view.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v) {
                                TextView textElegido = (TextView) v.findViewById(android.R.id.text1);
                                if(posicion == 1) autoCompleteInicio.setText(textElegido.getText());
                                else autoCompleteMeta.setText(textElegido.getText());
                                getDetailsPlace(place_ids.get(position));
                                adapter.clear();
                                //Toast.makeText(getApplicationContext(), "" + posicion, Toast.LENGTH_SHORT).show();
                                autoCompleteInicio.clearFocus();
                                autoCompleteMeta.clearFocus();

                                if(posicion == 1) autoCompleteInicio.requestFocus();
                                else autoCompleteMeta.requestFocus();
                                closeKeyBoard();
                            }
                        });
                        return view;
                    }
                };
                //Toast.makeText(getApplicationContext(), "" + posicion, Toast.LENGTH_SHORT).show();
                if(posicion == 1) autoCompleteInicio.setAdapter(adapter);
                else autoCompleteMeta.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private class DetailsPlace extends AsyncTask<String, Long, String> {
        protected String doInBackground(String... urls) {
            try {
                return HttpRequest.get(urls[0]).accept("application/json")
                        .body();
            } catch (HttpRequest.HttpRequestException exception) {
                return null;
            }
        }
        protected void onPostExecute(String response) {
            try {
                JSONObject obj = new JSONObject(response);
                obj = obj.getJSONObject("result").getJSONObject("geometry").getJSONObject("location");
                CameraPosition cam = new CameraPosition(getLatLng(obj.getString("lat"),obj.getString("lng")),15,0,0);
                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cam));
                if(posicion == 1) markerInicio.setPosition(getLatLng(obj.getString("lat"),obj.getString("lng")));
                else markerMeta.setPosition(getLatLng(obj.getString("lat"),obj.getString("lng")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class GetPoint extends AsyncTask<String, Long, String> {
        protected String doInBackground(String... urls) {
            try {
                return HttpRequest.get(urls[0]).accept("application/json")
                        .body();
            } catch (HttpRequest.HttpRequestException exception) {
                return null;
            }
        }
        protected void onPostExecute(String response) {
            try {
                cleanMap();
                ArrayList< List<LatLng> > coords = new ArrayList<List<LatLng>>();
                JSONObject obj = new JSONObject(response);

                if(obj.getString("status").equals("ZERO_RESULTS")){
                    Toast.makeText(getApplicationContext(), "Ruta no disponible", Toast.LENGTH_SHORT).show();
                    return;
                }
                getCoordenadas(coords, obj.getJSONArray("routes"));

                int indice = getPosBestRoute(coords);
                Toast.makeText(getApplicationContext(), "nrouters: " + coords.size()+"\n elegida: "+indice, Toast.LENGTH_SHORT).show();

                PolylineOptions rectLine = new PolylineOptions().width(15).color(Color.rgb(67,153,238));
                for (int i = 0; i < coords.get(indice).size(); i++)
                    rectLine.add(coords.get(indice).get(i));
                ruta = googleMap.addPolyline(rectLine);


                for(int i = 1 ; i <coords.get(indice).size(); ++i){
                    double x1 = coords.get(indice).get(i-1).latitude, y1 = coords.get(indice).get(i - 1).longitude;
                    double x2 = coords.get(indice).get(i).latitude, y2 = coords.get(indice).get(i).longitude;
                    double m = (y2 - y1) / (x2 - x1);

                    for(int j = 0 ; j < list_zonas.size(); ++j )
                    {
                        double x3 = list_zonas.get(j).getLat(), y3 = list_zonas.get(j).getLng();
                        double y = (x3 - x1 +m*y3 + (y1/m)) / ((1/m) + m);
                        double x = ((y - y1) / m) + x1;
                        LatLng peligro = new LatLng(x3,y3);
                        double  distancia = getDistanciaMetros(new LatLng(x, y), peligro);

                        if(distancia < list_zonas.get(j).getRadio() && isArea(x1, y1, x2, y2, x, y)){
                            Toast.makeText(getApplicationContext(), "Distancia: "+distancia+"\nradio: "+list_zonas.get(j).getRadio(), Toast.LENGTH_SHORT).show();
                            double difx = distanciaMinima20 * Math.cos(Math.atan(m));
                            double dify = distanciaMinima20 * Math.sin(Math.atan(m));
                            PolylineOptions linea = new PolylineOptions().width(15).color(Color.RED);
                            if(!isArea(x1, y1, x2, y2, x - difx, y - dify) || !isArea(x1, y1, x2, y2, x + difx, y + dify)){
                                linea.add(new LatLng(x1, y1));
                                linea.add(new LatLng(x2, y2));
                            }
                            else{
                                linea.add(new LatLng(x - difx, y - dify));
                                linea.add(new LatLng(x + difx, y + dify));
                            }
                            segPeligrosos.add(googleMap.addPolyline(linea));
                        }

                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //**************************Funciones adionales***************************************

    private void getPlaces(String input) {
        input = toUrlEncode(input,"utf8");

        String consulta = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=" +
                keyMap + "&components=country:pe&input=" + input;
        new Places().execute(consulta);
    }

    private void getDetailsPlace(String placeId) {
        String consulta = "https://maps.googleapis.com/maps/api/place/details/json?placeid=" +
                placeId + "&key=" + keyMap;
        new DetailsPlace().execute(consulta);
    }

    //Codifica una cadena para una Url
    private String toUrlEncode(String input,String encode) {
        try {
            return URLEncoder.encode(input, encode);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
    //Cierra el teclado en MainActivity
    private void closeKeyBoard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    //Convierte String a LatLng
    private LatLng getLatLng(String lat, String lng){
        Double latD = Double.parseDouble(lat);
        Double lngD = Double.parseDouble(lng);
        return new LatLng(latD,lngD);
    }

    public void drawRouteW(View v) {
        String url = "http://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + markerInicio.getPosition().latitude + "," + markerInicio.getPosition().longitude
                + "&destination=" + markerMeta.getPosition().latitude + "," + markerMeta.getPosition().longitude
                + "&sensor=false&units=metric&mode=walking&alternatives=true";//driving walking

        new GetPoint().execute(url);

    }

    public void drawRouteA(View v) {
        String url = "http://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + markerInicio.getPosition().latitude + "," + markerInicio.getPosition().longitude
                + "&destination=" + markerMeta.getPosition().latitude + "," + markerMeta.getPosition().longitude
                + "&sensor=false&units=metric&mode=driving&alternatives=true";//driving walking

        new GetPoint().execute(url);

    }
    public void drawRouteB(View v) {
        String url = "http://maps.googleapis.com/maps/api/directions/json?"
                + "origin=" + markerInicio.getPosition().latitude + "," + markerInicio.getPosition().longitude
                + "&destination=" + markerMeta.getPosition().latitude + "," + markerMeta.getPosition().longitude
                + "&sensor=false&units=metric&mode=bicycling&alternatives=true";
        Log.e("Mensaje", url);

        new GetPoint().execute(url);

    }

    private double getDistanciaMetros(LatLng A, LatLng B){
        double latA =  A.latitude * Math.PI / 180;
        double lonA =  A.longitude * Math.PI / 180;
        double latB =  B.latitude * Math.PI / 180;
        double lonB =  B.longitude * Math.PI / 180;
        double distancia = radioTierra * Math.acos(Math.sin(latA) * Math.sin(latB)
                + Math.cos(latA) * Math.cos(latB) * Math.cos(lonA - lonB)) *  1000;

        return Math.floor(distancia * 1000) / 1000;
    }

    private boolean isArea(double x1, double y1, double x2, double y2,double x, double y){
        double tmp = 0;
        if(x1 > x2){
            tmp = x1;x1 = x2;x2 = tmp;}
        if(y1 > y2){
            tmp = y1;y1 = y2;y2 = tmp;}

        if(x >= x1 && x <= x2 && y >= y1 && y <= y2) return true;
        return false;
    }

    /******************FUNCIONES ADICIONALES**************************/

    private void cleanMap(){
        if(ruta != null) ruta.remove();
        for(int i = 0 ; i < segPeligrosos.size(); ++i)
            segPeligrosos.get(i).remove();
        segPeligrosos.clear();

        for(int i = 0 ; i < marPeligrosos.size(); ++i)
            marPeligrosos.get(i).remove();
        marPeligrosos.clear();
    }

    private void getCoordenadas(ArrayList< List<LatLng> > coords, JSONArray routes)
    {
        try {
            for (int i= 0; i < routes.length();++i)
            {
                List<LatLng> coord = decodePoly(routes.getJSONObject(i).getJSONObject("overview_polyline").getString("points"));
                Log.d("Tamaño",String.valueOf(coord.size()));
                coords.add(coord);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private int getPosBestRoute(ArrayList< List<LatLng> > coords)
    {
        int pesoglobal = 10000000;
        int indice = 0;
        for(int i = 0; i < coords.size(); ++i)
        {
            int pesolocal = 0;
            for(int j = 1 ; j < coords.get(i).size(); ++j)
            {
                double x1 = coords.get(i).get(j-1).latitude, y1 = coords.get(i).get(j - 1).longitude;
                double x2 = coords.get(i).get(j).latitude, y2 = coords.get(i).get(j).longitude;
                double m = (y2 - y1) / (x2 - x1);

                for(int k = 0 ; k < list_zonas.size(); ++k )
                {
                    double x3 = list_zonas.get(k).getLat(), y3 = list_zonas.get(k).getLng();
                    double y = (x3 - x1 +m*y3 + (y1/m)) / ((1/m) + m);
                    double x = ((y - y1) / m) + x1;
                    LatLng peligro = new LatLng(x3, y3);
                    double  distancia = getDistanciaMetros(new LatLng(x, y), peligro);

                    if(distancia < list_zonas.get(k).getRadio() && isArea(x1, y1, x2, y2, x, y)){
                        pesolocal += list_zonas.get(k).getNivel();
                    }

                }
            }

            if(pesolocal < pesoglobal){
                pesoglobal = pesolocal;
                indice = i;
            }
        }

        return indice;

    }

    /*********CONSULTA ZONAS******/

    private class getZonas extends AsyncTask<String, Long, String> {
        protected String doInBackground(String... urls) {
            try {
                return HttpRequest.get(urls[0]).accept("application/json")
                        .body();
            } catch (HttpRequest.HttpRequestException exception) {
                return null;
            }
        }
        protected void onPostExecute(String response) {

            String resultadoJson = new String(response);
            //Log.e("Data", resultadoJson);
            try {
                JsonParser parser = new JsonParser();
                JsonElement tradeElement = parser.parse(resultadoJson);
                JsonArray arrayZonas = tradeElement.getAsJsonArray();
                int numZonas = arrayZonas.size();
                for (int i = 0; i < numZonas; i++) {
                    JsonElement obj = arrayZonas.get(i);
                    JsonObject json = obj.getAsJsonObject();
                    //JsonElement ele = json.get("_id");
                    Zona z = new Zona();
                    //z.set_id(json.get("_id").getAsString());
                    z.setIdFacebook(json.get("idFacebook").getAsString());
                    z.setIdGooglePlus(json.get("idGooglePlus").getAsString());
                    z.setIdExtra(json.get("idExtra").getAsString());
                    z.setLat(json.get("lat").getAsDouble());
                    z.setLng(json.get("lng").getAsDouble());
                    z.setRadio(json.get("radio").getAsInt());
                    if (json.get("descripcion") != null) {
                        z.setDescripcion(json.get("descripcion").getAsString());
                    }
                    else{z.setDescripcion("");}
                    z.setNivel(json.get("nivel").getAsInt());
                    list_zonas.add(z);

                }
                drawZonas();
                Toast.makeText(getApplicationContext(), "Zonas Cargadas...!", Toast.LENGTH_LONG).show();
            }catch(Exception e) {
                //
            }
        }
    }

    /******************ZONAS**************************/

    //Cargar las Zonas de la Base de Datos
    public  void load_zones()
    {
        String url = Configuration.URL_BASE + Configuration.LINK_BD_ZONA;
        new getZonas().execute(url);
    }

    //Dibujar las zonas en el mapa
    public void drawZonas(){
        try {
            for(int i=0;i<list_zonas.size();i++){
                int nivel = list_zonas.get(i).getNivel()-1;
                if(nivel>=5){nivel=4;}
                final LatLng posicionZona= new LatLng(list_zonas.get(i).getLat(), list_zonas.get(i).getLng());
                //Añadimos las zonas
                googleMap.addCircle(new CircleOptions()
                        .center(posicionZona)
                        .radius(list_zonas.get(i).getRadio())
                        .strokeColor(Color.parseColor(Configuration.list_colores.get(nivel)))
                        .fillColor(Color.parseColor(Configuration.list_colores_center.get(nivel))));
                //Log.d("Lat:",String.valueOf(list_zonas.get(i).getLat()));
                //Añadir los marcadores para las zonas
                googleMap.addMarker(new MarkerOptions()
                        .position(posicionZona)
                        .title(list_zonas.get(i).getDescripcion())
                        .icon(BitmapDescriptorFactory.fromResource(icon_nivel_zona(nivel))));
            }
        }
        catch (Exception e) {
            //e.printStackTrace();
        }
    }

    //Icono Adecuado segun el nivel de zona
    public int icon_nivel_zona(int nivel)
    {
        int icon_nivel = 0;
        if(nivel==0 || nivel==1){
            icon_nivel = R.drawable.icon_niv_bajo;
        }else{
            if(nivel==3 || nivel==2){
                icon_nivel = R.drawable.icon_niv_medio;
            }else{
                icon_nivel = R.drawable.icon_niv_alto;
            }
        }
        return  icon_nivel;
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }

}
