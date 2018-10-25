package com.icrisat.sbdm.ismu.retrofit.gobii;

import com.opencsv.CSVWriter;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.*;
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
     * Process the allelematrices Json into a list.
     *
     * @param alleleMatricesJSON Allele matrices
     * @param dataSetsList       data set list
     */
    static void processDataSets(AlleleMatrices alleleMatricesJSON, List<String[]> dataSetsList) {
        List<AlleleMatrices.Data> dataSets = alleleMatricesJSON.getResult().getData();
        for (AlleleMatrices.Data dataSet : dataSets) {
            String[] data = new String[5];
            data[0] = dataSet.getName();
            data[1] = dataSet.getMatrixDbId();
            data[2] = dataSet.getDescription();
            data[3] = dataSet.getLastUpdated();
            data[4] = dataSet.getStudyDbId();
            dataSetsList.add(data);
        }
    }

    public static boolean writeResponseBodyToDisk(ResponseBody body, String outputFileName) {
        List<List<String>> matrix = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(body.byteStream()));
             CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                matrix.add(Arrays.asList(line.split("\t")));
            }
            //Remove first line
            matrix.remove(0);
            performTranspose(matrix, csvWriter);
            csvWriter.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    static void performTranspose(List<List<String>> matrix, CSVWriter csvWriter) {
        //TODO: Time assuming file size is small and reading everything into memory. Will do chunks and transpose later.
        int sizeOfCols = matrix.get(0).size();
        for (int i = 0; i < sizeOfCols; i++) {
            List<String> outputLine = new ArrayList<>();
            for (List<String> lines : matrix) {
                outputLine.add(lines.get(i));
            }
            csvWriter.writeNext(outputLine.toArray(new String[outputLine.size()]));
        }
    }
}
