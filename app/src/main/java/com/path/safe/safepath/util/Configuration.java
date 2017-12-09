package com.path.safe.safepath.util;

/**
 * Created by Citec-PC on 09/12/17.
 */

public class Configuration {
    public static int DIMENSION_DESING_WIDTH= 640;
    public static int DIMENSION_DESING_HEIGHT= 1136;
    public static int WIDTH_PIXEL;
    public static int HEIGHT_PIXEL;

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
}

