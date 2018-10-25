package com.icrisat.sbdm.ismu.ui.openDialog.components.loginPanel;

import com.icrisat.sbdm.ismu.retrofit.germinate.GerminateRetrofitClient;
import com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.germinate.GerminateDataSelectionPanel;
import com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.germinate.GerminateDataSetTable;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import java.awt.event.ActionEvent;

public class GerminateLoginPanel extends LoginPanel {
    public GerminateLoginPanel(SharedInformation sharedInformation) {
        super(sharedInformation, "Germinate Login");
    }

    /**
     * Login button action for Germinate.
     *
     * @param e Action Event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (validateUserFields()) {
            GerminateRetrofitClient client = sharedInformation.getGerminateRetrofitClient();
            //TODO: Authentication support later.
            setVisible(false);
            clearPasswordField();
            String status = client.authenticate(url, userName, password, sharedInformation.getLogger());
            if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
                Util.showMessageDialog(status);
            } else {
                new GerminateDataSelectionPanel(sharedInformation, new GerminateDataSetTable());
            }
        }
    }
}
