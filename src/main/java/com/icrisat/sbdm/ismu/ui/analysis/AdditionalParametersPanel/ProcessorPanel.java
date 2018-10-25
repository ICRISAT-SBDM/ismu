package com.icrisat.sbdm.ismu.ui.analysis.AdditionalParametersPanel;

import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;

/**
 * Processor panel. Sub-panel for Analysis.
 */
@Component
public class ProcessorPanel extends JPanel {

    private JSpinner coresSpinner;

    public JSpinner getCoresSpinner() {
        return coresSpinner;
    }

    @Autowired
    public ProcessorPanel(SharedInformation sharedInformation) {
        int width = 80, height = 30;
        setBorder(Util.getCompoundBorder("Processor", sharedInformation));
        setLayout(null);

        JLabel rounds = Util.createJLabel("Cores", sharedInformation);
        rounds.setBounds(20, 40, width, height);
        add(rounds);

        coresSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 4, 1));
        coresSpinner.setBounds(120, 40, width, height);
        add(coresSpinner);
    }

    public void resetValue(){
        coresSpinner.getModel().setValue(2);
    }
}
