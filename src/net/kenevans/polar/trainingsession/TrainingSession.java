
package net.kenevans.polar.trainingsession;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TrainingSession {

    @SerializedName("exportVersion")
    @Expose
    private String exportVersion;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("stopTime")
    @Expose
    private String stopTime;
    @SerializedName("timeZoneOffset")
    @Expose
    private Integer timeZoneOffset;
    @SerializedName("distance")
    @Expose
    private Double distance;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("maximumHeartRate")
    @Expose
    private Integer maximumHeartRate;
    @SerializedName("averageHeartRate")
    @Expose
    private Integer averageHeartRate;
    @SerializedName("kiloCalories")
    @Expose
    private Integer kiloCalories;
    @SerializedName("physicalInformationSnapshot")
    @Expose
    private PhysicalInformationSnapshot physicalInformationSnapshot;
    @SerializedName("exercise")
    @Expose
    private List<Exercise> exercises = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public TrainingSession() {
    }

    /**
     * 
     * @param timeZoneOffset
     * @param exportVersion
     * @param exercise
     * @param maximumHeartRate
     * @param physicalInformationSnapshot
     * @param stopTime
     * @param startTime
     * @param distance
     * @param duration
     * @param kiloCalories
     * @param name
     * @param longitude
     * @param latitude
     * @param averageHeartRate
     */
    public TrainingSession(String exportVersion, String name, Double latitude, Double longitude, String startTime, String stopTime, Integer timeZoneOffset, Double distance, String duration, Integer maximumHeartRate, Integer averageHeartRate, Integer kiloCalories, PhysicalInformationSnapshot physicalInformationSnapshot, List<Exercise> exercises) {
        super();
        this.exportVersion = exportVersion;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.startTime = startTime;
        this.stopTime = stopTime;
        this.timeZoneOffset = timeZoneOffset;
        this.distance = distance;
        this.duration = duration;
        this.maximumHeartRate = maximumHeartRate;
        this.averageHeartRate = averageHeartRate;
        this.kiloCalories = kiloCalories;
        this.physicalInformationSnapshot = physicalInformationSnapshot;
        this.exercises = exercises;
    }

    public String getExportVersion() {
        return exportVersion;
    }

    public void setExportVersion(String exportVersion) {
        this.exportVersion = exportVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Integer getTimeZoneOffset() {
        return timeZoneOffset;
    }

    public void setTimeZoneOffset(Integer timeZoneOffset) {
        this.timeZoneOffset = timeZoneOffset;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public Integer getMaximumHeartRate() {
        return maximumHeartRate;
    }

    public void setMaximumHeartRate(Integer maximumHeartRate) {
        this.maximumHeartRate = maximumHeartRate;
    }

    public Integer getAverageHeartRate() {
        return averageHeartRate;
    }

    public void setAverageHeartRate(Integer averageHeartRate) {
        this.averageHeartRate = averageHeartRate;
    }

    public Integer getKiloCalories() {
        return kiloCalories;
    }

    public void setKiloCalories(Integer kiloCalories) {
        this.kiloCalories = kiloCalories;
    }

    public PhysicalInformationSnapshot getPhysicalInformationSnapshot() {
        return physicalInformationSnapshot;
    }

    public void setPhysicalInformationSnapshot(PhysicalInformationSnapshot physicalInformationSnapshot) {
        this.physicalInformationSnapshot = physicalInformationSnapshot;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }

}
