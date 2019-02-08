package net.kenevans.polar.accessmanager.classes;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AvailableUserData
{

    @SerializedName("available-user-data")
    @Expose
    public List<AvailableUserDatum> availableUserData = null;

}
