package com.icrisat.sbdm.ismu.retrofit.gobii;

import com.icrisat.sbdm.ismu.retrofit.ExtractResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface GOBIIClient {

    @POST("gobii/v1/auth")
    Call<Token> authUser(@Body String body,
                         @Header("X-Username") String userName,
                         @Header("X-Password") String password);

    @GET("brapi/v1/variantsets")
    Call<Variantsets> getVariantSets(@Header("X-Auth-Token") String token);

    @GET("brapi/v1/variantsets/{variantSetId}/calls")
    Call<Calls> downloadVariantSet(@Header("X-Auth-Token") String token,
                                   @Path("variantSetId") int variantSetId,
                                   @Query("pageSize") int pageSize);

    @GET("brapi/v1/allelematrix-search")
    Call<ExtractResponse> extractByExternalCodes(@Header("X-Auth-Token") String token,
                                                 @Query("markerprofileDbId") String markerprofileDbId);

    @GET("brapi/v1/allelematrix-search/status/{jobId}")
    Call<ExtractResponse> getExtractStatus(@Header("X-Auth-Token") String token,
                                           @Path("jobId") String jobId);

    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithURL(@Header("X-Auth-Token") String token,
                                           @Url String url);
}
