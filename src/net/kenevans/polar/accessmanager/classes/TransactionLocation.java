package net.kenevans.polar.accessmanager.classes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionLocation {

@SerializedName("transaction-id")
@Expose
public Integer transactionId;
@SerializedName("resource-uri")
@Expose
public String resourceUri;

}