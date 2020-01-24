package com.icrisat.sbdm.ismu.ui.openDialog.components.genotype.gobii;

import com.icrisat.sbdm.ismu.retrofit.gobii.GOBIIRetrofitClient;
import com.icrisat.sbdm.ismu.ui.WaitLayerUI;
import com.icrisat.sbdm.ismu.ui.openDialog.components.SubmitPanel;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.PathConstants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;

public class GOBIIDataSelectionPanel {

    protected JDialog dialogBox;
    protected SharedInformation sharedInformation;
    protected GOBIIDataSetTable gobiiDataSetTable;
    protected GOBIISearchPanel gobiiSearchPanel;
    protected SubmitPanel submitPanel;
    protected WaitLayerUI layerUI = new WaitLayerUI();

    public GOBIIDataSelectionPanel(SharedInformation sharedInformation, GOBIIDataSetTable gobiiDataSetTable) {
        this.sharedInformation = sharedInformation;
        dialogBox = new JDialog(sharedInformation.getMainFrame(), Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setTitle("GOBII - Dataset");
        dialogBox.setSize(new Dimension(900, 500));
        dialogBox.setLocation(Util.getLocation(900, 500));

        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BorderLayout());
        dataPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        gobiiSearchPanel = new GOBIISearchPanel(sharedInformation);
        gobiiSearchPanel.searchButton.addActionListener(this::filterTableData);
        gobiiSearchPanel.resetButton.addActionListener(this::resetTableData);


        this.gobiiDataSetTable = gobiiDataSetTable;
        String status = getDataSets(this.gobiiDataSetTable.defaultTableModel);
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            dataPanel.add(gobiiSearchPanel, BorderLayout.NORTH);
            this.gobiiDataSetTable.table.setModel(this.gobiiDataSetTable.defaultTableModel);
            JScrollPane scrollPane = new JScrollPane(this.gobiiDataSetTable.table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            this.gobiiDataSetTable.table.setFillsViewportHeight(true);
            dataPanel.add(scrollPane, BorderLayout.CENTER);

            submitPanel = new SubmitPanel(sharedInformation.getBoldFont());
            submitPanel.submit.addActionListener(this::startDataExtract);
            submitPanel.cancel.addActionListener(e -> {
                layerUI.stop();
                dialogBox.setVisible(false);
            });
            submitPanel.submit.setEnabled(true);
            submitPanel.cancel.setEnabled(true);

            dataPanel.add(submitPanel, BorderLayout.SOUTH);
            dialogBox.add(new JLayer<>(dataPanel, layerUI));

            dialogBox.setVisible(true);
        } else {
            Util.showMessageDialog("Error: " + status);
        }
    }

    protected String getBrapiOutputFileName() {
        String outputFileName;
        if (sharedInformation.getPathConstants().resultDirectory == null)
            outputFileName = sharedInformation.getPathConstants().tempResultDirectory + "GOBII" + "_" + new SimpleDateFormat("hhmmss").format(new Date()) + ".csv";
        else
            outputFileName = sharedInformation.getPathConstants().resultDirectory + "GOBII" + "_" + new SimpleDateFormat("hhmmss").format(new Date()) + ".csv";
        return outputFileName;
    }

    protected List getSelectedData() {
        List selectedData = null;
        int selectedRow = gobiiDataSetTable.table.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = (DefaultTableModel) gobiiDataSetTable.table.getModel();
            selectedData = new ArrayList((Collection) model.getDataVector().elementAt(selectedRow));
        } else {
            Util.showMessageDialog("Please select  a row.");
        }
        return selectedData;
    }


    /**
     * Gets the dataSet information using REST call.
     */
    protected String getDataSets(DefaultTableModel model) {
        model.setRowCount(0);
        GOBIIRetrofitClient client = sharedInformation.getGobiiRetrofitClient();
        List<String[]> dataSetList = new ArrayList<>();
        String status = client.getVariantSets(dataSetList);
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            // status = client.getDataSets(dataSetList);
            status = client.getVariantSets(dataSetList);
        }
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            long serialNo = 1;
            for (String[] dataSet : dataSetList) {
                String[] newDataset = new String[5];
                newDataset[0] = String.valueOf(serialNo++);
                System.arraycopy(dataSet, 0, newDataset, 1, dataSet.length);
                model.addRow(newDataset);
            }
        }
        return status;
    }

    /**
     * Download trial or study data and save in a file in result directory.
     *
     * @param e Action Event.
     */
    protected void startDataExtract(ActionEvent e) {
        List finalSelectedData = getSelectedData();
        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() {
                if (finalSelectedData != null) {
                    String outputFileName = getBrapiOutputFileName();
                    String status = sharedInformation.getGobiiRetrofitClient().downloadData((String) finalSelectedData.get(1), (String) finalSelectedData.get(3), outputFileName);
                    if (status.equals(Constants.SUCCESS)) {
                        PathConstants.recentGenotypeFile = outputFileName;
                        dialogBox.setVisible(false);
                    } else {
                        Util.showMessageDialog(status);
                    }
                }
                layerUI.stop();
                submitPanel.submit.setEnabled(true);
                return null;
            }
        };
        worker.execute();
        submitPanel.submit.setEnabled(false);
        layerUI.start();
    }

    private void setEnableForComponents(boolean b) {
        gobiiSearchPanel.variantSetInputField.setEnabled(b);
        gobiiSearchPanel.studyInputField.setEnabled(b);
        gobiiSearchPanel.searchButton.setEnabled(b);
        gobiiSearchPanel.resetButton.setEnabled(b);
        submitPanel.submit.setEnabled(b);
    }

    protected void filterTableData(ActionEvent e) {
        setEnableForComponents(false);
        String variantSetInputFieldText = gobiiSearchPanel.variantSetInputField.getText();
        String studyInputFieldText = gobiiSearchPanel.studyInputField.getText();
        if (Objects.equals(variantSetInputFieldText, "") && Objects.equals(studyInputFieldText, ""))
            gobiiDataSetTable.table.setModel(gobiiDataSetTable.defaultTableModel);
        DefaultTableModel newTableModel = new DefaultTableModel(Constants.gobiiHeaders, 0);
        for (Object obj : gobiiDataSetTable.defaultTableModel.getDataVector()) {
            List<String> row = (List<String>) obj;
            boolean variantSearchMatch = false, studyMatch = false;
            if (Objects.equals(variantSearchMatch, "")) {
                variantSearchMatch = true;
            } else if (Util.containsIgnoreCase(row.get(1), variantSetInputFieldText)) {
                variantSearchMatch = true;
            }
            if (Objects.equals(studyInputFieldText, "")) {
                studyMatch = true;
            } else if (Util.containsIgnoreCase(row.get(2), studyInputFieldText)) {
                studyMatch = true;
            }
            if (variantSearchMatch && studyMatch)
                newTableModel.addRow(row.toArray());
        }
        gobiiDataSetTable.table.setModel(newTableModel);
        setEnableForComponents(true);
    }

    protected void resetTableData(ActionEvent e) {
        gobiiSearchPanel.variantSetInputField.setText("");
        gobiiSearchPanel.studyInputField.setText("");
        gobiiDataSetTable.table.setModel(gobiiDataSetTable.defaultTableModel);
    }
}
