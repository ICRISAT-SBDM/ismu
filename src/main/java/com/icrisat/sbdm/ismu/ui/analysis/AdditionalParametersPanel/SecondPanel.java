package com.icrisat.sbdm.ismu.ui.analysis.AdditionalParametersPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

/**
 * Second panel. Sub-panel for Analysis.
 * It has two sub-panels, random forest and processor.
 */
@Component
public class SecondPanel extends JPanel {

    private RandomForestPanel randomForestPanel;
    private ProcessorPanel processorPanel;

    public RandomForestPanel getRandomForestPanel() {
        return randomForestPanel;
    }

    public ProcessorPanel getProcessorPanel() {
        return processorPanel;
    }

    @Autowired
    public SecondPanel(RandomForestPanel randomForestPanel, ProcessorPanel processorPanel) {
        this.randomForestPanel = randomForestPanel;
        this.processorPanel = processorPanel;
        setSize(500, 180);
        setLayout(new GridLayout(2, 1));
        add(randomForestPanel);
        add(processorPanel);
    }
}
