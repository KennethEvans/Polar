
package net.kenevans.polar.trainingsession;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exercise {

    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("stopTime")
    @Expose
    private String stopTime;
    @SerializedName("timezoneOffset")
    @Expose
    private Integer timezoneOffset;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("sport")
    @Expose
    private String sport;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("kiloCalories")
    @Expose
    private Integer kiloCalories;
    @SerializedName("heartRate")
    @Expose
    private HeartRate heartRate;
    @SerializedName("speed")
    @Expose
    private Speed speed;
    @SerializedName("zones")
    @Expose
    private Zones zones;
    @SerializedName("samples")
    @Expose
    private Samples samples;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Exercise() {
    }

    /**
     * 
     * @param startTime
     * @param distance
     * @param duration
     * @param speed
     * @param heartRate
     * @param kiloCalories
     * @param sport
     * @param longitude
     * @param timezoneOffset
     * @param latitude
     * @param zones
     * @param samples
     * @param stopTime
     */
    public Exercise(String startTime, String stopTime, Integer timezoneOffset, String duration, Double distance, String sport, Double latitude, Double longitude, Integer kiloCalories, HeartRate heartRate, Speed speed, Zones zones, Samples samples) {
        super();
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.timezoneOffset = timezoneOffset;
        this.duration = duration;
        this.distance = distance;
        this.sport = sport;
        this.latitude = latitude;
        this.longitude = longitude;
        this.kiloCalories = kiloCalories;
        this.heartRate = heartRate;
        this.speed = speed;
        this.zones = zones;
        this.samples = samples;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public Integer getTimezoneOffset() {
        return timezoneOffset;
    }

    public void setTimezoneOffset(Integer timezoneOffset) {
        this.timezoneOffset = timezoneOffset;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getKiloCalories() {
        return kiloCalories;
    }

    public void setKiloCalories(Integer kiloCalories) {
        this.kiloCalories = kiloCalories;
    }

    public HeartRate getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(HeartRate heartRate) {
        this.heartRate = heartRate;
    }

    public Speed getSpeed() {
        return speed;
    }

    public void setSpeed(Speed speed) {
        this.speed = speed;
    }

    public Zones getZones() {
        return zones;
    }

    public void setZones(Zones zones) {
        this.zones = zones;
    }

    public Samples getSamples() {
        return samples;
    }

    public void setSamples(Samples samples) {
        this.samples = samples;
    }

}
