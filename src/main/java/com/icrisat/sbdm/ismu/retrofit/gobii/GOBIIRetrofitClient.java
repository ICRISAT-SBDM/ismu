package com.icrisat.sbdm.ismu.retrofit.gobii;

import com.google.gson.Gson;
import com.icrisat.sbdm.ismu.retrofit.*;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.Util;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static com.icrisat.sbdm.ismu.retrofit.RetrofitUtil.checkForServerError;
import static com.icrisat.sbdm.ismu.retrofit.RetrofitUtil.returnExitStatus;
import static com.icrisat.sbdm.ismu.retrofit.gobii.GOBIIRetrofitUtil.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GOBIIRetrofitClient {

    private GOBIIClient client;
    private Logger logger;

    /**
     * Authenticate to GOBII.
     * //TODO: currently this just creates client. Auth part will be added when required
     *
     * @param URL      URL for GOBII service
     * @param userName UserName
     * @param password password
     * @param logger   logger
     * @return status of the rest call.
     */
    public String authenticate(String URL, String userName, String password, Logger logger) {
        this.logger = logger;
        this.logger.info("GOBII call details: " + URL + " " + userName + " " + password);
        String status = Constants.SUCCESS;
        try {
            client = createClient(URL);
        } catch (Exception e) {
            status = e.getMessage();
        }
        return status;
    }

    /**
     * @param dataSetsList List of datasets.
     * @return Success status.
     */
    public String getDataSets(List<String[]> dataSetsList) {
        String status = Constants.SUCCESS;
        Call<AlleleMatrices> getDataSets = client.getDataSets();
        try {
            Response<AlleleMatrices> dataSetsResponse = getDataSets.execute();
            if (dataSetsResponse.isSuccessful()) {
                AlleleMatrices alleleMatricesJSON = dataSetsResponse.body();
                if (alleleMatricesJSON != null)
                    processDataSets(alleleMatricesJSON, dataSetsList);
                else
                    status = "Could not receive any datasets.";
            } else {
                String serverStatus = checkForServerError(dataSetsResponse.errorBody().byteStream());
                if (!serverStatus.equalsIgnoreCase(Constants.SUCCESS))
                    return serverStatus;

                RetrofitError errorMessage = new Gson().fromJson(dataSetsResponse.errorBody().charStream(), RetrofitError.class);
                status = returnExitStatus(dataSetsResponse.code(), errorMessage.toString());
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
            status = e.getMessage();
        }
        return status;
    }

    /**
     * Issue an extract request to the selected matrixDbId
     *
     * @param selectedData Data selected. Second element is matrixDbId
     * @return extract jobId with status message
     */
    public List<String> extractData(List selectedData) {
        List<String> response = new ArrayList<>();
        String status = Constants.SUCCESS;
        logger.info("Submitting data extract request for: " + selectedData.get(0) + " with id: " + selectedData.get(1));
        String matrixDBId = (String) selectedData.get(1);
        Call<ExtractResponse> extractDataSetCall = client.extractDataSet(matrixDBId);
        try {
            Response<ExtractResponse> extractDataSetResponse = extractDataSetCall.execute();
            if (extractDataSetResponse.isSuccessful()) {
                ExtractResponse extractResponse = extractDataSetResponse.body();
                if (extractResponse != null && extractResponse.getMetadata() != null) {
                    Status[] statusArray = extractResponse.getMetadata().getStatus();
                    for (Status extractStatus : statusArray) {
                        response.add(extractStatus.getMessage());
                    }
                } else {
                    status = "Got null response.";
                }
            } else {
                RetrofitError errorMessage = new Gson().fromJson(extractDataSetResponse.errorBody().charStream(), RetrofitError.class);
                status = returnExitStatus(extractDataSetResponse.code(), errorMessage.toString());
            }
        } catch (IOException e) {
            status = Constants.NO_INTERNET;
            logger.error(status + "\t" + e.getMessage());
        }
        response.add(status);
        return response;
    }

    /**
     * Gets the job status if status is completed will get the genotype file.
     *
     * @param jobId job id
     * @return genotype file and status
     */
    public List<String> getExtractStatus(String jobId) {
        List<String> response = new ArrayList<>();
        logger.info("Requesting the status of the job: " + jobId);
        Call<ExtractResponse> getExtractStatus = client.getExtractStatus(jobId);
        response.add(RetrofitUtil.extractDataset(response, getExtractStatus));
        return response;
    }

    public String downloadData(String url, String fileName) {
        logger.info("Downloading file from GOBII server: " + url);
        Call<ResponseBody> call = client.downloadFileWithURL(url);
        return RetrofitUtil.downloadData(fileName, call, Constants.GOBII);
    }
}