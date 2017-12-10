package com.path.safe.safepath;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.preference.PreferenceActivity;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.loopj.android.http.RequestParams;
import com.path.safe.safepath.util.Configuration;
import com.path.safe.safepath.util.Usuario;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class RegisterActivity extends AppCompatActivity {

    // UI references.
    private EditText mEmailView;
    private EditText nameView;
    private EditText surNameView;
    private EditText mPasswordView;

    public List<Usuario> list_usuarios;
    private boolean emailExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //REMOVE TITLE AND FULLSCREEN enable
        this.getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        list_usuarios = new ArrayList<Usuario>();

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        nameView  = (EditText) findViewById(R.id.name);
        surNameView  = (EditText) findViewById(R.id.surname);

        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);
        nameView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        String name = nameView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        // Check for a valid name.
        if (TextUtils.isEmpty(name)) {
            nameView.setError(getString(R.string.error_field_required));
            focusView = nameView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // All is correct
            //Toast.makeText(RegisterActivity.this, "ENTRO..........", Toast.LENGTH_SHORT).show();
            this.funRegistrarse();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    //Funcion Registrarse
    public void funRegistrarse(){
        this.load_usuarios();
    }

    //VERIFICAR USUARIO
    //Cargar las Zonas de la Base de Datos
    public  void load_usuarios()
    {
        try{
            list_usuarios.clear();
            AsyncHttpClient client = new AsyncHttpClient();

            client.get(Configuration.URL_BASE + Configuration.LINK_BD_REGISTRO + mEmailView.getText().toString(), null, getUsers());
        }catch (Exception e) {
            Toast.makeText(this, "Error ...", Toast.LENGTH_LONG).show();
        }
    }

    public boolean verificarUsuario(){
        int tam= list_usuarios.size();
        for(int i=0;i<tam;i++){
            //Toast.makeText(MainActivity.this, list_usuarios.get(i).getPass(), Toast.LENGTH_LONG).show();
            if(list_usuarios.get(i).getEmail().equals(mEmailView.getText().toString()) ){
                return  true;
            }
        }
        return false;
    }

    //VALIDAR E-MAIL
    private AsyncHttpResponseHandler getUsers() {
        return new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                //verificando usuario
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] response,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                Toast.makeText(RegisterActivity.this, getText(R.string.error_register), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // TODO Auto-generated method stub
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
                    //z.setPass(json.get("clave").getAsString());
                    list_usuarios.add(z);
                    //Toast.makeText(MainActivity.this,json.get("nombre").getAsString() , Toast.LENGTH_LONG).show();

                }
                emailExist = verificarUsuario();
                if(emailExist){
                    Toast.makeText(RegisterActivity.this,getText(R.string.email_already_exist).toString(), Toast.LENGTH_LONG).show();
                }else{//Registrar usuario
                    Toast.makeText(RegisterActivity.this, getText(R.string.data_correct).toString(), Toast.LENGTH_SHORT).show();
                    RequestParams params = new RequestParams();
                    try {
                        params.put("nombre", nameView.getText().toString());
                        params.put("apellido", surNameView.getText().toString());
                        params.put("email", mEmailView.getText().toString());
                        params.put("clave", Configuration.ID_SP + mPasswordView.getText().toString());

                    } catch (Exception e) {
                        //Error al colocar los parametros
                    }
                    AsyncHttpClient client = new AsyncHttpClient();
                    client.post(Configuration.URL_BASE + Configuration.LINK_BD_USUARIO, params, registro());
                }
            }
        };
    }

    //PROCESO DE REGISTRO
    private AsyncHttpResponseHandler registro() {
        return new AsyncHttpResponseHandler() {
            ProgressDialog pDialog;

            @Override
            public void onStart() {
                super.onStart();
                pDialog = new ProgressDialog(RegisterActivity.this);
                pDialog.setMessage(getText(R.string.load_register));
                pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pDialog.show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] response,
                                  Throwable arg3) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                Toast.makeText(RegisterActivity.this, getText(R.string.error_register).toString(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                // TODO Auto-generated method stub
                pDialog.dismiss();
                Toast.makeText(RegisterActivity.this, getText(R.string.succes_register), Toast.LENGTH_LONG).show();
                //Una ves registrado nos vamos alactivity principal
                Intent intent = new Intent(RegisterActivity.this,MainActivityLogin.class);
                startActivity(intent);
                overridePendingTransition(R.anim.my_fade_in, R.anim.my_fade_out);
                finish();
            }
        };
    }
}

