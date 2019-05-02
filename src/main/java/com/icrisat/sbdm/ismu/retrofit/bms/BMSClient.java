package com.icrisat.sbdm.ismu.retrofit.bms;

import com.icrisat.sbdm.ismu.retrofit.bms.SampleResponse.SampleData;
import com.icrisat.sbdm.ismu.retrofit.bms.TriatResponse.TriatData;
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
                           @Query("programDbId") String programDbId,
                           @Query("pageNumber") int pageNumber);

    @GET("/bmsapi/{crop}/brapi/v1/phenotypes-search")
    Call<TriatData> getTrialData(@Header("Authorization") String value,
                                 @Path("crop") String crop,
                                 @Body PhenotypesSearchTrialDbId phenotypesSearchTrialDbId);

    @POST("/bmsapi/{crop}/brapi/v1/phenotypes-search")
    Call<TriatData> getStudyData(@Header("Authorization") String value,
                                 @Path("crop") String crop,
                                 @Body PhenotypesSearchStudyDbId phenotypesSearchStudyDbId);

    @POST("/bmsapi/{crop}/brapi/v1/samples-search")
    Call<SampleData> getSampleIdsForStudy(@Header("Authorization") String value,
                                          @Path("crop") String crop,
                                          @Body SampleSearchStudyDbId sampleSearchStudyDbId);
}