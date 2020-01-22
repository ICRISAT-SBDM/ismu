package com.icrisat.sbdm.ismu.ui.openDialog.components;

import com.icrisat.sbdm.ismu.ui.openDialog.components.connectionPanel.ConnectionPanel;
import com.icrisat.sbdm.ismu.ui.openDialog.components.connectionPanel.LogoFooterPanel;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * Creating GUI dialog box to connect to available pheno type data bases.
 *
 * @author Chaitanya
 */
@Component
public class PhenotypeDB {

    private SharedInformation sharedInformation;
    private JDialog dialogBox;

    @Autowired
    public void setSharedInformation(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
    }

    public void setVisible(boolean visible) {
        dialogBox.setVisible(visible);
    }

    /**
     * Creates the phenotype database dialog box..
     */
    public void createPhenotypeDB() {
        dialogBox = new JDialog(sharedInformation.getMainFrame(), "Connect to BMS", Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setSize(new Dimension(400, 250));
        dialogBox.setLocation(Util.getLocation(400, 250));
        dialogBox.setLocationRelativeTo(sharedInformation.getMainFrame());
        dialogBox.setLayout(new BorderLayout());
        dialogBox.add(new ConnectionPanel(sharedInformation, Constants.BMS,dialogBox), BorderLayout.CENTER);
        dialogBox.add(new LogoFooterPanel(sharedInformation, Constants.BMS), BorderLayout.SOUTH);
    }
}
