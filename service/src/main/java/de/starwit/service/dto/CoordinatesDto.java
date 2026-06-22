package de.starwit.service.dto;

public class CoordinatesDto {
    private double latitude;
    private double longitude;
    private double x;
    private double y;

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "CoordinatesDto [latitude=" + latitude + ", longitude=" + longitude + ", x=" + x + ", y=" + y + "]";
    }
}
