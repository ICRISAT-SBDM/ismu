package com.icrisat.sbdm.ismu.ui.analysis.AdditionalParametersPanel;

import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * Cross-validation panel. Sub-panel for Analysis.
 */
@Component
public class CrossValidationPanel extends JPanel {

    private JSpinner replicationSpinner, foldSpinner;

    public JSpinner getReplicationSpinner() {
        return replicationSpinner;
    }

    public JSpinner getFoldSpinner() {
        return foldSpinner;
    }

    @Autowired
    public CrossValidationPanel(SharedInformation sharedInformation) {
        int width = 80, height = 30;
        setBorder(Util.getCompoundBorder("Cross validation", sharedInformation));
        setLayout(null);

        JLabel rounds = Util.createJLabel("Replication", sharedInformation);
        rounds.setBounds(20, 40, width, height);
        add(rounds);

        replicationSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        replicationSpinner.setBounds(120, 40, width, height);
        add(replicationSpinner);

        JLabel fold = Util.createJLabel("Fold", sharedInformation);
        fold.setBounds(20, 80, width, height);
        add(fold);

        foldSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        foldSpinner.setBounds(120, 80, width, height);
        add(foldSpinner);
    }

    public void resetValue() {
        replicationSpinner.getModel().setValue(1);
        foldSpinner.getModel().setValue(1);
    }
}
