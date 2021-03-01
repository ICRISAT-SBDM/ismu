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
        private AvailableFormats[] availableFormats;
        private String callSetCount;
        private String variantCount;
        private String created;
        private String updated;

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
        private String created;
        private String description;

        public String getAnalysisDbId() {
            return analysisDbId;
        }

        public String getAnalysisName() {
            return analysisName;
        }

        public String getCreated() {
            return created;
        }

        public String getDescription() {
            return description;
        }
    }

    class AvailableFormats {
        private String dataFormat;
        private String fileFormat;
        private String fileURL;
        private String sepUnphased;
        private String unknownString;

        public String getDataFormat() {
            return dataFormat;
        }

        public String getFileFormat() {
            return fileFormat;
        }

        public String getFileURL() {
            return fileURL;
        }

        public String getSepUnphased() {
            return sepUnphased;
        }

        public String getUnknownString() {
            return unknownString;
        }
    }
}
