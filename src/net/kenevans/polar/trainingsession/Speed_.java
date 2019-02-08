
package net.kenevans.polar.trainingsession;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Speed_ {

    @SerializedName("dateTime")
    @Expose
    private String dateTime;
    @SerializedName("value")
    @Expose
    private Double value;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Speed_() {
    }

    /**
     * 
     * @param dateTime
     * @param value
     */
    public Speed_(String dateTime, Double value) {
        super();
        this.dateTime = dateTime;
        this.value = value;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

}
