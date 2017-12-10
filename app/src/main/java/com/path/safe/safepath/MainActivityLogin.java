package com.path.safe.safepath;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
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
        this.ActionsComponents();
    }

    private void InitComponents(){
        //Inicializamos los datos del Layout
        edt_email = (EditText)findViewById(R.id.email);
        edt_passw = (EditText)findViewById(R.id.password);
        btn_singIn = (Button)findViewById(R.id.buttonsingin);
    }

    private void ActionsComponents(){
        btn_singIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funSingIn();
            }
        });
    }

    //Funcion de Iniciar Sesion mediante la cuenta de Safe Path
    public  void funSingIn() {
        //
        this.Ir_a_mapaGeneral();
    }

    //Ir a MapaGeneral una vez iniciado sesion
    public  void Ir_a_mapaGeneral()
    {
        //Te manda al NavigationDrawerActivity
        Intent intent = new Intent(this,NavigationDrawerActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.my_fade_in,R.anim.my_fade_out);
        finish();

    }
}
