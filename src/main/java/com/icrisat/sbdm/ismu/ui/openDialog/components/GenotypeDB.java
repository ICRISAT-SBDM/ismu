package com.icrisat.sbdm.ismu.ui.openDialog.components;

import com.icrisat.sbdm.ismu.ui.openDialog.components.loginPanel.GOBIILoginPanel;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

@Component
public class GenotypeDB {
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
     * Creates the genotype database dialog box..
     */
    public void createGenotypeDB() {
        dialogBox = new JDialog(sharedInformation.getMainFrame(), "Connect to Genotype databases", Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setSize(new Dimension(500, 100));
        dialogBox.setLocation(Util.getLocation(500, 100));
        dialogBox.setLocationRelativeTo(sharedInformation.getMainFrame());
        sharedInformation.setGenotypeURLPanel(dialogBox);
        URLPanel gobiiURLPanel = new URLPanel(sharedInformation, "GOBII", new GOBIILoginPanel(sharedInformation));
        //   URLPanel germinateURLPanel = new URLPanel(sharedInformation, "GERMINATE", new GerminateLoginPanel(sharedInformation));
        //   URLPanel gigwaURLPanel = new URLPanel(sharedInformation, "GIGWA", new GigwaLoginPanel(sharedInformation));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(gobiiURLPanel);
        //   mainPanel.add(germinateURLPanel);
        //   mainPanel.add(gigwaURLPanel);
        dialogBox.add(mainPanel);
    }
}
