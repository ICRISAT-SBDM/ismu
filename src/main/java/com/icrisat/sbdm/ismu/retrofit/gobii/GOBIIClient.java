package com.icrisat.sbdm.ismu.retrofit.gobii;

import retrofit2.Call;
import retrofit2.http.*;

public interface GOBIIClient {

    @POST("brapi/v2/token")
    Call<Token> authUser(@Body User user);

    @GET("brapi/v2/variantsets")
    Call<Variantsets> getVariantSets(@Header("X-Auth-Token") String token);

    @GET("brapi/v2/variantsets/{variantSetId}/calls")
    Call<Calls> downloadVariantSetWithPageToken(@Header("X-Auth-Token") String token,
                                   @Path("variantSetId") int variantSetId,
                                   @Query("pageSize") int pageSize,
                                   @Query("pageToken") String pageToken);

    @GET("brapi/v2/variantsets/{variantSetId}/calls")
    Call<Calls> downloadVariantSetWithoutPageToken(@Header("X-Auth-Token") String token,
                                                @Path("variantSetId") int variantSetId,
                                                @Query("pageSize") int pageSize);
}
