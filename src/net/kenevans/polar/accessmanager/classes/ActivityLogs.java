
package net.kenevans.polar.accessmanager.classes;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ActivityLogs
{

    @SerializedName("activity-log")
    @Expose
    public List<String> activityLogs = null;

}