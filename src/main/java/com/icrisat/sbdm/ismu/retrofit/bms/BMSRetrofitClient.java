package com.icrisat.sbdm.ismu.retrofit.bms;

import com.google.gson.Gson;
import com.icrisat.sbdm.ismu.retrofit.RetrofitError;
import com.icrisat.sbdm.ismu.retrofit.bms.SampleResponse.Data;
import com.icrisat.sbdm.ismu.retrofit.bms.SampleResponse.SampleData;
import com.icrisat.sbdm.ismu.retrofit.bms.TriatResponse.TriatData;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;
import java.util.*;

import static com.icrisat.sbdm.ismu.retrofit.RetrofitUtil.returnExitStatus;
import static com.icrisat.sbdm.ismu.retrofit.bms.BMSRetrofitUtil.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class BMSRetrofitClient {

    private SharedInformation sharedInformation;
    private BMSClient client;
    private Token token;
    private Logger logger;
    private static final String BEARER = "Bearer ";// Space required

    public void setSharedInformation(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
    }

    /**
     * Authenticate to BMS.
     *
     * @param URL      URL for BMS service
     * @param userName UserName
     * @param password password
     * @return status of the rest call.
     */
    public String authenticate(String URL, String userName, String password) {
        logger = sharedInformation.getLogger();
        String status = Constants.SUCCESS;
        try {
            client = createClient(URL);
        } catch (Exception e) {
            status = e.getMessage();
            return status;
        }
        Call<Token> authUser = client.authUser(new User(userName, password, "", ""));
        try {
            Response<Token> response = authUser.execute();
            // Retrofit by default considers any response as successful.
            // isSuccessful() checks if the response is of type 2xx or 3xx;
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
     * Get list of crops from BMS.
     *
     * @param cropsList List of crops.
     * @return Status of rest call.
     */
    public String getCrops(List<String> cropsList) {
        String status = Constants.SUCCESS;
        String authToken = BEARER + token.getAccess_token();
        Call<Crops> getCrops = client.getCrops(authToken);
        try {
            Response<Crops> cropsResponse = getCrops.execute();
            if (cropsResponse.isSuccessful()) {
                Crops cropsJSON = cropsResponse.body();
                List<String> data = cropsJSON.getResult().getData();
                cropsList.addAll(data);
            } else {
                status = returnExitStatus(cropsResponse.code(), String.valueOf(cropsResponse.errorBody()));
            }
        } catch (IOException e) {
            status = Constants.NO_INTERNET;
            logger.error(status + "\t" + e.getMessage());
        }
        return status;
    }

    /**
     * @param selectedCrop Selected Crop.
     * @param trialList    List of trials.
     * @return Success status.
     */
    public String getTrials(String selectedCrop, List<String[]> trialList) {
        String authToken = BEARER + token.getAccess_token();
        int pageNo = 0;
        Status status = getTrialForPage(selectedCrop, trialList, authToken, pageNo);
        pageNo++;
        while ((status.getStatus().equalsIgnoreCase(Constants.SUCCESS)) && (pageNo < status.getPageNo())) {
            status = getTrialForPage(selectedCrop, trialList, authToken, pageNo);
            pageNo++;
        }
        return status.getStatus();
    }

    private Status getTrialForPage(String selectedCrop, List<String[]> trialList, String authToken, int pageNo) {
        Status status = new Status();
        Call<Trials> getTrials = client.getTrials(authToken, selectedCrop, pageNo);
        try {
            logger.info("Getting trials for " + selectedCrop + " for pageNo " + pageNo);
            Response<Trials> trialResponse = getTrials.execute();
            if (trialResponse.isSuccessful()) {
                Trials trialJSON = trialResponse.body();
                if (trialJSON != null) {
                    //              status.setPageNo(trialJSON.getMetadata().getPagination().getTotalPages());
                    processTrialData(trialJSON, selectedCrop, trialList);
                } else {
                    status.setStatus("Could not get the data from BMS. Please try after sometime");
                }
            } else {
                RetrofitError errorMessage = new Gson().fromJson(trialResponse.errorBody().charStream(), RetrofitError.class);
                status.setStatus(returnExitStatus(trialResponse.code(), errorMessage.toString()));
            }
        } catch (Exception e) {
            status.setStatus(e.getMessage());
        }
        return status;
    }

    /**
     * Make a rest call to get trial or study data.
     *
     * @param selectedData selected data row.
     * @return success status
     */
    public String getData(List selectedData) {

        String status;
        String authToken = BEARER + token.getAccess_token();
        String crop = (String) selectedData.get(0);
        boolean isTrial = selectedData.get(6) == "";
        List<com.icrisat.sbdm.ismu.retrofit.bms.TriatResponse.Data> traitData = new ArrayList<>();
        if (isTrial) {
            String trialDbId = (String) selectedData.get(5);
            PhenotypesSearchTrialDbId phenotypesSearchTrialDbId = new PhenotypesSearchTrialDbId();
            phenotypesSearchTrialDbId.setTrialDbIds(new ArrayList(Collections.singleton(trialDbId)));
            Call<TriatData> getData = client.getTrialData(authToken, crop, phenotypesSearchTrialDbId);
            status = downloadTraitData(getData, traitData);
            if (!status.equalsIgnoreCase(Constants.SUCCESS)) return status;
        } else {
            String studyDbId = (String) selectedData.get(6);
            PhenotypesSearchStudyDbId phenotypesSearchStudyDbId = new PhenotypesSearchStudyDbId();
            phenotypesSearchStudyDbId.setStudyDbIds(new ArrayList(Collections.singleton(studyDbId)));
            Call<TriatData> getData = client.getStudyData(authToken, crop, phenotypesSearchStudyDbId);
            status = downloadTraitData(getData, traitData);
            if (!status.equalsIgnoreCase(Constants.SUCCESS)) return status;
        }
        status = writeTrialDataToFile(traitData, sharedInformation);
        System.out.println();
        return status;
    }

    /**
     * Downloads trial data from BMS.
     *
     * @param getData retrofit client to get the data.
     * @return success status.
     */
    private String downloadTraitData(Call<TriatData> getData, List<com.icrisat.sbdm.ismu.retrofit.bms.TriatResponse.Data> traitData) {
        String status = "";
        int retryCount = 0;
        while (!status.equalsIgnoreCase(Constants.SUCCESS) && retryCount < 4) {
            try {
                Response<TriatData> data = getData.clone().execute();
                if (data.isSuccessful()) {
                    traitData.addAll(data.body().getData());
                    status = Constants.SUCCESS;
                } else {
                    RetrofitError errorMessage = new Gson().fromJson(data.errorBody().charStream(), RetrofitError.class);
                    status = returnExitStatus(data.code(), errorMessage.toString());
                }
            } catch (Exception e) {
                status = e.getMessage();
                logger.error(e.getMessage());
            }
            retryCount++;
        }
        return status;
    }

    /**
     * Downloads trial data from BMS.
     *
     * @param getData retrofit client to get the data.
     * @return success status.
     */
    private String downloadSampleData(Call<SampleData> getData, Map<String, String> sampleData) {
        String status = "";
        int retryCount = 0;
        while (!status.equalsIgnoreCase(Constants.SUCCESS) && retryCount < 4) {
            try {
                Response<SampleData> data = getData.clone().execute();
                if (data.isSuccessful()) {
                    List<Data> dataList = data.body().getData();
                    for (Data data1 : dataList) {
                        sampleData.put(data1.getSampleDbId(), data1.getObservationUnitDbId());
                    }
                    status = Constants.SUCCESS;
                } else {
                    RetrofitError errorMessage = new Gson().fromJson(data.errorBody().charStream(), RetrofitError.class);
                    status = returnExitStatus(data.code(), errorMessage.toString());
                }
            } catch (Exception e) {
                status = e.getMessage();
                logger.error(e.getMessage());
            }
            retryCount++;
        }
        return status;
    }

    class Status {
        private int pageNo;
        private String status;

        Status() {
            pageNo = 1;
            status = Constants.SUCCESS;
        }

        int getPageNo() {
            return pageNo;
        }

        String getStatus() {
            return status;
        }

        void setPageNo(int pageNo) {
            this.pageNo = pageNo;
        }

        void setStatus(String status) {
            this.status = status;
        }
    }
}