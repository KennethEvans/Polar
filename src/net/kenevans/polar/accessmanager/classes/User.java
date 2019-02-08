package net.kenevans.polar.accessmanager.classes;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User
{

    @SerializedName("polar-user-id")
    @Expose
    public String polarUserId;
    @SerializedName("member-id")
    @Expose
    public String memberId;
    @SerializedName("registration-date")
    @Expose
    public String registrationDate;
    @SerializedName("first-name")
    @Expose
    public String firstName;
    @SerializedName("last-name")
    @Expose
    public String lastName;
    @SerializedName("birthdate")
    @Expose
    public String birthdate;
    @SerializedName("gender")
    @Expose
    public String gender;
    @SerializedName("weight")
    @Expose
    public Double weight;
    @SerializedName("height")
    @Expose
    public Double height;
    @SerializedName("field")
    @Expose
    public List<UserField> field = null;

}