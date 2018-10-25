package com.icrisat.sbdm.ismu.util;

import java.util.HashMap;
import java.util.Map;

public class MarkerSummaryInfo {

    private String MarkerName;
    private int missingPercent;
    private Map<String, Integer> mafValues = new HashMap<>();
    private float mafValue;
    private float picValue;

    public String getMarkerName() {
        return MarkerName;
    }

    public void setMarkerName(String markerName) {
        MarkerName = markerName;
    }

    public int getMissingPercent() {
        return missingPercent;
    }

    public void setMissingPercent(int missingPercent) {
        this.missingPercent = missingPercent;
    }

    public Map<String, Integer> getMafValues() {
        return mafValues;
    }

    public void setMafValues(Map<String, Integer> mafValues) {
        this.mafValues = mafValues;
    }

    public float getMafValue() {
        return mafValue;
    }

    public void setMafValue(float mafValue) {
        this.mafValue = mafValue;
    }

    public float getPicValue() {
        return picValue;
    }

    public void setPicValue(float picValue) {
        this.picValue = picValue;
    }
}
