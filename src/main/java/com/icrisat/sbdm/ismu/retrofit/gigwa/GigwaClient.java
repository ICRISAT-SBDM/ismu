package com.icrisat.sbdm.ismu.retrofit.gigwa;

import com.icrisat.sbdm.ismu.retrofit.Studies;
import retrofit2.Call;
import retrofit2.http.*;

public interface GigwaClient{

    @GET("brapi/v1/studies-search")
    Call<Studies> getStudies();
}
