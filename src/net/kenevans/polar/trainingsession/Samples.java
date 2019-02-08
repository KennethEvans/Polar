
package net.kenevans.polar.trainingsession;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Samples {

    @SerializedName("heartRate")
    @Expose
    private List<HeartRate__> heartRate = null;
    @SerializedName("speed")
    @Expose
    private List<Speed_> speed = null;
    @SerializedName("distance")
    @Expose
    private List<Distance> distance = null;
    @SerializedName("recordedRoute")
    @Expose
    private List<RecordedRoute> recordedRoute = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Samples() {
    }

    /**
     * 
     * @param recordedRoute
     * @param distance
     * @param speed
     * @param heartRate
     */
    public Samples(List<HeartRate__> heartRate, List<Speed_> speed, List<Distance> distance, List<RecordedRoute> recordedRoute) {
        super();
        this.heartRate = heartRate;
        this.speed = speed;
        this.distance = distance;
        this.recordedRoute = recordedRoute;
    }

    public List<HeartRate__> getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(List<HeartRate__> heartRate) {
        this.heartRate = heartRate;
    }

    public List<Speed_> getSpeed() {
        return speed;
    }

    public void setSpeed(List<Speed_> speed) {
        this.speed = speed;
    }

    public List<Distance> getDistance() {
        return distance;
    }

    public void setDistance(List<Distance> distance) {
        this.distance = distance;
    }

    public List<RecordedRoute> getRecordedRoute() {
        return recordedRoute;
    }

    public void setRecordedRoute(List<RecordedRoute> recordedRoute) {
        this.recordedRoute = recordedRoute;
    }

}
