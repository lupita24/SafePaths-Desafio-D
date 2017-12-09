package com.path.safe.safepath;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

public class MainActivityLogin extends AppCompatActivity {

    private EditText edt_email;
    private EditText edt_passw;
    private Button btn_singIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_login);

        //REMOVE TITLE AND FULLSCREEN enable
        this.getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        this.InitComponents();
    }

    private void InitComponents(){
        //Inicializamos los datos del Layout
        edt_email = (EditText)findViewById(R.id.email);
        edt_passw = (EditText)findViewById(R.id.password);
        btn_singIn = (Button)findViewById(R.id.buttonsingin);
    }
}
