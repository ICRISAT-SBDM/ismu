package com.icrisat.sbdm.ismu.retrofit.bms.TriatResponse;

import java.util.List;

public class Data {
    private String observationUnitDbId;// Can be plot, plant or some id
    private String observationUnitName;
    private String observationLevel;
    private String observationLevels;
    private String plotNumber;
    private String plantNumber;
    private String blockNumber;
    private String replicate;
    private String germplasmDbId;
    private String germplasmName;
    private String studyDbId;
    private String studyName;
    private String studyLocationDbId;
    private String studyLocation;
    private String programName;
    private String x; // Co-ordinate in a field
    private String y; // co-ordinate in a field
    private String entryType;
    private String entryNumber;
    private List<Observations> observations;

    public String getObservationUnitDbId() {
        return observationUnitDbId;
    }

    public List<Observations> getObservations() {
        return observations;
    }

    public String getGermplasmName() {
        return germplasmName;
    }

    public void setGermplasmName(String germplasmName) {
        this.germplasmName = germplasmName;
    }
}
