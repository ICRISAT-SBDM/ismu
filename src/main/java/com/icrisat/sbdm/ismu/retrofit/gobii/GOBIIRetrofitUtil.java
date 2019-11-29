package com.icrisat.sbdm.ismu.retrofit.gobii;

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
