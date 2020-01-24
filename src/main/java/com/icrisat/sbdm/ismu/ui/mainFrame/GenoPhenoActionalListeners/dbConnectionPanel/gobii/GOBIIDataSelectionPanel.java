package com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel.gobii;

import com.icrisat.sbdm.ismu.retrofit.gobii.GOBIIRetrofitClient;
import com.icrisat.sbdm.ismu.ui.SubmitPanel;
import com.icrisat.sbdm.ismu.ui.WaitLayerUI;
import com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel.SelectionTable;
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
    protected SelectionTable selectionTable;
    protected GOBIIRetrofitClient client;
    protected GOBIISearchPanel gobiiSearchPanel;
    protected SubmitPanel submitPanel;
    protected WaitLayerUI layerUI = new WaitLayerUI();

    public GOBIIDataSelectionPanel(SharedInformation sharedInformation, SelectionTable selectionTable) {
        this.sharedInformation = sharedInformation;
        this.selectionTable = selectionTable;
        client = sharedInformation.getGobiiRetrofitClient();
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

        String status = getVariantSets(this.selectionTable.defaultTableModel);
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            dataPanel.add(gobiiSearchPanel, BorderLayout.NORTH);
            this.selectionTable.table.setModel(this.selectionTable.defaultTableModel);
            JScrollPane scrollPane = new JScrollPane(this.selectionTable.table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            this.selectionTable.table.setFillsViewportHeight(true);
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

    /**
     * Retrieve the selected row from rowNumber
     */
    protected List getSelectedData() {
        List selectedData = null;
        int selectedRow = selectionTable.table.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = (DefaultTableModel) selectionTable.table.getModel();
            selectedData = new ArrayList((Collection) model.getDataVector().elementAt(selectedRow));
        } else {
            Util.showMessageDialog("Please select  a row.");
        }
        return selectedData;
    }

    /**
     * Gets the list of variantSets from GOBii.
     */
    protected String getVariantSets(DefaultTableModel model) {
        //Clears the table and later adds elements to it.
        model.setRowCount(0);
        List<String[]> variantSetList = new ArrayList<>();
        String status = client.getVariantSets(variantSetList);
        //Retry
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            variantSetList = new ArrayList<>();
            status = client.getVariantSets(variantSetList);
        }
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            long serialNo = 1;
            // Adds serial number at the beginning of the variant set element
            for (String[] variantSet : variantSetList) {
                String[] newVariantSet = new String[5];
                newVariantSet[0] = String.valueOf(serialNo++);
                System.arraycopy(variantSet, 0, newVariantSet, 1, variantSet.length);
                model.addRow(newVariantSet);
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
        setEnableForComponents(false);
        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() {
                if (finalSelectedData != null) {
                    String outputFileName = PathConstants.resultDirectory + "GOBII" + "_" + new SimpleDateFormat("hhmmss").format(new Date()) + ".csv";
                    String status = client.downloadVariantSet((String) finalSelectedData.get(1), (String) finalSelectedData.get(3), outputFileName);
                    if (status.equals(Constants.SUCCESS)) {
                        PathConstants.recentGenotypeFile = outputFileName;
                        dialogBox.setVisible(false);
                    } else {
                        Util.showMessageDialog(status);
                    }
                }
                layerUI.stop();
                setEnableForComponents(true);
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
            selectionTable.table.setModel(selectionTable.defaultTableModel);
        DefaultTableModel newTableModel = new DefaultTableModel(Constants.gobiiHeaders, 0);
        for (Object obj : selectionTable.defaultTableModel.getDataVector()) {
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
        selectionTable.table.setModel(newTableModel);
        setEnableForComponents(true);
    }

    protected void resetTableData(ActionEvent e) {
        gobiiSearchPanel.variantSetInputField.setText("");
        gobiiSearchPanel.studyInputField.setText("");
        selectionTable.table.setModel(selectionTable.defaultTableModel);
    }
}
