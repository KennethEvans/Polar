package net.kenevans.polar.accessmanager.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhysicalInformation
{

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("transaction-id")
    @Expose
    public Integer transactionId;
    @SerializedName("created")
    @Expose
    public String created;
    @SerializedName("polar-user")
    @Expose
    public String polarUser;
    @SerializedName("weight")
    @Expose
    public Float weight;
    @SerializedName("height")
    @Expose
    public Float height;
    @SerializedName("maximum-heart-rate")
    @Expose
    public Integer maximumHeartRate;
    @SerializedName("resting-heart-rate")
    @Expose
    public Integer restingHeartRate;
    @SerializedName("aerobic-threshold")
    @Expose
    public Integer aerobicThreshold;
    @SerializedName("anaerobic-threshold")
    @Expose
    public Integer anaerobicThreshold;
    @SerializedName("vo2-max")
    @Expose
    public Integer vo2Max;
    @SerializedName("weight-source")
    @Expose
    public String weightSource;

}