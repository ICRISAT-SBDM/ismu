package com.icrisat.sbdm.ismu.retrofit.bms;

import java.util.List;

/**
 * BMS Trial data
 */
class Trial_Study_DBData {
    class Result {
        private List<String> headerRow;
        private List<String> observationVariableNames;
        private List<List<String>> data;

        List<String> getHeaderRow() {
            return headerRow;
        }

        List<String> getObservationVariableNames() {
            return observationVariableNames;
        }

        public List<List<String>> getData() {
            return data;
        }
    }


    private Result result;

    Result getResult() {
        return result;
    }
}
