package com.icrisat.sbdm.ismu.ui.openDialog.components.loginPanel;

import java.awt.event.ActionEvent;

import com.icrisat.sbdm.ismu.ui.openDialog.components.phenotype.bms.BMSDataSelectionPanel;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.retrofit.bms.BMSRetrofitClient;
import com.icrisat.sbdm.ismu.util.Util;

public class BMSLoginPanel extends LoginPanel {

    public BMSLoginPanel(SharedInformation sharedInformation) {
        super(sharedInformation, "BMS Login");
    }

    /**
     * Login button action for BMS.
     *
     * @param e Action Event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (validateUserFields()) {
            BMSRetrofitClient client = sharedInformation.getBmsRetrofitClient();
            String status = client.authenticate(url, userName, password);
            // Retry logic.
            if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
                status = client.authenticate(url, userName, password);
            }
            clearPasswordField();
            if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
                if (status.equalsIgnoreCase(Constants.URL_ISSUE))
                    setVisible("", false);
                Util.showMessageDialog(status);
            } else {
                setVisible(false);
                sharedInformation.getPhenotypeURLPanel().setVisible(false);
                BMSDataSelectionPanel bmsDataSelectionPanel = new BMSDataSelectionPanel(sharedInformation);
                bmsDataSelectionPanel.setVisible(true);
            }
        }
    }
}
