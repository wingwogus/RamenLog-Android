package kkj.mjc.ramenlog.dto;

public class Restaurant {
    private String name;
    private String address;

    private double longitude;
    private double latitude;
    private double avgRating;
    private double distance;


    public String getName() { return name; }
    public String getAddress() { return address; }

    public double getLatitude() { return latitude; }

    public double getLongitude() { return longitude; }
    public double getAvgRating() { return avgRating; }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
