package com.path.safe.safepath;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.path.safe.safepath.util.Configuration;

public class Splash extends AppCompatActivity {

    RelativeLayout mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //REMOVE TITLE AND FULLSCREEN enable
        this.getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        mainView = (RelativeLayout) findViewById(R.id.layoutSplash);

        this.loadBackground();

        this.IniAnimations();
    }


    private void loadBackground(){
        mainView.setBackgroundResource(R.drawable.background_splash);
    }


    public void IniAnimations()
    {
        //Inicializamos la imagen loading.png -- la imagen esta en drawable
        final ImageView imgSplash = (ImageView) findViewById(R.id.imageSplash);

        //Inicializamos los animations
        final Animation animLoad = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        final Animation animFade = AnimationUtils.loadAnimation(getBaseContext(), R.anim.my_fade_out);
        //animamos la imagen loading.png
        imgSplash.startAnimation(animLoad);
        //Cuando termine la animacion nos vamos al MainActivity.class
        animLoad.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //Hacemos que la imagen loading.png se desvanesca
                imgSplash.startAnimation(animFade);
                finish();
                Intent intent = new Intent(getBaseContext(), MainActivityLogin.class);
                startActivity(intent);
                //Hacemos que el Activity se desvanesza
                overridePendingTransition(R.anim.my_fade_in, R.anim.my_fade_out);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }
}
