package com.icrisat.sbdm.ismu.retrofit.bms;

import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.opencsv.CSVWriter;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

class BMSRetrofitUtil {
    /**
     * Creates a retrofit client.
     *
     * @param URL BMS URL
     * @return retrofit client.
     */
    static BMSClient createClient(String URL) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .writeTimeout(1, TimeUnit.MINUTES)
                .build();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        return retrofit.create(BMSClient.class);
    }

    /**
     * Process the trial data and store it in trialList
     *
     * @param trialJSON    Trial data from REST call.
     * @param selectedCrop selected crop.
     * @param trialList    Get the following data : "Crop", "  Program  ", "  Trial  ", "  Study  ", "  Location  ", "Trial DB Id", "study DB Id"
     */
    static void processTrialData(Trials trialJSON, String selectedCrop, List<String[]> trialList) {
        List<Trials.Data> data = trialJSON.getResult().getData();
        for (Trials.Data trialData : data) {
            String programName = trialData.getProgramName();
            String trialName = trialData.getTrialName();
            String trialDBid = trialData.getTrialDbId();
            // Selecting it will download the complete trial information.
            List<Trials.Studies> studies = trialData.getStudies();
            if (studies.size() != 1) {
                trialList.add(new String[]{selectedCrop, programName, trialName, "", "", trialDBid, ""});
            }
            for (Trials.Studies study : studies) {
                String[] trialInstance = new String[7];
                trialInstance[0] = selectedCrop;
                trialInstance[1] = programName;
                trialInstance[2] = trialName;
                trialInstance[3] = (study.getStudyName().equalsIgnoreCase("null")) ? "" : study.getStudyName();
                trialInstance[4] = (study.getLocationName().equalsIgnoreCase("null")) ? "" : study.getLocationName();
                trialInstance[5] = trialDBid;
                trialInstance[6] = study.getStudyDbId();
                trialList.add(trialInstance);
            }
        }
    }

    /**
     * Writes the data from rest call into a csv file.
     * We need "germplasmName", "germplasmDbId", "plotNumber", "plotId", "blockNumber", "replicate", and triat no's
     *
     * @param trialJSON         Trail JSON
     * @param sharedInformation shared information.
     */
    static String writeTrialDataToFile(Trial_Study_DBData trialJSON, SharedInformation sharedInformation) {
        String status = Constants.SUCCESS;
        String originalOutputFileName = sharedInformation.getPathConstants().tempResultDirectory + "BMS_data_original" + new SimpleDateFormat("hhmmss").format(new Date()) + ".csv";
        String outputFileName = sharedInformation.getPathConstants().tempResultDirectory + "BMS_data" + new SimpleDateFormat("hhmmss").format(new Date()) + ".csv";
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFileName));
             CSVWriter originalFileCsvWriter = new CSVWriter(new FileWriter(originalOutputFileName))) {

            //Write Headers
            BMSFileLineObject bmsFileHeaderLineObject = new BMSFileLineObject();
            bmsFileHeaderLineObject.setBlockNumber(Constants.BLOCK_NUMBER);
            bmsFileHeaderLineObject.setPlotId(Constants.PLOT_ID);
            bmsFileHeaderLineObject.setGermplasmName(Constants.GERMPLASM_NAME);
            bmsFileHeaderLineObject.setGermplasmDbId(Constants.GERMPLASM_DB_ID);
            bmsFileHeaderLineObject.setPlotNumber(Constants.PLOT_NUMBER);
            bmsFileHeaderLineObject.setReplicate(Constants.REPLICATE);
            sharedInformation.getPathConstants().noOfHeadersPheno = bmsFileHeaderLineObject.getHeaderSize();
            bmsFileHeaderLineObject.setData(trialJSON.getResult().getObservationVariableNames());
            List<String> headersAndData = bmsFileHeaderLineObject.getHeadersAndData();
            // Write Data
            Map<String, Integer> headerIndex = getHeaderIndex(trialJSON.getResult().getHeaderRow());
            int noOfHeaders = trialJSON.getResult().getHeaderRow().size();
            List<BMSFileLineObject> outputLines = new ArrayList<>();
            originalFileCsvWriter.writeNext(headersAndData.toArray(new String[headersAndData.size()]));
            for (List<String> data : trialJSON.getResult().getData()) {
                BMSFileLineObject outputLine = getOutputLine(noOfHeaders, data, headerIndex);
                outputLines.add(outputLine);
                List<String> outputLineHeadersAndData = outputLine.getHeadersAndData();
                originalFileCsvWriter.writeNext(outputLineHeadersAndData.toArray(new String[outputLineHeadersAndData.size()]));
            }
            try {
                Set<Integer> qualitativeTraits = new HashSet<>();
                List<BMSFileLineObject> bmsFileLineObjects = performMean(outputLines, qualitativeTraits);
                List<Integer> qualitativeTraitsList = new ArrayList<>(qualitativeTraits);
                int noOfQualitativeTraits = qualitativeTraitsList.size();
                List<String> qualitativeTraits1 = new ArrayList<>();
                for (int i = noOfQualitativeTraits - 1; i >= 0; i--) {
                    int traitNo = qualitativeTraitsList.get(i);
                    qualitativeTraits1.add(headersAndData.remove(bmsFileHeaderLineObject.getHeaderSize() + traitNo));
                }
                sharedInformation.getPathConstants().qualitativeTraits = qualitativeTraits1;
                csvWriter.writeNext(headersAndData.toArray(new String[headersAndData.size()]));
                for (BMSFileLineObject line : bmsFileLineObjects) {
                    List<String> outputLineHeadersAndData = line.getHeadersAndData();
                    csvWriter.writeNext(outputLineHeadersAndData.toArray(new String[outputLineHeadersAndData.size()]));
                }
                originalFileCsvWriter.flush();
                csvWriter.flush();
                sharedInformation.getOpenDialog().getTxtPhenotype().setText(outputFileName);
            } catch (Exception e) {
                status = Constants.MISSING_DATA + "\n Please check " + originalOutputFileName;
            }
        } catch (Exception e) {
            status = e.getMessage();
            sharedInformation.getLogger().error(e.getMessage() + e.getStackTrace());
        }
        return status;
    }

    /**
     * Computes the mean of the bms data per germplasm-name.
     * This ignores the qualitative traits and stores this list of qualitative traits.
     *
     * @param outputLines       Data from BMS
     * @param qualitativeTraits Postition of qualitative traits
     * @return BMSFileLineObjects
     */
    private static List<BMSFileLineObject> performMean(List<BMSFileLineObject> outputLines, Set<Integer> qualitativeTraits) {
        System.out.println("Lets see.");
        Map<String, BMSFileLineObject> sumHashMap = new HashMap<>();
        for (BMSFileLineObject outputLine : outputLines) {
            if (sumHashMap.containsKey(outputLine.getGermplasmName())) {
                //Perform mean
                BMSFileLineObject bmsFileLineObject = sumHashMap.get(outputLine.getGermplasmName());
                List<String> hashData = bmsFileLineObject.getData();

                List<String> data = outputLine.getData();
                for (int i = 0; i < hashData.size(); i++) {
                    String character = data.get(i);
                    if (!character.equalsIgnoreCase("")) {
                        try {
                            // If the no of characters are more, float converts to 20160211 to 2.0160211E7
                            if (character.contains(".")) {
                                float floatValue = Float.parseFloat(character);
                                String s1 = hashData.get(i);
                                if (s1.equalsIgnoreCase("")) s1 = "0.0";
                                hashData.set(i, String.valueOf(Float.valueOf(s1) + floatValue));
                            } else {
                                long longValue = Long.parseLong(character);
                                String s1 = hashData.get(i);
                                if (s1.contains(".")) {
                                    if (s1.equalsIgnoreCase("")) s1 = "0.0";
                                    hashData.set(i, String.valueOf(Float.parseFloat(s1) + longValue));
                                } else {
                                    if (s1.equalsIgnoreCase(""))
                                        s1 = "0";
                                    hashData.set(i, String.valueOf(Long.parseLong(s1) + longValue));
                                }
                            }
                        } catch (Exception e) {
                            qualitativeTraits.add(i);
                        }

/*                        If it is a qualitative trait like characters print a messagw that we are considering only the quantitative
                        () -> traits. Display appropriate message. Test it with bean Trial: 4188
  */
                    }
                }
                bmsFileLineObject.setCount(bmsFileLineObject.getCount() + 1);
            } else {
                outputLine.setCount(1);
                sumHashMap.put(outputLine.getGermplasmName(), outputLine);
            }
        }

        List<BMSFileLineObject> meanLines = new ArrayList<>();
        for (Map.Entry<String, BMSFileLineObject> meanHashMapEntry : sumHashMap.entrySet()) {
            BMSFileLineObject entryValue = meanHashMapEntry.getValue();
            int count = entryValue.getCount();
            List<String> data = entryValue.getData();
            List<String> meanData = new ArrayList<>();
            for (int i = 0; i < data.size(); i++) {
                if (!qualitativeTraits.contains(i)) {
                    if (data.get(i).equalsIgnoreCase(""))
                        meanData.add(data.get(i));
                    else {
                        if (data.get(i).contains("."))
                            meanData.add(String.valueOf(Float.valueOf(data.get(i)) / count));
                        else {
                            long value = Long.valueOf(data.get(i));
                            if (value > 9999)
                                meanData.add(String.valueOf(Long.valueOf(data.get(i)) / count));
                            else
                                meanData.add(String.valueOf(Float.valueOf(data.get(i)) / count));
                        }
                    }
                }
            }
            entryValue.setData(meanData);
            meanLines.add(entryValue);
        }
        return meanLines;
    }

    /**
     * Parses a line from the rest call output and writes the required content to file.
     *
     * @param noOfHeaders     No of headers in the result.
     * @param data            Line of data from rest result.
     * @param requiredColumns Required columns have -1 if the value is not present else index.
     * @return output line
     */
    private static BMSFileLineObject getOutputLine(int noOfHeaders, List<String> data, Map<String, Integer> requiredColumns) {
        BMSFileLineObject bmsFileLineObject = new BMSFileLineObject();
        for (Map.Entry<String, Integer> requiredColumn : requiredColumns.entrySet()) {
            switch (requiredColumn.getKey()) {
                case Constants.PLOT_ID:
                    bmsFileLineObject.setPlotId(getValue(requiredColumn, data));
                    break;
                case Constants.GERMPLASM_NAME:
                    bmsFileLineObject.setGermplasmName(getValue(requiredColumn, data));
                    break;
                case Constants.GERMPLASM_DB_ID:
                    bmsFileLineObject.setGermplasmDbId(getValue(requiredColumn, data));
                    break;
                case Constants.PLOT_NUMBER:
                    bmsFileLineObject.setPlotNumber(getValue(requiredColumn, data));
                    break;
                case Constants.BLOCK_NUMBER:
                    bmsFileLineObject.setBlockNumber(getValue(requiredColumn, data));
                    break;
                case Constants.REPLICATE:
                    bmsFileLineObject.setReplicate(getValue(requiredColumn, data));
                    break;
            }
        }
        List<String> bmsData = bmsFileLineObject.getData();
        for (int i = noOfHeaders; i < data.size(); i++) {
            if (data.get(i) == null) {
                bmsData.add("");
            } else
                bmsData.add(data.get(i));
        }
        bmsFileLineObject.setData(bmsData);
        return bmsFileLineObject;
    }

    /**
     * Read the positions of the headers in the response and use them.
     * If not present then leave it as blank.
     */
    private static Map<String, Integer> getHeaderIndex(List<String> headers) {
        Map<String, Integer> indexes = new LinkedHashMap<>();
        if (headers.contains(Constants.PLOT_ID))
            indexes.put(Constants.PLOT_ID, headers.indexOf(Constants.PLOT_ID));
        else indexes.put(Constants.PLOT_ID, -1);

        if (headers.contains(Constants.GERMPLASM_NAME))
            indexes.put(Constants.GERMPLASM_NAME, headers.indexOf(Constants.GERMPLASM_NAME));
        else indexes.put(Constants.GERMPLASM_NAME, -1);

        if (headers.contains(Constants.GERMPLASM_DB_ID))
            indexes.put(Constants.GERMPLASM_DB_ID, headers.indexOf(Constants.GERMPLASM_DB_ID));
        else indexes.put(Constants.GERMPLASM_DB_ID, -1);

        if (headers.contains(Constants.PLOT_NUMBER))
            indexes.put(Constants.PLOT_NUMBER, headers.indexOf(Constants.PLOT_NUMBER));
        else indexes.put(Constants.PLOT_NUMBER, -1);

        if (headers.contains(Constants.BLOCK_NUMBER))
            indexes.put(Constants.BLOCK_NUMBER, headers.indexOf(Constants.BLOCK_NUMBER));
        else indexes.put(Constants.BLOCK_NUMBER, -1);

        if (headers.contains(Constants.REPLICATE))
            indexes.put(Constants.REPLICATE, headers.indexOf(Constants.REPLICATE));
        else indexes.put(Constants.REPLICATE, -1);
        return indexes;
    }

    private static String getValue(Map.Entry<String, Integer> requiredColumn, List<String> data) {
        String value;
        if (requiredColumn.getValue() == -1) {
            value = "";
        } else {
            if ((data.get(requiredColumn.getValue()) == null) || (data.get(requiredColumn.getValue()).equalsIgnoreCase("null"))) {
                value = "";
            } else
                value = data.get(requiredColumn.getValue());
        }
        return value;
    }
}
