package net.kenevans.polar.accessmanager.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserField
{

    @SerializedName("value")
    @Expose
    public String value;
    @SerializedName("index")
    @Expose
    public Integer index;
    @SerializedName("name")
    @Expose
    public String name;

}
