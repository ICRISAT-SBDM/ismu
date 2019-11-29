package com.icrisat.sbdm.ismu.retrofit.gobii;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.icrisat.sbdm.ismu.util.GenoFileFirstTImeProcessing;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GOBIIRetrofitUtil {
    /**
     * Creates a retrofit client.
     *
     * @param URL GOBII URL
     * @return retrofit client.
     */
    static GOBIIClient createClient(String URL) {
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        return retrofit.create(GOBIIClient.class);
    }

    /**
     * @param variantsetsJSON input JSON
     * @param variantSetsList varaiant set
     */
    static void processVariantSets(Variantsets variantsetsJSON, List<String[]> variantSetsList) {
        Variantsets.Data[] variantSets = variantsetsJSON.getResult().getData();
        for (Variantsets.Data variantSet : variantSets) {
            String[] data = new String[4];
            data[0] = variantSet.getVariantSetName();
            data[1] = variantSet.getStudyName();
            data[2] = variantSet.getVariantSetDbId();
            data[3] = variantSet.getStudyDbId();
            variantSetsList.add(data);
        }
    }

    static void processCallSets(Calls callsJSON, String fileName) {
        Calls.Data[] calls = callsJSON.getResult().getData();
        String variantName = null;
        List<String> callNames = new ArrayList<>();
        List<List<String>> variantSet = new ArrayList<>();
        List<String> row = new ArrayList<>();
        boolean isFirstRow = true;
        for (Calls.Data call : calls) {
            if (variantName == null) {
                variantName = call.getVariantName();
                callNames.add("");
            }
            if (variantName.equalsIgnoreCase(call.getVariantName())) {
                // It is the same row
                callNames.add(call.getCallSetName());
                row.add(call.getGenotype().getStringValue());
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
                row.add(call.getGenotype().getStringValue());
            }
        }
        System.out.println("Check US");
    }

    public static boolean writeResponseBodyToDisk(ResponseBody body, String outputFileName) {
        List<List<String>> matrix = new ArrayList<>();
        List<List<String>> transposeMatrix = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(body.byteStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                matrix.add(Arrays.asList(line.split("\t")));
            }
            //Remove first line
            matrix.remove(0);

            //Perform the transpose
            int sizeOfCols = matrix.get(0).size();
            for (int i = 0; i < sizeOfCols; i++) {
                List<String> outputLine = new ArrayList<>();
                for (List<String> lines : matrix) {
                    outputLine.add(lines.get(i));
                }
                transposeMatrix.add(outputLine);
            }
            GenoFileFirstTImeProcessing.genofileComputation(outputFileName, transposeMatrix);
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
