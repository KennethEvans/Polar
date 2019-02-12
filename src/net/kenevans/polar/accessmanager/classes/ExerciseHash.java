package net.kenevans.polar.accessmanager.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ExerciseHash {

@SerializedName("id")
@Expose
public String id;
@SerializedName("upload_time")
@Expose
public String uploadTime;
@SerializedName("polar_user")
@Expose
public String polarUser;
@SerializedName("device")
@Expose
public String device;
@SerializedName("start_time")
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
@SerializedName("heart_rate")
@Expose
public HeartRate heartRate;
@SerializedName("training_load")
@Expose
public Double trainingLoad;
@SerializedName("sport")
@Expose
public String sport;
@SerializedName("has_route")
@Expose
public Boolean hasRoute;
@SerializedName("club_id")
@Expose
public Integer clubId;
@SerializedName("club_name")
@Expose
public String clubName;
@SerializedName("detailed_sport_info")
@Expose
public String detailedSportInfo;

}