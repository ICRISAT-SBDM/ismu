package com.icrisat.sbdm.ismu.ui.mainFrame.project;

import com.icrisat.sbdm.ismu.ui.mainFrame.DynamicTree;
import com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.GenoPhenoPanel;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;

import javax.swing.*;
import java.awt.*;

public class GenoPhenoSelectionPanel extends JPanel {

    GenoPhenoPanel genoPanel, phenoPanel;

    GenoPhenoSelectionPanel(SharedInformation sharedInformation, DynamicTree dynamicTree) {
        setLayout(new GridBagLayout());
        setSize(new Dimension(400, 75));
        // -----------------------------Geno----------------------------------------
        genoPanel = new GenoPhenoPanel(sharedInformation, dynamicTree, Constants.GENO, false);
        phenoPanel = new GenoPhenoPanel(sharedInformation, dynamicTree, Constants.PHENO, false);

        Insets noInsets = new Insets(0, 0, 0, 0);
        int x = 0;
        int y = 0;
        add(genoPanel.panel, getGridBag(x, y, GridBagConstraints.FIRST_LINE_START, noInsets));

        y = y + 1;
        add(phenoPanel.panel, getGridBag(x, y, GridBagConstraints.LINE_START, noInsets));
    }

    private GridBagConstraints getGridBag(int x, int y, int anchor, Insets insets) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = x;
        gridBagConstraints.gridy = y;
        gridBagConstraints.anchor = anchor;
        gridBagConstraints.insets = insets;
        return gridBagConstraints;
    }

    public void setEnable(boolean value) {
        genoPanel.setEnabled(value);
        phenoPanel.setEnabled(value);
    }
}
