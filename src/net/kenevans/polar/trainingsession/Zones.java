
package net.kenevans.polar.trainingsession;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Zones {

    @SerializedName("heart_rate")
    @Expose
    private List<HeartRate_> heartRate = null;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Zones() {
    }

    /**
     * 
     * @param heartRate
     */
    public Zones(List<HeartRate_> heartRate) {
        super();
        this.heartRate = heartRate;
    }

    public List<HeartRate_> getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(List<HeartRate_> heartRate) {
        this.heartRate = heartRate;
    }

}
