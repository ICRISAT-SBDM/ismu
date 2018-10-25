package com.icrisat.sbdm.ismu.ui.openDialog.components.loginPanel;

import com.icrisat.sbdm.ismu.retrofit.gigwa.GigwaRetrofitClient;
import com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.gigwa.GigwaDataSelectionPanel;
import com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.gigwa.GigwaDataSetTable;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import java.awt.event.ActionEvent;

public class GigwaLoginPanel extends LoginPanel {
    public GigwaLoginPanel(SharedInformation sharedInformation) {
        super(sharedInformation, "Gigwa Login");
    }

    /**
     * Login button action for Germinate.
     *
     * @param e Action Event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (validateUserFields()) {
            GigwaRetrofitClient client = sharedInformation.getGigwaRetrofitClient();
            //TODO: Authentication support later.
            setVisible(false);
            clearPasswordField();
            String status = client.authenticate(url, userName, password, sharedInformation.getLogger());
            if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
                Util.showMessageDialog(status);
            } else {
              new GigwaDataSelectionPanel(sharedInformation, new GigwaDataSetTable());
            }
        }
    }
}
