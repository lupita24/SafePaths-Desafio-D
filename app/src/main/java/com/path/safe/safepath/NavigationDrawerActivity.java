package com.path.safe.safepath;

import android.app.FragmentManager;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.path.safe.safepath.util.Configuration;

public class NavigationDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private LayoutInflater inflater;
    private RelativeLayout contenedor;

    private View header;
    private TextView nickname;
    private TextView email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation_drawer);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fabAddZona = (FloatingActionButton) findViewById(R.id.fab_addzona);
        fabAddZona.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NavigationDrawerActivity.this, AddZona.class );
                startActivity(i);
            }
        });
        FloatingActionButton fabRuta = (FloatingActionButton) findViewById(R.id.fab_ruta);
        fabRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NavigationDrawerActivity.this, PathsActivity.class );
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        contenedor = (RelativeLayout)findViewById(R.id.contend_layout);
        inflater = LayoutInflater.from(this);

        header = navigationView.getHeaderView(0);
        nickname = (TextView) header.findViewById(R.id.nicknameView);
        email = (TextView) header.findViewById(R.id.text_email);

        setMyProfile();
        funMiMapa();
    }

    //Seteamos elnickname e imgProfile por uno por defecto, lo que nosotros queramos
    public void myDefaultProlife()
    {
        SharedPreferences miCuenta = getSharedPreferences(Configuration.MY_PREFS_NAME,this.MODE_PRIVATE);
        String name = miCuenta.getString("nombre", "no found");
        String email_ = "Bienvenido a SafePaths!!";//miCuenta.getString("clave","no found");
        nickname.setText(name);
        email.setText(email_);
    }

    public void setMyProfile()
    {
        try{
            myDefaultProlife();
        }catch (Exception e)
        {
            //error al cargar miprofile
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_account) {
            // Handle the camera action
        } else if (id == R.id.nav_paths) {

        } else if (id == R.id.nav_qualification) {

        } else if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_help) {

        }else if (id == R.id.nav_info) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void funMiMapa()
    {
        try {
            this.setTitle("Mi Mapa");
            //Infamos otra vez el content_navigator_mapas que contiene un fragment
            contenedor.removeAllViews();
            inflater.inflate(R.layout.content_navigation_drawer, contenedor, true);
            //Reemplazamos el Fragmente por el de MapaGeneral
            FragmentManager fm = getFragmentManager();
            fm.beginTransaction().replace(R.id.contend_frame, new GeneralMapActivity()).commit();

        }catch (Exception e){
            //Error al pasar el activity MapaGeneral
        }
    }
}
