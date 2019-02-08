
package net.kenevans.polar.trainingsession;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HeartRate_ {

    @SerializedName("lowerLimit")
    @Expose
    private Integer lowerLimit;
    @SerializedName("higherLimit")
    @Expose
    private Integer higherLimit;
    @SerializedName("inZone")
    @Expose
    private String inZone;
    @SerializedName("zoneIndex")
    @Expose
    private Integer zoneIndex;

    /**
     * No args constructor for use in serialization
     * 
     */
    public HeartRate_() {
    }

    /**
     * 
     * @param lowerLimit
     * @param zoneIndex
     * @param inZone
     * @param higherLimit
     */
    public HeartRate_(Integer lowerLimit, Integer higherLimit, String inZone, Integer zoneIndex) {
        super();
        this.lowerLimit = lowerLimit;
        this.higherLimit = higherLimit;
        this.inZone = inZone;
        this.zoneIndex = zoneIndex;
    }

    public Integer getLowerLimit() {
        return lowerLimit;
    }

    public void setLowerLimit(Integer lowerLimit) {
        this.lowerLimit = lowerLimit;
    }

    public Integer getHigherLimit() {
        return higherLimit;
    }

    public void setHigherLimit(Integer higherLimit) {
        this.higherLimit = higherLimit;
    }

    public String getInZone() {
        return inZone;
    }

    public void setInZone(String inZone) {
        this.inZone = inZone;
    }

    public Integer getZoneIndex() {
        return zoneIndex;
    }

    public void setZoneIndex(Integer zoneIndex) {
        this.zoneIndex = zoneIndex;
    }

}
