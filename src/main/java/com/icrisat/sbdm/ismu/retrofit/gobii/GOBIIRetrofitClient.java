package com.icrisat.sbdm.ismu.retrofit.gobii;

import com.google.gson.Gson;
import com.icrisat.sbdm.ismu.retrofit.ExtractResponse;
import com.icrisat.sbdm.ismu.retrofit.RetrofitError;
import com.icrisat.sbdm.ismu.retrofit.Status;
import com.icrisat.sbdm.ismu.util.Constants;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
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
    private Token token;

    /**
     * Authenticate to GOBII.
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
        Call<Token> authUser = client.authUser("", userName, password);
        try {
            Response<Token> response = authUser.execute();
            if (response.isSuccessful()) {
                token = response.body();
            } else {
                RetrofitError errorMessage = new Gson().fromJson(response.errorBody().charStream(), RetrofitError.class);
                status = returnExitStatus(response.code(), errorMessage.toString());
            }
        } catch (Exception e) {
            status = "Check URL and internet connection." + e.getMessage();
            logger.error(status + "\t" + e.getMessage());
        }
        return status;
    }

    /**
     * @param variantSetList List of variantSets.
     * @return Success status.
     */
    public String getVariantSets(List<String[]> variantSetList) {
        String status = Constants.SUCCESS;
        Call<Variantsets> getVariantSets = client.getVariantSets(token.getToken());
        try {
            Response<Variantsets> dataSetsResponse = getVariantSets.execute();
            if (dataSetsResponse.isSuccessful()) {
                Variantsets variantsetsJSON = dataSetsResponse.body();
                if (variantsetsJSON != null)
                    processVariantSets(variantsetsJSON, variantSetList);
                else
                    status = "Could not receive any datasets.";
            } else {
                String serverStatus = checkForServerError(dataSetsResponse.errorBody().byteStream());
                if (!serverStatus.equalsIgnoreCase(Constants.SUCCESS))
                    return serverStatus;
                RetrofitError errorMessage = new Gson().fromJson(dataSetsResponse.errorBody().charStream(), RetrofitError.class);
                status = returnExitStatus(dataSetsResponse.code(), errorMessage.toString());
            }
        } catch (Exception e) {
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
    public String downloadData(List selectedData, String fileName) {
        String status = Constants.SUCCESS;
        logger.info("Submitting data download request for: " + selectedData.get(0) + " with id: " + selectedData.get(2));
        int variantSetId = Integer.parseInt((String) selectedData.get(2));
        Call<Calls> downloadVariantSetCall = client.downloadVariantSet(token.getToken(), variantSetId, 1000);
        try {
            Response<Calls> downloadVariantSetResponse = downloadVariantSetCall.execute();
            if (downloadVariantSetResponse.isSuccessful()) {
                Calls callResponseJSON = downloadVariantSetResponse.body();
                if (callResponseJSON != null) {
                    status = processCallSets(callResponseJSON, fileName);
                } else {
                    status = "Got null response.";
                }
            } else {
                RetrofitError errorMessage = new Gson().fromJson(downloadVariantSetResponse.errorBody().charStream(), RetrofitError.class);
                status = returnExitStatus(downloadVariantSetResponse.code(), errorMessage.toString());
            }
        } catch (Exception e) {
            status = e.getMessage();
            logger.error(status);
        }
        return status;
    }

    /**
     * Issue an extract request to the selected matrixDbId
     *
     * @param markerProfileId markerprofile ID
     * @return extract jobId with status message
     */
    public List<String> extractByExternalCodes(String markerProfileId) {
        List<String> response = new ArrayList<>();
        String status = Constants.SUCCESS;
        logger.info("Submitting data extract request for: " + markerProfileId);
        Call<ExtractResponse> extractByExternalCodes = client.extractByExternalCodes(token.getToken(), markerProfileId);
        try {
            Response<ExtractResponse> extractDataSetResponse = extractByExternalCodes.execute();
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
}