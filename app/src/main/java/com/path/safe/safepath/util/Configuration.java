package com.path.safe.safepath.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Citec-PC on 09/12/17.
 */

public class Configuration {
    public  static Marker miPosicion;
    public static int DIMENSION_DESING_WIDTH= 640;
    public static int DIMENSION_DESING_HEIGHT= 1136;
    public static int WIDTH_PIXEL;
    public static int HEIGHT_PIXEL;
    Context context;

    public static int getHeight(int value){

        return HEIGHT_PIXEL*value/DIMENSION_DESING_HEIGHT;
    }

    public static int getWidth(int value){
        return WIDTH_PIXEL*value/DIMENSION_DESING_WIDTH;
    }

    public static void setWidthPixel(int value){
        WIDTH_PIXEL=value;
    }

    public static void setHeigthPixel(int value){
        HEIGHT_PIXEL=value;
    }

    public Bitmap escalarImagen(String path, int w, int h){
        AssetManager assetManager = context.getAssets();
        Bitmap bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        try {
            InputStream is = assetManager.open(path);
            bitmap = BitmapFactory.decodeStream(is);
            bitmap = Bitmap.createScaledBitmap(bitmap,w,h,true);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return  bitmap;
    }
}

