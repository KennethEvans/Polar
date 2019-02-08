package net.kenevans.polar.accessmanager.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AccessToken {

@SerializedName("access_token")
@Expose
public String accessToken;
@SerializedName("token_type")
@Expose
public String tokenType;
@SerializedName("expires_in")
@Expose
public Integer expiresIn;
@SerializedName("x_user_id")
@Expose
public Integer xUserId;

}