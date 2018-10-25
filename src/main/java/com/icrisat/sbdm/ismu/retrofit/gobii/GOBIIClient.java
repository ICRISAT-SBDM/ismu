package com.icrisat.sbdm.ismu.retrofit.gobii;

import com.icrisat.sbdm.ismu.retrofit.ExtractResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface GOBIIClient{
    @GET("brapi/v1/allelematrices")
    Call<AlleleMatrices> getDataSets();

    @GET("brapi/v1/allelematrix-search")
    Call<ExtractResponse> extractDataSet(@Query("matrixDbId") String matrixDbId);

    @GET("brapi/v1/allelematrix-search/status/{jobId}")
    Call<ExtractResponse> getExtractStatus(@Path("jobId") String jobId);

    @Streaming
    @GET
    Call<ResponseBody> downloadFileWithURL(@Url String url);
}
