package com.testprojects.sanchellios.mockflight;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by aleksandrvasilenko on 13.01.16.
 */
public class Route {

    private final int UNDETAILED_ROUTE_POINTS = 32;
    private final int DETAILED_ROUTE_POINTS = 32*10;
    private final boolean UNDETAILED_ROUTE = true;
    private final boolean DETAILED_ROUTE = false;

    private Airport startPoint;
    private Airport endPoint;
    private LatLng middlePoint;
    private CameraPosition position;
    private MarkerOptions startPointMarker;
    private MarkerOptions endPointMarker;
    private Bitmap dot;
    private ArrayList<LatLng> undetailedRoute;
    private ArrayList<LatLng> detailedRoute;
    private ArrayList<MarkerOptions> routeMarkers;
    private boolean isLatMove;

    public Route(Airport startPoint, Airport endPoint, Context context){
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.middlePoint = setMiddlePoint();
        this.position = setCameraPosition();
        this.isLatMove = moveTypeDetection();
        this.startPointMarker = createMarker(startPoint, endPoint);
        this.endPointMarker = createMarker(endPoint, startPoint);
        dot = BitmapFactory.decodeResource(context.getResources(), R.drawable.dot);
        dot = Bitmap.createScaledBitmap(dot, 30, 30, false);
        undetailedRoute = new ArrayList<>();
        routeMarkers = new ArrayList<>();
    }

    public void detectLineRoute(){
        if(isLatMove){
            getRouteLatMove(true);
        }else{

        }
        addRouteMarkers(UNDETAILED_ROUTE);
    }

    private void addRouteMarkers(boolean isUndetailed){
        if(isUndetailed){
            for(int i = 0; i < getUndetailedRoute().size(); i++){
                routeMarkers.add(createMarker(getUndetailedRoute().get(i)));
            }
        }else {
            for(int i = 0; i < getDetailedRoute().size(); i++){
                routeMarkers.add(createMarker(getDetailedRoute().get(i)));
            }
        }
    }

    /** x = lat, y = lng*/
    private ArrayList<LatLng> formSinusoidalRoute(ArrayList<LatLng> linRoute, int num){
        ArrayList<LatLng> route = new ArrayList<>();

        double cLat;
        double cLng;

        double angleStep = Math.toRadians(90/(num/4));
        double currentAngle = Math.toRadians(180);

        for (int i = 0; i < linRoute.size(); i++){
            double sinus = Math.sin(currentAngle);
            cLat = linRoute.get(i).latitude;
            cLng = linRoute.get(i).longitude;
            cLng = cLng + (cLng*sinus)/8;
            route.add(new LatLng(cLat, cLng));
            currentAngle += angleStep;

        }
        return route;
    }
    /** x = lng, y = lat*/
    private void getRoutePointsSinusLngMove(){

    }

    private void getRouteLatMove(boolean isSinusoidal){
        CoordParameters cp = new CoordParameters();

        if(isSinusoidal){
            undetailedRoute = formLinearRoute(cp, UNDETAILED_ROUTE_POINTS);
            detailedRoute = formLinearRoute(cp, DETAILED_ROUTE_POINTS);
            undetailedRoute = formSinusoidalRoute(undetailedRoute, UNDETAILED_ROUTE_POINTS);
            detailedRoute = formSinusoidalRoute(getDetailedRoute(), DETAILED_ROUTE_POINTS);

        }else {
            undetailedRoute = formLinearRoute(cp, UNDETAILED_ROUTE_POINTS);
        }
    }

    private ArrayList<LatLng> formLinearRoute(CoordParameters cp, int num){

        ArrayList<LatLng> route = new ArrayList<>();

        double cLat = cp.startLat;
        double cLng = cp.startLng;
        double addLat = cp.getLatStep(num);
        double addLng = cp.getLngStep(num);

        for (int i = 0; i < num; i++){
            route.add(new LatLng(cLat, cLng));
            cLat += addLat;
            cLng += addLng;
        }
        return route;
    }

    /** If it returns true, than we are moving along latitude*/
    private final boolean moveTypeDetection(){
        double latDiff = Math.abs(startPoint.getCoordinates().latitude - endPoint.getCoordinates().longitude);
        double lngDiff = Math.abs(startPoint.getCoordinates().longitude - endPoint.getCoordinates().longitude);
        if(latDiff >= lngDiff){
            return true;
        }else {
            return false;
        }
    }
    /**Creates marker for enpoints*/
    private final MarkerOptions createMarker(Airport airport,
                                             Airport secondAirport){
        float anchorX;
        float anchorY;

        if(airport.getCoordinates().latitude >= secondAirport.getCoordinates().latitude){
            anchorX = 0.8F;
            anchorY = 0.99F;
        }else {
            anchorX = 1;
            anchorY = 0.4F;
        }

        if(airport.getCoordinates().longitude >= secondAirport.getCoordinates().longitude){

        }else {
           anchorX = 0.2F;
        }


        return new MarkerOptions()
                .position(airport.getCoordinates())
                .title(airport.getInfo())
                .icon(airport.getIcon())
                .anchor(anchorX, anchorY);
    }
    /**Creates marker for route point*/
    private MarkerOptions createMarker(LatLng coordinates){
        return new MarkerOptions()
                .position(coordinates)
                .icon(BitmapDescriptorFactory.fromBitmap(dot));
    }


    private final CameraPosition setCameraPosition(){
        return CameraPosition.builder()
                .target(middlePoint)
                .zoom(4)
                .build();
    }
    /**This method creates the point which will be the center of the screen*/
    private final LatLng setMiddlePoint(){
        double latStart = getStartPoint().getCoordinates().latitude;
        double lngStart = getStartPoint().getCoordinates().longitude;
        double latEnd = getEndPoint().getCoordinates().latitude;
        double lngEnd = getEndPoint().getCoordinates().longitude;
        double latMid;
        double lngMid;

        if(latStart >= latEnd){
            latMid = getMid(latStart, latEnd);
        }else {
            latMid = getMid(latEnd, latStart);
        }

        if(lngStart >= lngEnd){
            lngMid = getMid(lngStart, lngEnd);
        }else {
            lngMid = getMid(lngEnd, latStart);
        }
        return new LatLng(latMid, lngMid);
    }

    private final double getMid(double a, double b){
        return (a - b)/2 + b;
    }

    public Airport getStartPoint() {
        return startPoint;
    }

    public Airport getEndPoint() {
        return endPoint;
    }

    /**Made to center the map on it*/
    public LatLng getMiddlePoint() {
        return middlePoint;
    }

    public CameraPosition getPosition() {
        return position;
    }

    public MarkerOptions getStartPointMarker() {
        return startPointMarker;
    }

    public MarkerOptions getEndPointMarker() {
        return endPointMarker;
    }

    public ArrayList<MarkerOptions> getRouteMarkers() {
        return routeMarkers;
    }

    public ArrayList<LatLng> getUndetailedRoute() {
        return undetailedRoute;
    }

    public ArrayList<LatLng> getDetailedRoute() {
        return detailedRoute;
    }

    private class CoordParameters{
        final double startLat = startPoint.getCoordinates().latitude;
        final double endLat = endPoint.getCoordinates().latitude;
        final double startLng = startPoint.getCoordinates().longitude;
        final double endLng = endPoint.getCoordinates().longitude;

        public double getLatStep(int num){
            return (endLat - startLat)/num;
        }
        public double getLngStep(int num){
            return (endLng - startLng)/num;
        }
    }
}
