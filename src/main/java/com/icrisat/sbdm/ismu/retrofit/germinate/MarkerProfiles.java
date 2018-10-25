package com.icrisat.sbdm.ismu.retrofit.germinate;

import com.icrisat.sbdm.ismu.retrofit.Metadata;

import java.util.List;

public class MarkerProfiles {
    private Metadata metadata;
    private Result result;

    public Metadata getMetadata() {
        return metadata;
    }

    public Result getResult() {
        return result;
    }

    class Result {
        private List<Data> data;

        public List<Data> getData() {
            return data;
        }
    }

    class Data {
        private String markerprofileDbId;
        private String germplasmDbId;
        private String uniqueDisplayName;
        private String extractDbId;
        private String analysisMethod;
        private String resultCount;
        private String sampleDbId;

        public String getMarkerprofileDbId() {
            return markerprofileDbId;
        }

        public void setMarkerprofileDbId(String markerprofileDbId) {
            this.markerprofileDbId = markerprofileDbId;
        }

        public String getGermplasmDbId() {
            return germplasmDbId;
        }

        public void setGermplasmDbId(String germplasmDbId) {
            this.germplasmDbId = germplasmDbId;
        }

        public String getUniqueDisplayName() {
            return uniqueDisplayName;
        }

        public void setUniqueDisplayName(String uniqueDisplayName) {
            this.uniqueDisplayName = uniqueDisplayName;
        }

        public String getExtractDbId() {
            return extractDbId;
        }

        public void setExtractDbId(String extractDbId) {
            this.extractDbId = extractDbId;
        }

        public String getAnalysisMethod() {
            return analysisMethod;
        }

        public void setAnalysisMethod(String analysisMethod) {
            this.analysisMethod = analysisMethod;
        }

        public String getResultCount() {
            return resultCount;
        }

        public void setResultCount(String resultCount) {
            this.resultCount = resultCount;
        }

        public String getSampleDbId() {
            return sampleDbId;
        }

        public void setSampleDbId(String sampleDbId) {
            this.sampleDbId = sampleDbId;
        }
    }

}
