
package net.kenevans.polar.trainingsession;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HeartRate {

    @SerializedName("min")
    @Expose
    private Integer min;
    @SerializedName("avg")
    @Expose
    private Integer avg;
    @SerializedName("max")
    @Expose
    private Integer max;

    /**
     * No args constructor for use in serialization
     * 
     */
    public HeartRate() {
    }

    /**
     * 
     * @param min
     * @param max
     * @param avg
     */
    public HeartRate(Integer min, Integer avg, Integer max) {
        super();
        this.min = min;
        this.avg = avg;
        this.max = max;
    }

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getAvg() {
        return avg;
    }

    public void setAvg(Integer avg) {
        this.avg = avg;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

}
