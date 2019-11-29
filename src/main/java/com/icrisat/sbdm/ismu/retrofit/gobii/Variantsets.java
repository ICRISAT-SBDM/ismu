package com.icrisat.sbdm.ismu.retrofit.gobii;

import com.icrisat.sbdm.ismu.retrofit.gobii.metadata.Metadata;

public class Variantsets {
    private Metadata metadata;

    private Result result;

    public Result getResult() {
        return result;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public class Result {
        private Data[] data;

        public Data[] getData() {
            return data;
        }
    }

    public class Data {
        private String variantSetDbId;
        private String studyDbId;
        private String variantSetName;
        private String studyName;
        private Analyses[] analyses;

        public String getVariantSetDbId() {
            return variantSetDbId;
        }

        public String getStudyDbId() {
            return studyDbId;
        }

        public String getVariantSetName() {
            return variantSetName;
        }

        public String getStudyName() {
            return studyName;
        }

        public Analyses[] getAnalyses() {
            return analyses;
        }
    }

    class Analyses {
        private String analysisDbId;
        private String analysisName;
        private String createdDate;
        private String description;
        //TODO: SOftware some issue b/w spec and implementation
        private String type;
        private String updated;

        public String getAnalysisDbId() {
            return analysisDbId;
        }

        public String getAnalysisName() {
            return analysisName;
        }

        public String getCreatedDate() {
            return createdDate;
        }

        public String getDescription() {
            return description;
        }

        public String getType() {
            return type;
        }

        public String getUpdated() {
            return updated;
        }
    }
}
