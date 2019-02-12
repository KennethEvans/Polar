
package net.kenevans.polar.accessmanager.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HeartRate {

    @SerializedName("average")
    @Expose
    public Integer average;
    @SerializedName("maximum")
    @Expose
    public Integer maximum;

}
