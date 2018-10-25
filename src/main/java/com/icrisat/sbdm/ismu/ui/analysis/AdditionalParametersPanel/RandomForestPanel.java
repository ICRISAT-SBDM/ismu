package com.icrisat.sbdm.ismu.ui.analysis.AdditionalParametersPanel;

import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * Random forest panel. Sub-panel for Analysis.
 */
@Component
public class RandomForestPanel extends JPanel {

    private JSpinner forestsSpinner;

    public JSpinner getForestsSpinner() {
        return forestsSpinner;
    }

    @Autowired
    public RandomForestPanel(SharedInformation sharedInformation) {
        int width = 80, height = 30;
        setBorder(Util.getCompoundBorder("Random Forest", sharedInformation));
        setLayout(null);

        JLabel rounds = Util.createJLabel("Forests", sharedInformation);
        rounds.setBounds(20, 40, width, height);
        add(rounds);

        forestsSpinner = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));
        forestsSpinner.setBounds(120, 40, width, height);
        add(forestsSpinner);
    }

    public void enableComponent(boolean value) {
        forestsSpinner.getModel().setValue(100);
        forestsSpinner.setEnabled(value);
    }
}
