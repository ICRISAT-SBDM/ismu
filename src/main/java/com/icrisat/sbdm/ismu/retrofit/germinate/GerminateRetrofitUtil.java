package com.icrisat.sbdm.ismu.retrofit.germinate;

import com.opencsv.CSVWriter;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.*;

public class GerminateRetrofitUtil {
    /**
     * Creates a retrofit client.
     *
     * @param URL Germinate URL
     * @return retrofit client.
     */
    static GerminateClient createClient(String URL) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        return retrofit.create(GerminateClient.class);
    }

    public static boolean writeResponseBodyToDisk(ResponseBody body, String outputFileName) {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(body.byteStream()));
             CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFileName))) {

            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] split = line.split("\t");
                csvWriter.writeNext(split);

            }
            csvWriter.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
