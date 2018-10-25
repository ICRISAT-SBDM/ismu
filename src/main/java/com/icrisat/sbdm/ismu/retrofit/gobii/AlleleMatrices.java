package com.icrisat.sbdm.ismu.retrofit.gobii;

import java.util.List;

public class AlleleMatrices {

    private Result result;

    public Result getResult() {
        return result;
    }

    class Data {
        private String name;
        private String matrixDbId;
        private String description;
        private String lastUpdated;
        private String studyDbId;

        public String getName() {
            return name;
        }

        public String getMatrixDbId() {
            return matrixDbId;
        }

        public String getDescription() {
            return description;
        }

        public String getLastUpdated() {
            return lastUpdated;
        }

        public String getStudyDbId() {
            return studyDbId;
        }
    }

    class Result {
        private List<Data> data;

        public List<Data> getData() {
            return data;
        }
    }
}
