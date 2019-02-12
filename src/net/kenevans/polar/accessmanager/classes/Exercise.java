
package net.kenevans.polar.accessmanager.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exercise {

    @SerializedName("upload-time")
    @Expose
    public String uploadTime;
    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("polar-user")
    @Expose
    public String polarUser;
    @SerializedName("transaction-id")
    @Expose
    public Integer transactionId;
    @SerializedName("device")
    @Expose
    public String device;
    @SerializedName("start-time")
    @Expose
    public String startTime;
    @SerializedName("duration")
    @Expose
    public String duration;
    @SerializedName("calories")
    @Expose
    public Integer calories;
    @SerializedName("distance")
    @Expose
    public Integer distance;
    @SerializedName("heart-rate")
    @Expose
    public HeartRate heartRate;
    @SerializedName("training-load")
    @Expose
    public Double trainingLoad;
    @SerializedName("sport")
    @Expose
    public String sport;
    @SerializedName("has-route")
    @Expose
    public Boolean hasRoute;
    @SerializedName("club-id")
    @Expose
    public Integer clubId;
    @SerializedName("club-name")
    @Expose
    public String clubName;
    @SerializedName("detailed-sport-info")
    @Expose
    public String detailedSportInfo;

}
