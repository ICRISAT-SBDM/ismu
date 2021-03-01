package com.icrisat.sbdm.ismu.retrofit.gobii;

import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.GenoFileFirstTImeProcessing;
import org.slf4j.Logger;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.ArrayList;
import java.util.List;

class GOBIIRetrofitUtil {
    /**
     * Creates a retrofit client.
     *
     * @param URL GOBII URL
     * @return retrofit client.
     */
    static GOBIIClient createClient(String URL) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        return retrofit.create(GOBIIClient.class);
    }

    /**
     * @param variantSetsJSON input JSON
     * @param variantSetsList varaiant set
     * @param logger
     */
    static void processVariantSets(Variantsets variantSetsJSON, List<String[]> variantSetsList, Logger logger) {
        if (variantSetsJSON == null || variantSetsJSON.getResult() == null || variantSetsJSON.getResult().getData() == null)
            logger.info("Processing varientsets null");
        Variantsets.Data[] variantSets = variantSetsJSON.getResult().getData();
        for (Variantsets.Data variantSet : variantSets) {
            String[] data = new String[4];
            data[0] = variantSet.getVariantSetName();
            data[1] = variantSet.getStudyName();
            data[2] = variantSet.getVariantSetDbId();
            data[3] = variantSet.getStudyDbId();
            variantSetsList.add(data);
        }
    }

    static String processCallSets(List<Calls> callsJSON, String fileName) {
        String variantName = null;
        List<String> callNames = new ArrayList<>();
        List<String> variantNames = new ArrayList<>();
        List<String> duplicateVariantNames = new ArrayList<>();

        List<List<String>> variantSet = new ArrayList<>();
        List<String> row = new ArrayList<>();
        boolean isFirstRow = true;
        for (Calls callJSON : callsJSON) {
            Calls.Data[] calls = callJSON.getResult().getData();
            for (Calls.Data call : calls) {
                if (variantName == null) {
                    variantName = call.getVariantName();
                    callNames.add("Marker");
                }
                if (variantName.equalsIgnoreCase(call.getVariantName())) {
                    // It is the same row
                    String[] split = call.getCallSetName().split(":");
                    callNames.add(split[0]);
                    row.add(call.getGenotype().getValues()[0]);
                } else {
                    if (isFirstRow) {
                        variantSet.add(callNames);
                        isFirstRow = false;
                    }
                    row.add(0, variantName);
                    variantSet.add(row);
                    row = new ArrayList<>();
                    callNames = new ArrayList<>();
                    variantName = call.getVariantName();
                    callNames.add(call.getCallSetName());
                    if (variantNames.contains(variantName))
                        duplicateVariantNames.add(variantName);
                    variantNames.add(variantName);
                    row.add(call.getGenotype().getValues()[0]);
                }
            }
        }

        //filter duplicate rows
        List<List<String>> uniqueVariantSet = new ArrayList<>();
        for (List<String> variant : variantSet) {
            if(duplicateVariantNames.contains(variant.get(0))){
                duplicateVariantNames.remove(variant.get(0));
            }else
                uniqueVariantSet.add(variant);
        }
        try {
            GenoFileFirstTImeProcessing.genofileComputation(fileName, uniqueVariantSet);
            return Constants.SUCCESS;
        } catch (Exception e) {
            return "Error in writing file to disk. Please check log file for details.";
        }
    }
}
