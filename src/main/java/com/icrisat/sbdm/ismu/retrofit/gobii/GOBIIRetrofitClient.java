package com.icrisat.sbdm.ismu.retrofit.gobii;

import com.google.gson.Gson;
import com.icrisat.sbdm.ismu.retrofit.RetrofitError;
import com.icrisat.sbdm.ismu.util.Constants;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

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
     * @param logger   Logger
     * @return status of the rest call.
     */
    public String authenticate(String URL, String userName, String password, Logger logger) {
        this.logger = logger;
        String status = Constants.SUCCESS;
        try {
            client = createClient(URL);
        } catch (Exception e) {
            status = e.getMessage();
        }
        Call<Token> authUser = client.authUser(new User(userName, password));
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
        logger.info("Getting variant sets");
        Call<Variantsets> getVariantSets = client.getVariantSets(token.getAccess_token());
        try {
            Response<Variantsets> dataSetsResponse = getVariantSets.execute();
            if (dataSetsResponse.isSuccessful()) {
                logger.info("Data response successful");
                Variantsets variantsetsJSON = dataSetsResponse.body();
                if (variantsetsJSON != null) {
                    logger.info("Data response processing varients");
                    processVariantSets(variantsetsJSON, variantSetList, logger);
                } else {
                    status = "Could not receive any datasets.";
                    logger.info("Data response processing variants: null variant resonse");
                }
            } else {
                logger.info("Data response not successful");
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
        logger.info(variantSetList.size() + " variant sets received.");
        return status;
    }

    /**
     * Issue an extract request to the selected variantSet ID
     *
     * @return extract jobId with status message
     */
    public String downloadVariantSet(String variantSetName, String variantSetIdString, String fileName) {
        String status = Constants.SUCCESS;
        List<Calls> callResponseList = new ArrayList<>();
        logger.info("Submitting data download request for: " + variantSetName + " with id: " + variantSetIdString);
        int variantSetId = Integer.parseInt(variantSetIdString);
        String pageToken = "";
        try {
            do {
                logger.info("Submitting data download request for: " + variantSetName + " with id: " + variantSetId + " for page: " + pageToken);
                Call<Calls> downloadVariantSetCall;
                if(pageToken.equals(""))
                    downloadVariantSetCall = client.downloadVariantSetWithoutPageToken(token.getAccess_token(), variantSetId, 100000);
                else
                    downloadVariantSetCall = client.downloadVariantSetWithPageToken(token.getAccess_token(), variantSetId, 100000,pageToken);
                Response<Calls> downloadVariantSetResponse = downloadVariantSetCall.execute();
                if (downloadVariantSetResponse.isSuccessful()) {
                    Calls callResponseJSON = downloadVariantSetResponse.body();
                    if (callResponseJSON != null) {
                        if (callResponseJSON.getMetadata() != null & callResponseJSON.getMetadata().getPagination() != null) {
                            callResponseList.add(callResponseJSON);
                            pageToken = callResponseJSON.getMetadata().getPagination().getNextPageToken();
                        } else {
                            status = "Got null pagination information.\n could not get all the data.";
                        }
                    } else {
                        status = "Got null response.";
                    }
                } else {
                    RetrofitError errorMessage = new Gson().fromJson(downloadVariantSetResponse.errorBody().charStream(), RetrofitError.class);
                    status = returnExitStatus(downloadVariantSetResponse.code(), errorMessage.toString());
                }
            } while (pageToken != null & status.equalsIgnoreCase(Constants.SUCCESS));
            status = processCallSets(callResponseList, fileName);
        } catch (Exception e) {
            status = e.getMessage();
            logger.error(status);
        }
        logger.info("Submitting data download request completed ");
        return status;
    }
}