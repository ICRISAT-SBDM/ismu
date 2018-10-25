package com.icrisat.sbdm.ismu.retrofit.bms;

import java.util.List;

/**
 * Class to represent the Crops
 */
class Crops {

    class Data {
        private List<String> data;

        List<String> getData() {
            return data;
        }
    }

    private Data result;

    Data getResult() {
        return result;
    }
}

