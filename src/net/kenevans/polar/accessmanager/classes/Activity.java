package net.kenevans.polar.accessmanager.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Activity
{

    @SerializedName("id")
    @Expose
    public Integer id;
    @SerializedName("polar-user")
    @Expose
    public String polarUser;
    @SerializedName("transaction-id")
    @Expose
    public Integer transactionId;
    @SerializedName("date")
    @Expose
    public String date;
    @SerializedName("created")
    @Expose
    public String created;
    @SerializedName("calories")
    @Expose
    public Integer calories;
    @SerializedName("active-calories")
    @Expose
    public Integer activeCalories;
    @SerializedName("duration")
    @Expose
    public String duration;
    @SerializedName("active-steps")
    @Expose
    public Integer activeSteps;

}