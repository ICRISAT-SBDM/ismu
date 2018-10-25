package com.icrisat.sbdm.ismu.retrofit.germinate;

import com.icrisat.sbdm.ismu.retrofit.ExtractResponse;
import com.icrisat.sbdm.ismu.retrofit.Studies;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface GerminateClient{

    @GET("brapi/v1/studies-search")
    Call<Studies> getStudies(@Query("studyType") String studyType);

    @GET("brapi/v1/markerprofiles")
    Call<MarkerProfiles> getMarkerProfiles(@Query("studyDbId") String studyDbId);

    @FormUrlEncoded
    @POST("brapi/v1/allelematrix-search")
    Call<ExtractResponse> getAlleleMatrix(@Field("markerprofileDbId") List<String> markerProfileDbIds,
                                          @Field("format") String format);

    @GET("brapi/v1/allelematrix-search/status/{jobId}")
    Call<ExtractResponse> getExtractStatus(@Path("jobId") String jobId);

    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithURL(@Url String url);
}
