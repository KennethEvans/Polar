package net.kenevans.polar.accessmanager.classes;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhysicalInformations
{

    @SerializedName("physical-informations")
    @Expose
    public List<String> physicalInformations = null;

}