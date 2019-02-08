package net.kenevans.polar.accessmanager.classes;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exercise
{

    @SerializedName("exercise")
    @Expose
    public List<String> exercise = null;

}