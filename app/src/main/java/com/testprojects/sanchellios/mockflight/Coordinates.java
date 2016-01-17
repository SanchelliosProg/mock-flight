package com.testprojects.sanchellios.mockflight;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by aleksandrvasilenko on 13.01.16.
 */
public class Coordinates {
    private Context context;
    /**Lappeenranta airport coordinates*/
    final static LatLng LPP = new LatLng(61.044039, 28.156276);
    /**Sofia airport coordinates*/
    final static LatLng SOF = new LatLng(42.689624, 23.402102);

    public Coordinates(Context context){
        this.context = context;
    }

    public Airport getLPP(){
        return new Airport("Lappeenranta airport, Finland", "lpp", LPP, R.drawable.lpp, context);
    }
    public Airport getSOF(){
        return new Airport("Sofia airport, Bulgaria", "sof", SOF, R.drawable.sof, context);
    }
}
