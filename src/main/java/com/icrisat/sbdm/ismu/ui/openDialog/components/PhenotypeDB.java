package com.icrisat.sbdm.ismu.ui.openDialog.components;

import java.awt.*;
import javax.swing.*;

import com.icrisat.sbdm.ismu.ui.openDialog.components.loginPanel.BMSLoginPanel;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        dialogBox = new JDialog(sharedInformation.getMainFrame(), "Connect to Phenotype databases", Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setSize(new Dimension(500, 120));
        dialogBox.setLocation(Util.getLocation(500, 120));
        dialogBox.setLocationRelativeTo(sharedInformation.getMainFrame());
        sharedInformation.setPhenotypeURLPanel(dialogBox);
        URLPanel bmsURLPanel = new URLPanel(sharedInformation, "BMS", new BMSLoginPanel(sharedInformation));
        //b4rURLPanel = new URLPanel(parent,"B4R", new B4rLoginPanel(parent));
        dialogBox.setLayout(new FlowLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(bmsURLPanel);
        dialogBox.add(mainPanel);
    }
}
