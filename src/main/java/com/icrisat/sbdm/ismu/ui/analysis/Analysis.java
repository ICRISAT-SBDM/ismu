package com.icrisat.sbdm.ismu.ui.analysis;

import com.icrisat.sbdm.ismu.ui.analysis.AdditionalParametersPanel.AdditionalParametersPanel;
import com.icrisat.sbdm.ismu.ui.analysis.AnalysisMethods.AnalysisMethodsPanel;
import com.icrisat.sbdm.ismu.ui.analysis.AnalysisMethods.FortranMethodsPanel;
import com.icrisat.sbdm.ismu.ui.analysis.AnalysisMethods.RMethodsPanel;
import com.icrisat.sbdm.ismu.ui.components.ColumnSelectionPanel;
import com.icrisat.sbdm.ismu.ui.mainFrame.DynamicTree;
import com.icrisat.sbdm.ismu.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

/**
 * Analysis panel.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Analysis {
    private JDialog dialogBox;
    private SharedInformation sharedInformation;
    private ColumnSelectionPanel columnSelectionPanel;
    private SelectFilesPanel selectFilesPanel;
    private AdditionalParametersPanel additionalParametersPanel;
    private AnalysisMethodsPanel analysisMethodsPanel;
    private DataSubsetPanel dataSubsetPanel;
    private AnalysisDataObject analysisDataObject;
    private AnalysisUtil analysisUtil;
    private DynamicTree dynamicTree;
    private AnalysisProgress analysisProgress;
    private RMethodsPanel rMethodsPanel;
    private FortranMethodsPanel fortranMethodsPanel;
    private boolean isAnalysisCancelled = false;

    public void setIsAnalysisCancelled(boolean analysisCancelled) {
        isAnalysisCancelled = analysisCancelled;
    }

    @Autowired
    public void setAnalysisProgress(AnalysisProgress analysisProgress) {
        this.analysisProgress = analysisProgress;
    }

    @Autowired
    public void setDynamicTree(DynamicTree dynamicTree) {
        this.dynamicTree = dynamicTree;
    }

    @Autowired
    public void setAnalysisUtil(AnalysisUtil analysisUtil) {
        this.analysisUtil = analysisUtil;
    }

    @Autowired
    public void setAnalysisDataObject(AnalysisDataObject analysisDataObject) {
        this.analysisDataObject = analysisDataObject;
    }

    @Autowired
    public void setSelectFilesPanel(SelectFilesPanel selectFilesPanel) {
        this.selectFilesPanel = selectFilesPanel;
    }

    @Autowired
    public void setSharedInformation(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
    }

    @Autowired
    public void setAdditionalParametersPanel(AdditionalParametersPanel additionalParametersPanel) {
        this.additionalParametersPanel = additionalParametersPanel;
    }

    @Autowired
    public void setDataSubsetPanel(DataSubsetPanel dataSubsetPanel) {
        this.dataSubsetPanel = dataSubsetPanel;
    }

    /**
     * Creates dialog box.
     */
    public void createDialog(ColumnSelectionPanel columnSelectionPanel) {
        this.columnSelectionPanel = columnSelectionPanel;
        additionalParametersPanel.createAdditionalParametersPanel();
        rMethodsPanel = new RMethodsPanel(sharedInformation);
        fortranMethodsPanel = new FortranMethodsPanel(sharedInformation);
        analysisMethodsPanel = new AnalysisMethodsPanel(sharedInformation, rMethodsPanel, fortranMethodsPanel);
        dialogBox = new JDialog(sharedInformation.getMainFrame(), Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setSize(950, 700);
        dialogBox.setLocation(Util.getLocation(900, 700));
        dialogBox.setLayout(null);
        dialogBox.setTitle("Data Analysis");
        dialogBox.setResizable(false);
        createAndAddComponents();

        dialogBox.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                actionCancel();
            }

            public void windowClosing(WindowEvent e) {
                actionCancel();
            }
        });
    }

    public void setVisible() {
        Util.fillGenoAndPhenoFiles(selectFilesPanel.getGenoCombo(), selectFilesPanel.getPhenoCombo());
        resetComponents();
        dialogBox.setVisible(true);
    }

    /**
     * When cancel is clicked.
     */
    private void actionCancel() {
        resetComponents();
        dialogBox.setVisible(false);
    }

    /**
     * Resets all components to its default value.
     */
    private void resetComponents() {
        JTabbedPane methodPane = analysisMethodsPanel.getMethodPane();
        RMethodsPanel rMethodsPanel = (RMethodsPanel) methodPane.getComponentAt(0);
        rMethodsPanel.setSelected(false);
        FortranMethodsPanel fortranMethodsPanel = (FortranMethodsPanel) methodPane.getComponentAt(1);
        fortranMethodsPanel.setSelected(false);

        dataSubsetPanel.resetValues();
        additionalParametersPanel.getBayesPanel().enableComponent(false);
        additionalParametersPanel.getSecondPanel().getRandomForestPanel().enableComponent(false);
        additionalParametersPanel.getSecondPanel().getProcessorPanel().resetValue();
        additionalParametersPanel.getCrossValidationPanel().resetValue();
    }

    /**
     * Creates the components and adds them to panel which intern is added to dialog.
     */
    private void createAndAddComponents() {
        columnSelectionPanel.setBounds(10, 160, 550, 300);
        dialogBox.add(columnSelectionPanel);
        selectFilesPanel.setColumnSelectionPanel(columnSelectionPanel);
        selectFilesPanel.setBounds(10, 10, 550, 150);
        dialogBox.add(selectFilesPanel);
        additionalParametersPanel.setBounds(10, 460, 700, 210);
        dialogBox.add(additionalParametersPanel);
        analysisMethodsPanel.setBounds(560, 10, 380, 300);
        dialogBox.add(analysisMethodsPanel);
        dataSubsetPanel.setBounds(560, 310, 380, 150);
        dialogBox.add(dataSubsetPanel);
        JButton start = new JButton("Start");
        start.setFont(sharedInformation.getBoldFont());
        start.setBounds(750, 500, 150, 70);
        start.addActionListener(this::actionStart);
        dialogBox.add(start);
        JButton cancel = new JButton("Cancel");
        cancel.setFont(sharedInformation.getBoldFont());
        cancel.setBounds(750, 580, 150, 70);
        cancel.addActionListener(e -> actionCancel());
        rMethodsPanel.getChkBayesB().addActionListener(this::enableDisablePanel);
        rMethodsPanel.getChkBayesCPI().addActionListener(this::enableDisablePanel);
        rMethodsPanel.getChkBayesLasso().addActionListener(this::enableDisablePanel);
        rMethodsPanel.getChkRandomForest().addActionListener(e -> additionalParametersPanel.getSecondPanel().getRandomForestPanel().enableComponent(rMethodsPanel.getChkRandomForest().isSelected()));
        dialogBox.add(cancel);
    }

    private void enableDisablePanel(ActionEvent e) {
        //this is the first check
        // this is the last uncheck
        int noOfChecked = 0;
        if (rMethodsPanel.getChkBayesB().isSelected()) noOfChecked++;
        if (rMethodsPanel.getChkBayesCPI().isSelected()) noOfChecked++;
        if (rMethodsPanel.getChkBayesLasso().isSelected()) noOfChecked++;
        AbstractButton abstractButton = (AbstractButton) e.getSource();
        boolean currentSelection = abstractButton.getModel().isSelected();
        // Current action is selection and noOfSelected is one
        if ((currentSelection && noOfChecked == 1) || (!currentSelection && noOfChecked == 0)) {
            additionalParametersPanel.getBayesPanel().enableComponent(currentSelection);
        }
    }

    private void actionStart(ActionEvent e) {

        storeUserSelectedValues();
        if (!validateUserFields()) {
            return;
        }
        // Process if at-least one of phenotype or genotype selected. Else don't do anything.
        // setEnableForAllComponents(false);
        analysisProgress.createDialog(sharedInformation.getMainFrame(), sharedInformation.getFont(), analysisDataObject.getMethodsSelected(), this);
        List<Triat> requiredTriats = getRequiredTriats();
        List<Integer> methodsSelected = analysisDataObject.getMethodsSelected();
        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                // define what thread will do here
                analysisProgress.methodName.setText("Generating input for analysis.");
                isAnalysisCancelled = false;
                addAnalysisLogFiles();
                if (analysisUtil.runAscript(e, analysisDataObject, sharedInformation)) {
                    for (Triat triat : requiredTriats) {
                        analysisProgress.clearFields();
                        analysisProgress.triatName.setText(triat.triatName);
                        analysisDataObject.setResultFileName(triat.triatName + "_" + analysisDataObject.getResultFileStub() + ".htm");
                        if (!analysisUtil.runABscript(e, analysisDataObject, sharedInformation, triat.triatNo))
                            break;
                        boolean statusFailed = false;
                        for (int i = 0; i < 6; i++) {
                            if (isAnalysisCancelled) break;
                            if (methodsSelected.get(i) == 1) {
                                analysisProgress.methodName.setText(getMethodName(i));
                                if (!analysisUtil.runBscript(e, analysisDataObject, sharedInformation, triat.triatNo, i)) {
                                    statusFailed = true;
                                    break;
                                } else {
                                    setMethodCompletionStatus(i);
                                }
                            }
                        }
                        if (statusFailed || isAnalysisCancelled) break;
                        analysisUtil.runCscript(e, analysisDataObject, sharedInformation);
                        saveAnalysisFiles(analysisDataObject.getResultFileName());
                    }
                } else {
                    Util.showMessageDialog("Could not perform analysis.\nPlease check a.txt for details");
                }
                analysisProgress.actionCancel();
                return null;
            }
        };
        worker.execute();
        dialogBox.setVisible(false);
        analysisProgress.display();
    }

    private void addAnalysisLogFiles() {

        for (int i = 0; i < dynamicTree.getLogNode().getChildCount(); i++) {
            DefaultMutableTreeNode childAt = (DefaultMutableTreeNode) dynamicTree.getLogNode().getChildAt(i);
            FileLocation userObject = (FileLocation) childAt.getUserObject();
            if (userObject.getFileNameInApplication().equalsIgnoreCase("a_log.txt"))
                return;
        }
        FileLocation aLogFileLocation = new FileLocation("a_log.txt", sharedInformation.getPathConstants().resultDirectory + "a_log.txt");
        dynamicTree.addObject(dynamicTree.getLogNode(), aLogFileLocation, Boolean.TRUE);
        FileLocation abLogFileLocation = new FileLocation("ab_log.txt", sharedInformation.getPathConstants().resultDirectory + "ab_log.txt");
        dynamicTree.addObject(dynamicTree.getLogNode(), abLogFileLocation, Boolean.TRUE);
        FileLocation bLogFileLocation = new FileLocation("b_log.txt", sharedInformation.getPathConstants().resultDirectory + "b_log.txt");
        dynamicTree.addObject(dynamicTree.getLogNode(), bLogFileLocation, Boolean.TRUE);
    }

    private boolean validateUserFields() {
        if (analysisDataObject.getPhenoFile().equalsIgnoreCase(Constants.SELECT) || analysisDataObject.getGenoFile().equalsIgnoreCase(Constants.SELECT)) {
            Util.showMessageDialog("Please select one genotype and phenotype files.");
            return false;
        }
        if (getRequiredTriats().size() == 0) {
            Util.showMessageDialog("Please select atleast one triat.");
            return false;
        }
        boolean selected = false;
        for (int i : analysisDataObject.getMethodsSelected()) {
            if (i == 1) selected = true;
        }
        if (!selected) {
            Util.showMessageDialog("Please select atleast one method.");
            return false;
        }

        return true;
    }

    private void setMethodCompletionStatus(int i) {
        switch (i) {
            case 0:
                analysisProgress.ridgeRegressionStatus.setText("Done");
                break;
            case 1:
                analysisProgress.kinshipGaussStatus.setText("Done");
                break;
            case 2:
                analysisProgress.bayesBStatus.setText("Done");
                break;
            case 3:
                analysisProgress.bayesCPIStatus.setText("Done");
                break;
            case 4:
                analysisProgress.bayesLassoStatus.setText("Done");
                break;
            default:
                analysisProgress.randomForestStatus.setText("Done");
        }
    }

    private String getMethodName(int i) {

        switch (i) {
            case 0:
                return "RidgeRegression";
            case 1:
                return "KinshipGauss";
            case 2:
                return "BayesB";
            case 3:
                return "BayesCPI";
            case 4:
                return "BayesLasso";
            default:
                return "RandomForest";
        }
    }

    /**
     * Saves result in tree and displays it.
     *
     * @param fileName result filename.
     */
    private void saveAnalysisFiles(String fileName) {
        FileLocation analysisFileLocation = new FileLocation(fileName, sharedInformation.getPathConstants().resultDirectory + fileName);
        dynamicTree.addObject(dynamicTree.getResultsNode(), analysisFileLocation, Boolean.TRUE);
        sharedInformation.getPathConstants().resultFiles.add(analysisFileLocation);

        String status = UtilHTML.editHTML2DisplayImages(analysisFileLocation, "src='", "src='file:///");
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            Util.showMessageDialog(status);
        }
        UtilHTML.displayHTMLFile(analysisFileLocation);
    }

    private List<Triat> getRequiredTriats() {
        List<Triat> triatPosition = new ArrayList<>();
        String phenoFile = Objects.requireNonNull(selectFilesPanel.getPhenoCombo().getSelectedItem()).toString();
        ListModel model = columnSelectionPanel.getSelectedColumns().getModel();
        List<String> headers = UtilCSV.getHeaders(sharedInformation.getPathConstants().resultDirectory + phenoFile);
        for (int i = 0; i < model.getSize(); i++) {
            // Adding +1 as column no start from 1 in R
            triatPosition.add(new Triat((String) model.getElementAt(i), headers.indexOf(model.getElementAt(i))));
        }
        return triatPosition;
    }

    private void storeUserSelectedValues() {
        analysisDataObject.clearValues();
        ListModel selectedModel = columnSelectionPanel.getSelectedColumns().getModel();
        int size = selectedModel.getSize();
        for (int i = 0; i < size; i++) {
            analysisDataObject.getPhenoTraits().add((String) selectedModel.getElementAt(i));
        }
        fillMethodSelectionStatus();
        int percentMissingMarkers = (int) dataSubsetPanel.getPercentSpinner().getModel().getValue();
        analysisDataObject.setPercentMissingMarkers(String.valueOf(.01 * percentMissingMarkers));
        analysisDataObject.setPICValue(dataSubsetPanel.getPicSpinner().getModel().getValue().toString());
        analysisDataObject.setMAF(dataSubsetPanel.getMafSpinner().getModel().getValue().toString());
        analysisDataObject.setForests(additionalParametersPanel.getSecondPanel().getRandomForestPanel().getForestsSpinner().getModel().getValue().toString());
        analysisDataObject.setCores(additionalParametersPanel.getSecondPanel().getProcessorPanel().getCoresSpinner().getModel().getValue().toString());
        analysisDataObject.setReplication(additionalParametersPanel.getCrossValidationPanel().getReplicationSpinner().getModel().getValue().toString());
        analysisDataObject.setFold(additionalParametersPanel.getCrossValidationPanel().getFoldSpinner().getModel().getValue().toString());
        analysisDataObject.setInputFile(Objects.requireNonNull(selectFilesPanel.getGenoCombo().getSelectedItem()).toString(), Objects.requireNonNull(selectFilesPanel.getPhenoCombo().getSelectedItem()).toString());
    }

    private void fillMethodSelectionStatus() {
        JTabbedPane methodPane = analysisMethodsPanel.getMethodPane();
        RMethodsPanel rMethodsPanel = (RMethodsPanel) methodPane.getComponentAt(0);
        FortranMethodsPanel fortranMethodsPanel = (FortranMethodsPanel) methodPane.getComponentAt(1);
        List<Integer> methodsSelected = analysisDataObject.getMethodsSelected();

        if (rMethodsPanel.getChkRidgeRegression().isSelected()) methodsSelected.add(1);
        else methodsSelected.add(-1);

        if (rMethodsPanel.getChkKinshipGauss().isSelected()) methodsSelected.add(1);
        else methodsSelected.add(-1);

        if (rMethodsPanel.getChkBayesB().isSelected()) methodsSelected.add(1);
        else methodsSelected.add(-1);

        if (rMethodsPanel.getChkBayesCPI().isSelected()) methodsSelected.add(1);
        else methodsSelected.add(-1);

        if (rMethodsPanel.getChkBayesLasso().isSelected()) methodsSelected.add(1);
        else methodsSelected.add(-1);

        if (rMethodsPanel.getChkRandomForest().isSelected()) methodsSelected.add(1);
        else methodsSelected.add(-1);

        if (fortranMethodsPanel.getChkRidgeRegression().isSelected()) methodsSelected.add(1);
        else methodsSelected.add(-1);

        if (fortranMethodsPanel.getChkBayesA().isSelected()) methodsSelected.add(1);
        else methodsSelected.add(-1);
    }

    class Triat {
        String triatName;
        int triatNo;

        Triat(String triatName, int triatNo) {
            this.triatName = triatName;
            this.triatNo = triatNo;
        }
    }
}
