package com.testprojects.sanchellios.mockflight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by aleksandrvasilenko on 13.01.16.
 */
public class Airport {
    private String info;
    private String shortName;
    private LatLng coordinates;
    private BitmapDescriptor icon;

    public Airport(String info, String shortName, LatLng coordinates, int resource, Context context){
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resource);
        bitmap = Bitmap.createScaledBitmap(bitmap, 229, 131, false);
        this.info = info;
        this.shortName = shortName;
        this.coordinates = coordinates;
        this.icon = BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public String getInfo() {
        return info;
    }

    public String getShortName() {
        return shortName;
    }

    public LatLng getCoordinates() {
        return coordinates;
    }

    public BitmapDescriptor getIcon() {
        return icon;
    }
}
