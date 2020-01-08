package com.icrisat.sbdm.ismu.ui.analysis;

import com.icrisat.sbdm.ismu.ui.WaitLayerUI;
import com.icrisat.sbdm.ismu.util.Util;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

@Component
public class AnalysisProgress {

    private Analysis analysis;
    private JDialog dialog;
    JLabel triatName, methodName;
    JLabel ridgeRegressionStatus, kinshipGaussStatus, bayesBStatus, bayesCPIStatus, bayesLassoStatus, randomForestStatus;
    final private int height = 20;
    private WaitLayerUI layerUI = new WaitLayerUI();

    public void createDialog(Frame frame, Font font, List<Integer> methodsSelected, Analysis analysis) {
        dialog = new JDialog(frame, Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setSize(500, 300);
        dialog.setLocation(Util.getLocation(500, 250));
        dialog.setTitle("Data Analysis Progress");
        dialog.setResizable(false);
        createAndAddComponents(font, methodsSelected, analysis);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                actionCancel();
            }

            public void windowClosing(WindowEvent e) {
                actionCancel();
            }
        });
    }

    public void display() {
        layerUI.start();
        dialog.setVisible(true);
    }

    /**
     * When cancel is clicked.
     */
    public void actionCancel() {
        layerUI.stop();
        analysis.setIsAnalysisCancelled(true);
        dialog.setVisible(false);
    }

    public void clearFields() {
        triatName.setText("");
        methodName.setText("");
        ridgeRegressionStatus.setText("");
        kinshipGaussStatus.setText("");
        bayesBStatus.setText("");
        bayesCPIStatus.setText("");
        bayesLassoStatus.setText("");
        randomForestStatus.setText("");
    }

    /**
     * Creates the components and adds them to panel which intern is added to dialog.
     *
     * @param font            font
     * @param methodsSelected list of methods indicating whether selected or not.
     */
    private void createAndAddComponents(Font font, List<Integer> methodsSelected, Analysis analysis) {

        this.analysis = analysis;
        JPanel dataPanel = new JPanel();
        dataPanel.setSize(500, 300);
        dataPanel.setLayout(null);

        JLabel triatInProgress = createLabel(font, "Triat in progress   :");
        triatInProgress.setBounds(50, 10, 150, height);

        triatName = createLabel(font, "");
        triatName.setBounds(200, 10, 250, height);

        JLabel processingMethod = createLabel(font, "Running method   :");
        processingMethod.setBounds(50, 40, 150, height);

        methodName = createLabel(font, "");
        methodName.setBounds(200, 40, 250, height);


        JLabel ridgeRegression = createLabel(font, "Ridge Regression");
        ridgeRegressionStatus = createLabel(font, "");

        JLabel kinshipGauss = createLabel(font, "Kinship Guass");
        kinshipGaussStatus = createLabel(font, "");

        JLabel bayesB = createLabel(font, "Bayes B");
        bayesBStatus = createLabel(font, "");

        JLabel bayesCPI = createLabel(font, "Bayes CPI");
        bayesCPIStatus = createLabel(font, "");

        JLabel bayesLasso = createLabel(font, "Bayes Lasso");
        bayesLassoStatus = createLabel(font, "");

        JLabel randomForest = createLabel(font, "Random Forest");
        randomForestStatus = createLabel(font, "");


        dataPanel.add(triatInProgress);
        dataPanel.add(triatName);
        dataPanel.add(processingMethod);
        dataPanel.add(methodName);

        int xCoordinate = 100, yCoordinate = 80;

        if (methodsSelected.get(0) == 1) {
            addMethod(dataPanel, ridgeRegression, ridgeRegressionStatus, xCoordinate, yCoordinate);
            yCoordinate = yCoordinate + 30;
        }
        if (methodsSelected.get(1) == 1) {
            addMethod(dataPanel, kinshipGauss, kinshipGaussStatus, xCoordinate, yCoordinate);
            yCoordinate = yCoordinate + 30;
        }

        if (methodsSelected.get(2) == 1) {
            addMethod(dataPanel, bayesB, bayesBStatus, xCoordinate, yCoordinate);
            yCoordinate = yCoordinate + 30;
        }
        if (methodsSelected.get(3) == 1) {
            addMethod(dataPanel, bayesCPI, bayesCPIStatus, xCoordinate, yCoordinate);
            yCoordinate = yCoordinate + 30;
        }

        if (methodsSelected.get(4) == 1) {
            addMethod(dataPanel, bayesLasso, bayesLassoStatus, xCoordinate, yCoordinate);
            yCoordinate = yCoordinate + 30;
        }
        if (methodsSelected.get(5) == 1) {
            addMethod(dataPanel, randomForest, randomForestStatus, xCoordinate, yCoordinate);
        }

        dialog.add(new JLayer<>(dataPanel, layerUI));
    }

    private void addMethod(JPanel dataPanel, JLabel methodLabel, JLabel methodLabelStatus, int xCoordinate, int yCoordinate) {
        int width = 150;
        methodLabel.setBounds(xCoordinate, yCoordinate, width, height);
        methodLabelStatus.setBounds(xCoordinate + width, yCoordinate, width, height);
        dataPanel.add(methodLabel);
        dataPanel.add(methodLabelStatus);
    }

    private JLabel createLabel(Font font, String label) {
        JLabel triatInProgress = new JLabel(label);
        triatInProgress.setFont(font);
        return triatInProgress;
    }
}
