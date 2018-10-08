package com.hotelnow.utils;

import com.thebrownarrow.model.MyLocation;

import java.util.ArrayList;

public class GlobalUtils {

    private static ArrayList<MyLocation> latLngsArrayList = new ArrayList<>();

    public static ArrayList<MyLocation> getLatLongArray() {

        addLocations(34.0508937, -118.2483757, true);
        addLocations(34.101059, -118.3163313, false);
        addLocations(34.0984372, -118.30793849999998, false);
        addLocations(34.1598974, -118.31221519999997, false);
        addLocations(34.1012814, -118.31296900000001, false);
        addLocations(34.1490401, -118.2840485, false);
        addLocations(34.029298, -118.38770850000003, false);
        addLocations(34.1582665, -118.2518697, false);
        addLocations(33.9451969, -118.1824534, false);
        addLocations(34.15412490000001, -118.3081143, false);
        addLocations(34.02128249999999, -118.383893, false);


        return latLngsArrayList;
    }

    private static void addLocations(double latitude, double longitude, boolean isfocus) {
        MyLocation location = new MyLocation(latitude, longitude, isfocus);
        latLngsArrayList.add(location);
    }
}
