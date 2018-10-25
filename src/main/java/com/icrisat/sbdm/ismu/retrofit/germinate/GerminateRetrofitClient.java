package com.icrisat.sbdm.ismu.retrofit.germinate;

import com.google.gson.Gson;
import com.icrisat.sbdm.ismu.retrofit.*;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.icrisat.sbdm.ismu.retrofit.RetrofitUtil.*;
import static com.icrisat.sbdm.ismu.retrofit.germinate.GerminateRetrofitUtil.createClient;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GerminateRetrofitClient {

    private GerminateClient client;
    private Logger logger;

    /**
     * Authenticate to GOBII.
     * //TODO: currently this just creates client. Auth part will be added when required
     *
     * @param URL      URL for GOBII service
     * @param userName UserName
     * @param password password
     * @param logger logger
     * @return status of the rest call.
     */
    public String authenticate(String URL, String userName, String password, Logger logger) {
        this.logger = logger;
        this.logger.info("Germinate call details: " + URL + " " + userName + " " + password);
        String status = Constants.SUCCESS;
        try {
            client = createClient(URL);
        } catch (Exception e) {
            status = e.getMessage();
        }
        return status;
    }

    public String getStudies(List<String[]> studiesList, SharedInformation sharedInformation) {
        return callStudies(client.getStudies("genotype"), studiesList,sharedInformation);
    }

    public List<String> getMarkerProfiles(List selectedData) {
        List<String> response = new ArrayList<>();
        String status = Constants.SUCCESS;
        logger.info("Submitting marker profile request for: " + selectedData.get(1));
        String studyDBId = (String) selectedData.get(1);
        Call<MarkerProfiles> markerProfilesCall = client.getMarkerProfiles(studyDBId);
        try {
            Response<MarkerProfiles> markerProfilesResponse = markerProfilesCall.execute();
            if (markerProfilesResponse.isSuccessful()) {
                MarkerProfiles markerProfilesBody = markerProfilesResponse.body();
                List<MarkerProfiles.Data> dataList = markerProfilesBody.getResult().getData();
                for (MarkerProfiles.Data data : dataList) {
                    response.add(data.getMarkerprofileDbId());
                }

            } else {
                RetrofitError errorMessage = new Gson().fromJson(markerProfilesResponse.errorBody().charStream(), RetrofitError.class);
                status = returnExitStatus(markerProfilesResponse.code(), errorMessage.toString());
            }
        } catch (IOException e) {
            status = Constants.NO_INTERNET;
            logger.error(status + "\t" + e.getMessage());
        }
        response.add(status);
        return response;
    }

    public List<String> downloadData(List<String> markerProfiles) {
        List<String> response = new ArrayList<>();
        String status = Constants.SUCCESS;
        logger.info("Submitting data extract request for: " + markerProfiles);
        Call<ExtractResponse> downloadDataCall = client.getAlleleMatrix(markerProfiles, "flapjack");
        try {
            Response<ExtractResponse> downloadResponse = downloadDataCall.execute();
            if (downloadResponse.isSuccessful()) {
                ExtractResponse markerProfilebody = downloadResponse.body();
                if (markerProfilebody != null) {
                    Status[] statusArray = markerProfilebody.getMetadata().getStatus();
                    for (Status extractStatus : statusArray) {
                        response.add(extractStatus.getMessage());
                    }
                } else {
                    status = "Got null response.";
                }
            } else {
                RetrofitError errorMessage = new Gson().fromJson(downloadResponse.errorBody().charStream(), RetrofitError.class);
                status = returnExitStatus(downloadResponse.code(), errorMessage.toString());
            }
        } catch (IOException e) {
            status = Constants.NO_INTERNET;
            logger.error(status + "\t" + e.getMessage());
        }
        response.add(status);
        return response;
    }

    public List<String> getExtractStatus(String jobId) {
        List<String> response = new ArrayList<>();
        Call<ExtractResponse> getExtractStatus = client.getExtractStatus(jobId);
        response.add(RetrofitUtil.extractDataset(response, getExtractStatus));
        return response;
    }


    public String downloadData(String url, String fileName) {
        logger.info("Downloading file from germinate server: " + url);
        Call<ResponseBody> call = client.downloadFileWithURL(url);
        return RetrofitUtil.downloadData(fileName, call, Constants.GERMINATE);
    }
}