
package net.kenevans.polar.trainingsession;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PhysicalInformationSnapshot {

    @SerializedName("dateTime")
    @Expose
    private String dateTime;
    @SerializedName("sex")
    @Expose
    private String sex;
    @SerializedName("birthday")
    @Expose
    private String birthday;
    @SerializedName("height, cm")
    @Expose
    private Double heightCm;
    @SerializedName("weight, kg")
    @Expose
    private Double weightKg;
    @SerializedName("vo2Max")
    @Expose
    private Integer vo2Max;
    @SerializedName("maximumHeartRate")
    @Expose
    private Integer maximumHeartRate;
    @SerializedName("restingHeartRate")
    @Expose
    private Integer restingHeartRate;
    @SerializedName("functionalThresholdPower")
    @Expose
    private Integer functionalThresholdPower;

    /**
     * No args constructor for use in serialization
     * 
     */
    public PhysicalInformationSnapshot() {
    }

    /**
     * 
     * @param vo2Max
     * @param birthday
     * @param sex
     * @param heightCm
     * @param restingHeartRate
     * @param dateTime
     * @param weightKg
     * @param maximumHeartRate
     * @param functionalThresholdPower
     */
    public PhysicalInformationSnapshot(String dateTime, String sex, String birthday, Double heightCm, Double weightKg, Integer vo2Max, Integer maximumHeartRate, Integer restingHeartRate, Integer functionalThresholdPower) {
        super();
        this.dateTime = dateTime;
        this.sex = sex;
        this.birthday = birthday;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
        this.vo2Max = vo2Max;
        this.maximumHeartRate = maximumHeartRate;
        this.restingHeartRate = restingHeartRate;
        this.functionalThresholdPower = functionalThresholdPower;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public Integer getVo2Max() {
        return vo2Max;
    }

    public void setVo2Max(Integer vo2Max) {
        this.vo2Max = vo2Max;
    }

    public Integer getMaximumHeartRate() {
        return maximumHeartRate;
    }

    public void setMaximumHeartRate(Integer maximumHeartRate) {
        this.maximumHeartRate = maximumHeartRate;
    }

    public Integer getRestingHeartRate() {
        return restingHeartRate;
    }

    public void setRestingHeartRate(Integer restingHeartRate) {
        this.restingHeartRate = restingHeartRate;
    }

    public Integer getFunctionalThresholdPower() {
        return functionalThresholdPower;
    }

    public void setFunctionalThresholdPower(Integer functionalThresholdPower) {
        this.functionalThresholdPower = functionalThresholdPower;
    }

}
