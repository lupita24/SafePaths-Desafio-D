package com.path.safe.safepath;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.path.safe.safepath.util.Configuration;
import com.path.safe.safepath.util.Usuario;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MainActivityLogin extends AppCompatActivity {

    private EditText edt_email;
    private EditText edt_passw;
    private Button btn_singIn;
    private TextView text_registrar;
    public List<Usuario> list_usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        verificarSiYaIniciasteSesion();

        //REMOVE TITLE AND FULLSCREEN enable
        this.getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.InitComponents();
        this.ActionsComponents();
    }

    private void InitComponents(){
        //Inicializamos los datos del Layout
        edt_email = (EditText)findViewById(R.id.email);
        edt_passw = (EditText)findViewById(R.id.password);
        btn_singIn = (Button)findViewById(R.id.buttonsingin);
        text_registrar = (TextView) findViewById(R.id.textRegistrate);

        list_usuarios = new ArrayList<Usuario>();
    }

    private void ActionsComponents(){
        btn_singIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funSingIn();
            }
        });
        text_registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivityLogin.this, getString(R.string.registrateGratis), Toast.LENGTH_LONG).show();
                funRegister();
            }
        });
    }

    private void attemptLogin() {

        // Reset errors.
        edt_email.setError(null);
        edt_passw.setError(null);
        // Store values at the time of the login attempt.
        String email = edt_email.getText().toString();
        String password = edt_passw.getText().toString();
        boolean cancel = false;
        View focusView = null;
        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            edt_passw.setError(getString(R.string.error_field_required));
            focusView = edt_passw;
            cancel = true;
        }
        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            edt_email.setError(getString(R.string.error_field_required));
            focusView = edt_email;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // All is correct
            this.load_usuarios();
        }
    }

    //Funcion de Iniciar Sesion mediante la cuenta de Safe Path
    public  void funSingIn() {
        //
        this.attemptLogin();
    }

    //Pasamos a otro activity - A Registrarse
    public void funRegister(){
        try{
            Intent intent = new Intent(this,RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.my_fade_in, R.anim.my_fade_out);
            //finish(); //Sirve para cerrar definitivamente el activity actual al pasar a otro
        }catch (Exception e){
            //Error al pasar al actvity Registrarse
        }
    }

    //Verificamos si el usuario ya habia iniciado sesion anteriormente, si ese fuera el caso lo redireccionamos al activity MavigatorMapas
    public void verificarSiYaIniciasteSesion()
    {
        //Shared Preferences
        SharedPreferences miCuentaCrear = getSharedPreferences(Configuration.MY_PREFS_NAME,MainActivityLogin.this.MODE_PRIVATE);
        String restoredText = miCuentaCrear.getString("nombre", null);
        //Si se inicio session abre el nuevo activity
        if (restoredText!=null) {

            Ir_a_mapaGeneral();
        }
    }

    //Ir a MapaGeneral una vez iniciado sesion
    public  void Ir_a_mapaGeneral()
    {
        //Te manda al NavigationDrawerActivity
        Intent intent = new Intent(this,NavigationDrawerActivity.class);

        //PEDIR PERMISO GPS

        startActivity(intent);
        overridePendingTransition(R.anim.my_fade_in,R.anim.my_fade_out);
        finish();

    }

    //***************VERIFICAr SI EL USUARIO EXISTE***********************
    //Cargar las Zonas de la Base de Datos
    public  void load_usuarios()
    {
        try{
            list_usuarios.clear();
            AsyncHttpClient client = new AsyncHttpClient();

            client.get(Configuration.URL_BASE + Configuration.LINK_BD_REGISTRO+edt_email.getText(), null, getUsers());
        }catch (Exception e) {
            Toast.makeText(MainActivityLogin.this, "Error ...", Toast.LENGTH_LONG).show();
        }
    }

    public boolean verificarUsuario(){
        int tam= list_usuarios.size();
        String pass = Configuration.ID_SP+edt_passw.getText().toString();
        for(int i=0;i<tam;i++){
            //Toast.makeText(MainActivity.this, list_usuarios.get(i).getPass(), Toast.LENGTH_LONG).show();
            if(list_usuarios.get(i).getEmail().equals(edt_email.getText().toString()) && list_usuarios.get(i).getPass().equals(pass) ){
                return  true;
            }
        }
        return false;
    }

    private AsyncHttpResponseHandler getUsers() {
        return new AsyncHttpResponseHandler() {
            ProgressDialog pDialog;

            @Override
            public void onStart() {
                super.onStart();
                pDialog = new ProgressDialog(MainActivityLogin.this);
                pDialog.setMessage(getText(R.string.autenticate));
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] response,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                Toast.makeText(MainActivityLogin.this, getText(R.string.error_autenticate), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                String resultadoJson = new String(response);
                JsonParser parser = new JsonParser();
                JsonElement tradeElement = parser.parse(resultadoJson);
                JsonArray arrayUsers = tradeElement.getAsJsonArray();
                int numUser = arrayUsers.size();
                for(int i=0;i<numUser;i++){
                    JsonElement obj = arrayUsers.get(i);
                    JsonObject json = obj.getAsJsonObject();
                    //JsonElement ele = json.get("_id");
                    Usuario z = new Usuario();
                    z.setEmail(json.get("email").getAsString());
                    z.setName_(json.get("nombre").getAsString());
                    z.setLastname(json.get("apellido").getAsString());
                    z.setPass(json.get("clave").getAsString());
                    list_usuarios.add(z);
                    //Toast.makeText(MainActivity.this,json.get("nombre").getAsString() , Toast.LENGTH_LONG).show();

                }
                if(verificarUsuario()){
                    SharedPreferences sharedPreferences = getSharedPreferences(Configuration.MY_PREFS_NAME,MainActivityLogin.this.MODE_PRIVATE);
                    SharedPreferences.Editor miCuenta = sharedPreferences.edit();
                    miCuenta.putString("email",edt_email.getText().toString());
                    miCuenta.putString("nombre",list_usuarios.get(0).getName_());
                    miCuenta.putString("apellido",list_usuarios.get(0).getLastname());
                    miCuenta.putString("clave",list_usuarios.get(0).getPass());
                    miCuenta.commit();
                    Ir_a_mapaGeneral();
                }else {
                    Toast.makeText(MainActivityLogin.this,getText(R.string.please_register), Toast.LENGTH_LONG).show();
                }


            }
        };
    }
}
