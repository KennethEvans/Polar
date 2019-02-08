package net.kenevans.polar.accessmanager.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AvailableUserDatum
{

    @SerializedName("user-id")
    @Expose
    public Integer userId;
    @SerializedName("data-type")
    @Expose
    public String dataType;
    @SerializedName("url")
    @Expose
    public String url;

}
