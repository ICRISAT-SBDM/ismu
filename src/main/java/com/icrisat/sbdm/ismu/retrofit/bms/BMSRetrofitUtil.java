package com.icrisat.sbdm.ismu.retrofit.bms;

import com.icrisat.sbdm.ismu.retrofit.bms.TriatResponse.Data;
import com.icrisat.sbdm.ismu.retrofit.bms.TriatResponse.Observations;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.PathConstants;
import com.opencsv.CSVWriter;
import okhttp3.OkHttpClient;
import org.slf4j.Logger;
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
     * <p>
     * TODO: Currently we are assuming there is one to one relation between observationId and sampleId
     */
    static String writeTrialDataToFile(List<Data> triatJSONList, Logger logger) {
        String status = Constants.SUCCESS;
        String outputFileName = PathConstants.resultDirectory + "BMS_data" + new SimpleDateFormat("hhmmss").format(new Date()) + ".csv";
        String uuidGermplasmNameFileName = PathConstants.resultDirectory + "BMS_data_uuid" + new SimpleDateFormat("hhmmss").format(new Date()) + ".csv";
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFileName));
             CSVWriter uuidWriter = new CSVWriter(new FileWriter(uuidGermplasmNameFileName))) {
            //Write Headers
            List<String> headers = new ArrayList<>();
            List<String> uuidHeaders = new ArrayList<>();
            headers.add(Constants.GERMPLASM_NAME);
            uuidHeaders.add(Constants.SAMPLE_ID);
            uuidHeaders.add(Constants.GERMPLASM_NAME);
            List<Observations> observations = triatJSONList.get(0).getObservations();
            Map<String, String> rowMap = new HashMap<>();
            for (Observations observation : observations) {
                headers.add(observation.getObservationVariableName());
                rowMap.put(observation.getObservationVariableName(), "NA");
            }
            csvWriter.writeNext(headers.toArray(new String[headers.size()]));
            uuidWriter.writeNext(uuidHeaders.toArray(new String[uuidHeaders.size()]));
            headers.remove(0);
            // Write Data
            for (Data triatJSON : triatJSONList) {
                for (Map.Entry<String, String> myEntry : rowMap.entrySet()) {
                    myEntry.setValue("NA");
                }
                List<String> dataRow = new ArrayList<>();
                List<String> uuidRow = new ArrayList<>();
                uuidRow.add(triatJSON.getGermplasmName());
                uuidRow.add(triatJSON.getObservationUnitDbId());
                dataRow.add(triatJSON.getGermplasmName());
                List<Observations> observationList = triatJSON.getObservations();
                for (Observations observation : observationList) {
                    rowMap.replace(observation.getObservationVariableName(), observation.getValue());
                }
                for (String header : headers) {
                    dataRow.add(rowMap.get(header));
                }
                csvWriter.writeNext(dataRow.toArray(new String[dataRow.size()]));
                uuidWriter.writeNext(uuidRow.toArray(new String[uuidRow.size()]));
            }
            csvWriter.flush();
            PathConstants.recentPhenotypeFile = outputFileName;
        } catch (Exception e) {
            status = e.getMessage();
            logger.error(e.getMessage() + e.getStackTrace());
        }
        return status;
    }
}
