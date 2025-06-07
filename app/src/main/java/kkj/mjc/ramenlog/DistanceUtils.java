package kkj.mjc.ramenlog;

import android.location.Location;

public class DistanceUtils {
    public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        Location loc1 = new Location("start");
        loc1.setLatitude(lat1);
        loc1.setLongitude(lon1);
        Location loc2 = new Location("end");
        loc2.setLatitude(lat2);
        loc2.setLongitude(lon2);
        return loc1.distanceTo(loc2); // meter
    }
}
