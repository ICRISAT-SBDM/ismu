package com.icrisat.sbdm.ismu.retrofit.gigwa;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GigwaRetrofitUtil {
    /**
     * Creates a retrofit client.
     *
     * @param URL Germinate URL
     * @return retrofit client.
     */
    static GigwaClient createClient(String URL) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        return retrofit.create(GigwaClient.class);
    }
}
