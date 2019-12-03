package com.icrisat.sbdm.ismu.retrofit;

import com.google.gson.Gson;
import com.icrisat.sbdm.ismu.retrofit.germinate.GerminateRetrofitUtil;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class RetrofitUtil {
    /**
     * Sets the error message and logs it to the logger.
     *
     * @param code  Error-code
     * @param error Error message. Body of retrofit error.
     * @return Status msg for the error.
     */
    public static String returnExitStatus(int code, String error) {
        String status;
        switch (code) {
            case 404:
                status = error;
                break;
            //   case 401:
            case 500:
                status = error;
                break;
            default:
                status = code + error;
                break;
        }
        return status;
    }

    /**
     * Get data files.
     */
    private static String getDataFiles(List<String> response, Response<ExtractResponse> extractDataSetResponse) {
        String status = Constants.SUCCESS;
        ExtractResponse extractDataSet = extractDataSetResponse.body();

        // Status is set to success if extraction is in progress or completed.
        if ((extractDataSet != null)
                && extractDataSet.getMetadata() != null
                && extractDataSet.getMetadata().getStatus() != null
                && extractDataSet.getMetadata().getDatafiles() != null) {
            String extractStatus = extractDataSet.getMetadata().getStatus()[0].getMessage();
            if (extractStatus.equalsIgnoreCase("FINISHED")) {
                List<String> datafiles = extractDataSet.getMetadata().getDatafiles();
                response.add(datafiles.get(0));
            } else if (extractStatus.equalsIgnoreCase("failed") || extractStatus.equals("exception")) {
                status = extractStatus;
            }
        } else {
            status = "Got null response.";
        }
        return status;
    }

    public static String extractDataset(List<String> response, Call<ExtractResponse> getExtractStatus) {
        String status;
        try {
            Response<ExtractResponse> extractDataSetResponse = getExtractStatus.execute();
            if (extractDataSetResponse.isSuccessful()) {
                status = getDataFiles(response, extractDataSetResponse);
            } else {
                RetrofitError errorMessage = new Gson().fromJson(extractDataSetResponse.errorBody().charStream(), RetrofitError.class);
                status = returnExitStatus(extractDataSetResponse.code(), errorMessage.toString());
            }
        } catch (IOException e) {
            status = e.getMessage();
        }
        return status;
    }

    public static String downloadData(String fileName, Call<ResponseBody> call, String type) {
        String status = Constants.SUCCESS;
        try {
            Response<ResponseBody> downloadDataSetResponse = call.execute();
            if (downloadDataSetResponse.isSuccessful()) {
                switch (type) {
                    case Constants.GERMINATE:
                        if (!GerminateRetrofitUtil.writeResponseBodyToDisk(downloadDataSetResponse.body(), fileName)) {
                            status = "Error in writing file to disk. Please check log file for details.";
                        }
                        break;
                }
            } else {
                RetrofitError errorMessage = new Gson().fromJson(downloadDataSetResponse.errorBody().charStream(), RetrofitError.class);
                status = returnExitStatus(downloadDataSetResponse.code(), errorMessage.toString());
            }
        } catch (IOException e) {
            status = e.getMessage();
        }
        return status;
    }

    public static String checkForServerError(InputStream studiesResponse) throws IOException {
        String status = Constants.SUCCESS;
        BufferedReader br = new BufferedReader(new InputStreamReader(studiesResponse));
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("<html>")) {
                status = "Not supported. Check your URL";
            }
        }
        return status;
    }

    private static void processStudies(List<Studies.Data> studies, List<String[]> studiesList) {
        for (Studies.Data study : studies) {
            String[] data = new String[3];
            data[0] = study.getName();
            data[1] = study.getStudyDbId();
            data[2] = study.getProgramName();
            studiesList.add(data);
        }
    }

    public static String callStudies(Call<Studies> studies, List<String[]> studiesList, SharedInformation sharedInformation) {
        String status = Constants.SUCCESS;
        try {
            Response<Studies> studiesResponse = studies.execute();
            if (studiesResponse.isSuccessful()) {
                Studies studiesJSON = studiesResponse.body();
                if (studiesJSON != null)
                    processStudies(studiesJSON.getResult().getData(), studiesList);
                else
                    status = "Could not receive any studies.";
            } else {
                String serverStatus = checkForServerError(studiesResponse.errorBody().byteStream());
                if (!serverStatus.equalsIgnoreCase(Constants.SUCCESS)) return serverStatus;
                RetrofitError errorMessage = new Gson().fromJson(studiesResponse.errorBody().charStream(), RetrofitError.class);
                status = returnExitStatus(studiesResponse.code(), errorMessage.toString());
            }
        } catch (Exception e) {
            sharedInformation.getLogger().error(status + "\t" + e.getMessage());
            status = e.getMessage();
        }
        return status;
    }
}
