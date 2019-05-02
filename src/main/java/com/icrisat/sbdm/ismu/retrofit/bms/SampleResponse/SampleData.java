package com.icrisat.sbdm.ismu.retrofit.bms.SampleResponse;

import java.util.List;

public class SampleData {
    class Result {
        private List<Data> data;

        List<Data> getData() {
            return data;
        }
    }

    private Result result;

    public List<Data> getData() {
        return result.getData();
    }
}
