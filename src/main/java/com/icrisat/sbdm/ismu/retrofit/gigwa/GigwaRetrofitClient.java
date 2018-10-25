package com.icrisat.sbdm.ismu.retrofit.gigwa;

import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import org.slf4j.Logger;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.List;

import static com.icrisat.sbdm.ismu.retrofit.RetrofitUtil.*;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class GigwaRetrofitClient {

    private GigwaClient client;
    private Logger logger;

    /**
     * Authenticate to Gigwa.
     * //TODO: currently this just creates client. Auth part will be added when required
     *
     * @param URL      URL for Gigwa service
     * @param userName UserName
     * @param password password
     * @param logger logger
     * @return status of the rest call.
     */
    public String authenticate(String URL, String userName, String password, Logger logger) {
        this.logger = logger;
        this.logger.info("Gigwa call details: " + URL + " " + userName + " " + password);
        String status = Constants.SUCCESS;
        try {
            client = GigwaRetrofitUtil.createClient(URL);
        } catch (Exception e) {
            status = e.getMessage();
        }
        return status;
    }

    public String getStudies(List<String[]> studiesList, SharedInformation sharedInformation) {
        return callStudies(client.getStudies(), studiesList, sharedInformation);
    }


}