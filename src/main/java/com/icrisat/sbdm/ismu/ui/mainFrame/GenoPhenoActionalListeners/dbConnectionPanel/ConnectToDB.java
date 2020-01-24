package com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel;

import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.swing.*;
import java.awt.*;

public class ConnectToDB {
    private SharedInformation sharedInformation;
    private JDialog dialogBox;

    public void setVisible(boolean visible) {
        dialogBox.setVisible(visible);
    }


    public ConnectToDB(SharedInformation sharedInformation, String type) {
        this.sharedInformation = sharedInformation;
        if (type.equalsIgnoreCase(Constants.GOBII))
            dialogBox = new JDialog(sharedInformation.getMainFrame(), "Connect to GOBii", Dialog.ModalityType.APPLICATION_MODAL);
        else
            dialogBox = new JDialog(sharedInformation.getMainFrame(), "Connect to BMS", Dialog.ModalityType.APPLICATION_MODAL);

        dialogBox.setSize(new Dimension(400, 250));
        dialogBox.setLocation(Util.getLocation(400, 250));
        dialogBox.setLocationRelativeTo(sharedInformation.getMainFrame());
        dialogBox.setLayout(new BorderLayout());
        if (type.equalsIgnoreCase(Constants.GOBII)) {
            dialogBox.add(new ConnectionPanel(sharedInformation, Constants.GOBII, dialogBox), BorderLayout.CENTER);
            dialogBox.add(new LogoFooterPanel(sharedInformation, Constants.GOBII), BorderLayout.SOUTH);
        } else {
            dialogBox.add(new ConnectionPanel(sharedInformation, Constants.BMS, dialogBox), BorderLayout.CENTER);
            dialogBox.add(new LogoFooterPanel(sharedInformation, Constants.BMS), BorderLayout.SOUTH);
        }
    }
}
