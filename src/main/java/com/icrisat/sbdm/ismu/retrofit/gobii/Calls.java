package com.icrisat.sbdm.ismu.retrofit.gobii;

import com.icrisat.sbdm.ismu.retrofit.gobii.metadata.Metadata;

public class Calls {
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
        private String callSetDbId;
        private String callSetName;
        private String variantDbId;
        private String variantName;
        private Genotype genotype;

        public String getVariantSetDbId() {
            return variantSetDbId;
        }

        public String getCallSetDbId() {
            return callSetDbId;
        }

        public String getCallSetName() {
            return callSetName;
        }

        public String getVariantDbId() {
            return variantDbId;
        }

        public String getVariantName() {
            return variantName;
        }

        public Genotype getGenotype() {
            return genotype;
        }
    }

    public class Genotype {
        private String[] values;

        public String[] getValues() {
            return values;
        }
    }
}
