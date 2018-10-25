package com.icrisat.sbdm.ismu.retrofit;

import java.util.List;

public class Studies {

    private Metadata metadata;
    private Result result;

    public Metadata getMetadata() {
        return metadata;
    }

    public Result getResult() {
        return result;
    }

    public class Result {
        private List<Data> data;

        public List<Data> getData() {
            return data;
        }
    }

    public class Data {

        private String studyDbId;
        private String name;
        private String trialDbId;
        private String trialName;
        private String seasons;
        private String locationDbId;
        private String locationName;
        private String programDbId;
        private String programName;
        private String startDate;
        private String endDate;
        private String studyType;
        private String active;
        private List<String> additionalInfo;

        public String getStudyDbId() {
            return studyDbId;
        }

        public String getName() {
            return name;
        }

        public String getTrialDbId() {
            return trialDbId;
        }

        public String getTrialName() {
            return trialName;
        }

        public String getSeasons() {
            return seasons;
        }

        public String getLocationDbId() {
            return locationDbId;
        }

        public String getLocationName() {
            return locationName;
        }

        public String getProgramDbId() {
            return programDbId;
        }

        public String getProgramName() {
            return programName;
        }

        public String getStartDate() {
            return startDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public String getStudyType() {
            return studyType;
        }

        public String getActive() {
            return active;
        }

        public List<String> getAdditionalInfo() {
            return additionalInfo;
        }
    }
}

