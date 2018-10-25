package com.icrisat.sbdm.ismu.retrofit.bms;

import com.icrisat.sbdm.ismu.retrofit.Metadata;

import java.util.List;

/**
 * Trial information from REST call.
 */
class Trials {

    class Studies {
        private String studyDbId;
        private String studyName;
        //private String locationDbId;
        private String locationName;

        String getStudyDbId() {
            return studyDbId;
        }

        String getStudyName() {
            return studyName;
        }

        String getLocationName() {
            return locationName;
        }
    }

    class Data {
        private String trialDbId;
        private String trialName;
        private String programDbId;
        private String programName;
        //private String startDate;
        //private String endDate;
        //private String active;
        private List<Studies> studies;

        String getTrialDbId() {
            return trialDbId;
        }

        String getTrialName() {
            return trialName;
        }

        private String getProgramDbId() {
            return programDbId;
        }

        String getProgramName() {
            return programName;
        }

        List<Studies> getStudies() {
            return studies;
        }
    }

    class Result {
        private List<Data> data;

        List<Data> getData() {
            return data;
        }
    }

    private Metadata metadata;

    private Result result;

    Result getResult() {
        return result;
    }

    public Metadata getMetadata() {
        return metadata;
    }

}
