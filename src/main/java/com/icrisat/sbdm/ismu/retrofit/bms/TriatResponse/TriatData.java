package com.icrisat.sbdm.ismu.retrofit.bms.TriatResponse;

import java.util.List;

/**
 * BMS Trial data
 */
public class TriatData {
    class Result {
        private List<Data> data;

        public List<Data> getData() {
            return data;
        }
    }

    private Result result;

    public List<Data> getData() {
        return result.getData();
    }
}
