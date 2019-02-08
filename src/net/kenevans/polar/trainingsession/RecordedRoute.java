
package net.kenevans.polar.trainingsession;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RecordedRoute {

    @SerializedName("dateTime")
    @Expose
    private String dateTime;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("altitude")
    @Expose
    private Double altitude;

    /**
     * No args constructor for use in serialization
     * 
     */
    public RecordedRoute() {
    }

    /**
     * 
     * @param dateTime
     * @param altitude
     * @param longitude
     * @param latitude
     */
    public RecordedRoute(String dateTime, Double longitude, Double latitude, Double altitude) {
        super();
        this.dateTime = dateTime;
        this.longitude = longitude;
        this.latitude = latitude;
        this.altitude = altitude;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

}
