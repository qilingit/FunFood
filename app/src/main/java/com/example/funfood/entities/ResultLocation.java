package com.example.funfood.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ResultLocation implements Serializable {
    @SerializedName("open_now")
    private Boolean openNow;

    @SerializedName("weekday_text")
    private List<Object> weekdayText = new ArrayList<Object>();

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lng")
    private double longitude;

    /**
     * @return The openNow
     */
    public Boolean getOpenNow() {
        return openNow;
    }

    /**
     * @param openNow The open_now
     */
    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    /**
     * @return The weekdayText
     */
    public List<Object> getWeekdayText() {
        return weekdayText;
    }

    /**
     * @param weekdayText The weekday_text
     */
    public void setWeekdayText(List<Object> weekdayText) {
        this.weekdayText = weekdayText;
    }

    /**
     * @return latitude of place
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return longitude of place
     */
    public double getLongitude() {
        return longitude;
    }
}
