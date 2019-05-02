package com.icrisat.sbdm.ismu.retrofit.bms.TriatResponse;

public class Observations {
    private String observationDbId;
    private String observationVariableDbId;
    private String observationVariableName;
    private String observationTimeStamp;
    private String season;
    private String collector;
    private String value;

    public String getObservationVariableName() {
        return observationVariableName;
    }

    public String getValue() {
        return value;
    }

}
