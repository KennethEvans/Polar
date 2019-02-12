package net.kenevans.polar.accessmanager.classes;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Exercises
{

    @SerializedName("exercises")
    @Expose
    public List<String> exercises = null;

}