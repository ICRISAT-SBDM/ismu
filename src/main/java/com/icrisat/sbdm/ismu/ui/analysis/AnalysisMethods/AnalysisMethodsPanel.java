package com.icrisat.sbdm.ismu.ui.analysis.AnalysisMethods;

import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.swing.*;
import java.awt.*;

public class AnalysisMethodsPanel extends JPanel {

    private JTabbedPane methodPane;

    public JTabbedPane getMethodPane() {
        return methodPane;
    }

    public AnalysisMethodsPanel(SharedInformation sharedInformation, RMethodsPanel rMethodsPanel, FortranMethodsPanel fortranMethodsPanel) {
        setBorder(Util.getCompoundBorder("Select method(s) to start analysis", sharedInformation));
        setSize(600, 150);
        setLayout(new GridLayout(1, 1));
        methodPane = new JTabbedPane();
        methodPane.addTab("R", rMethodsPanel);
        methodPane.addTab("Fortran", fortranMethodsPanel);
        add(methodPane);
        methodPane.addChangeListener(changeEvent -> {
            JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
            int index = sourceTabbedPane.getSelectedIndex();
            // R selected
            if (index == 0) {
                FortranMethodsPanel fortranMethodsPanel1 = (FortranMethodsPanel) methodPane.getComponentAt(1);
                fortranMethodsPanel1.setSelected(false);
            } else {
                RMethodsPanel rMethodsPanel1 = (RMethodsPanel) methodPane.getComponentAt(0);
                rMethodsPanel1.setSelected(false);
            }
        });
    }
}
