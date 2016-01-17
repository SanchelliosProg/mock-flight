package com.testprojects.sanchellios.mockflight;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by aleksandrvasilenko on 17.01.16.
 */
public class AngleComp {
    public static float computeAngleBetween(LatLng from, LatLng to) {
        double fromLat = from.latitude;
        double fromLng = from.longitude;
        double toLat = to.latitude;
        double toLng = to.longitude;
        double dLat = fromLat - toLat;
        double dLng = fromLng - toLng;
        double o = dLng/dLat;
        double result = Math.sin(o);
        float export = (float)(Math.toDegrees(result)+180);
        return export;
    }
}
