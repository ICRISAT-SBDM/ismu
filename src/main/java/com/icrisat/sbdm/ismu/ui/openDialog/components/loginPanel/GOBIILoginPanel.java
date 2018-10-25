package com.icrisat.sbdm.ismu.ui.openDialog.components.loginPanel;

import com.icrisat.sbdm.ismu.retrofit.gobii.GOBIIRetrofitClient;
import com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.gobii.GOBIIDataSelectionPanel;
import com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.gobii.GOBIIDataSetTable;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import java.awt.event.ActionEvent;

public class GOBIILoginPanel extends LoginPanel {
    public GOBIILoginPanel(SharedInformation sharedInformation) {
        super(sharedInformation, "GOBII Login");
    }

    /**
     * Login button action for GOBII.
     *
     * @param e Action Event.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (validateUserFields()) {
            GOBIIRetrofitClient client = sharedInformation.getGobiiRetrofitClient();
            //TODO: Authentication when gobii supports it.
            setVisible(false);
            clearPasswordField();
            String status = client.authenticate(url, userName, password, sharedInformation.getLogger());
            if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
                Util.showMessageDialog(status);
            } else {
                new GOBIIDataSelectionPanel(sharedInformation, new GOBIIDataSetTable());
            }
        }
    }
}
