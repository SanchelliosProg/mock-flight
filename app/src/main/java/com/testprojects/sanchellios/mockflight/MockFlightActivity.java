package com.testprojects.sanchellios.mockflight;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import java.util.ArrayList;



public class MockFlightActivity extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap gMap;
    boolean mapReady;
    private Route lppToSof;
    private Coordinates coordinates;
    private int currentCoordIndex = 0;
    private ArrayList<LatLng> theRoute;
    MarkerOptions airplaneMO;
    private float airplaneBearing;
    private Bitmap airplaneIcon;
    final Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mock_flight);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MockFlightActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fab.hide();

        airplaneIcon = BitmapFactory.decodeResource(getApplicationContext().getResources(),
                R.drawable.airplane);
        airplaneIcon = Bitmap.createScaledBitmap(airplaneIcon, 100, 100, false);

        coordinates = new Coordinates(getApplicationContext());
        lppToSof = new Route(coordinates.getLPP(), coordinates.getSOF(), getApplicationContext());
        lppToSof.detectLineRoute();
        theRoute = new ArrayList<>();
        theRoute = lppToSof.getUndetailedRoute();
        LatLng start = lppToSof.getStartPoint().getCoordinates();
        airplaneMO = new MarkerOptions()
                .position(start)
                .title("Airplane")
                .icon(BitmapDescriptorFactory.fromBitmap(airplaneIcon))
                .rotation(120)
                .anchor(0.49f,0.2f)
                .flat(true);

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;

        gMap = googleMap;
        Marker theAirplane = gMap.addMarker(airplaneMO);
        gMap.addMarker(lppToSof.getStartPointMarker());

        gMap.addMarker(lppToSof.getEndPointMarker());
        ArrayList<MarkerOptions> routeMarkers = lppToSof.getRouteMarkers();


        for(int i =0; i < routeMarkers.size(); i++){
            gMap.addMarker(routeMarkers.get(i));
        }

        airplaneBearing = AngleComp.computeAngleBetween(theRoute.get(0), theRoute.get(1));
        theAirplane.setRotation(airplaneBearing);
        Log.i("rotation", String.valueOf(theAirplane.getRotation()));
        movePlane(theAirplane, 0, 1);
        centerTheMap(lppToSof.getPosition());
    }

    private void movePlane(Marker plane, int pIndex, int index){
        if(currentCoordIndex < theRoute.size()){
            LatLng sll = theRoute.get(pIndex);
            LatLng tp = theRoute.get(index);
            airplaneBearing = AngleComp.computeAngleBetween(sll, tp);
            plane.setRotation(airplaneBearing);
            animateMarker(plane, theRoute.get(index), false, index);
            Log.i("rotation", String.valueOf(plane.getRotation()));
        }
    }

    private void animateMarker(final Marker plane,
                               final LatLng toPosition,
                               final boolean hideMarker,
                               final int currentIndex) {

        final long start = SystemClock.uptimeMillis();
        Projection proj = gMap.getProjection();
        Point startPoint = proj.toScreenLocation(plane.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                double lat = t * toPosition.latitude + (1 - t) * startLatLng.latitude;
                double lng = t * toPosition.longitude + (1 - t) * startLatLng.longitude;
                airplaneBearing = AngleComp.computeAngleBetween(startLatLng, toPosition);
                plane.setPosition(new LatLng(lat, lng));
                plane.setRotation(airplaneBearing);


                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        plane.setVisible(false);
                        int nextIndex = currentIndex + 1;
                        if (nextIndex < theRoute.size()) {
                            movePlane(plane, currentIndex, nextIndex);
                        }
                    } else {
                        plane.setVisible(true);
                        int nextIndex = currentIndex + 1;
                        if (nextIndex < theRoute.size()) {
                            movePlane(plane, currentIndex, nextIndex);
                        }
                    }
                }
            }
        });
    }

    private void centerTheMap(CameraPosition target){
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
    }


}
