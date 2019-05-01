package com.icrisat.sbdm.ismu.retrofit.bms;

import retrofit2.Call;
import retrofit2.http.*;

public interface BMSClient {

    // Endpoint. This gets appeneded to the baseURL
    @POST("/bmsapi/brapi/v1/token")
    Call<Token> authUser(@Body User user);

    @GET("/bmsapi/brapi/v1/crops")
    Call<Crops> getCrops(@Header("Authorization") String value);

    @GET("/bmsapi/{crop}/brapi/v1/trials ")
    Call<Trials> getTrials(@Header("Authorization") String value,
                           @Path("crop") String crop,
                           @Query("pageNumber") int pageNumber);

    @GET("/bmsapi/{crop}/brapi/v1/trials/{trialDbId}/table")
    Call<Trial_Study_DBData> getTrialData(@Header("Authorization") String value,
                                          @Path("crop") String crop,
                                          @Path("trialDbId") String trialDbId);

    @GET("/bmsapi/{crop}/brapi/v1/studies/{studyDbId}/table")
    Call<Trial_Study_DBData> getStudyData(@Header("Authorization") String value,
                                          @Path("crop") String crop,
                                          @Path("studyDbId") String studyDbId);
}