
package net.kenevans.polar.trainingsession;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Speed {

    @SerializedName("avg")
    @Expose
    private Double avg;
    @SerializedName("max")
    @Expose
    private Double max;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Speed() {
    }

    /**
     * 
     * @param max
     * @param avg
     */
    public Speed(Double avg, Double max) {
        super();
        this.avg = avg;
        this.max = max;
    }

    public Double getAvg() {
        return avg;
    }

    public void setAvg(Double avg) {
        this.avg = avg;
    }

    public Double getMax() {
        return max;
    }

    public void setMax(Double max) {
        this.max = max;
    }

}
