package com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel.bms;

import com.icrisat.sbdm.ismu.retrofit.bms.BMSRetrofitClient;
import com.icrisat.sbdm.ismu.ui.SubmitPanel;
import com.icrisat.sbdm.ismu.ui.WaitLayerUI;
import com.icrisat.sbdm.ismu.ui.mainFrame.GenoPhenoActionalListeners.dbConnectionPanel.SelectionTable;
import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * Trial data display panel.
 */
public class BMSDataSelectionPanel {
    private JDialog dialogBox;
    private SharedInformation sharedInformation;
    protected BMSRetrofitClient client;
    private JComboBox<String> cropsCombo;
    private BMSSearchPanel bmsSearchPanel;
    protected SelectionTable selectionTable;
    private SubmitPanel submitPanel;
    private WaitLayerUI layerUI = new WaitLayerUI();

    public BMSDataSelectionPanel(SharedInformation sharedInformation, SelectionTable selectionTable) {
        this.sharedInformation = sharedInformation;
        this.selectionTable = selectionTable;
        client = sharedInformation.getBmsRetrofitClient();
        dialogBox = new JDialog(sharedInformation.getMainFrame(), Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setTitle("BMS - Trial ");
        dialogBox.setSize(new Dimension(900, 600));
        dialogBox.setLocation(Util.getLocation(900, 600));

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        cropsCombo = new JComboBox<>();
        cropsCombo.setFont(sharedInformation.getFont());
        if (!addCrops(cropsCombo).equalsIgnoreCase(Constants.SUCCESS)) return;

        cropsCombo.addActionListener(this::getTrialInformation);
        bmsSearchPanel = new BMSSearchPanel(sharedInformation);
        bmsSearchPanel.searchButton.addActionListener(this::filterTableData);
        bmsSearchPanel.resetButton.addActionListener(this::resetTableData);
        JPanel cropPanel = new JPanel();
        cropPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        addToCropPanel(cropsCombo, bmsSearchPanel, cropPanel);
        mainPanel.add(cropPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(selectionTable.table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        selectionTable.table.setFillsViewportHeight(true);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        submitPanel = new SubmitPanel(sharedInformation.getOkButtonFont());
        submitPanel.submit.addActionListener(this::getData);
        submitPanel.cancel.addActionListener(e -> dialogBox.setVisible(false));
        mainPanel.add(submitPanel, BorderLayout.SOUTH);

        dialogBox.add(mainPanel);
        dialogBox.add(new JLayer<>(mainPanel, layerUI));
        setVisible(true);
    }

    /**
     * Add subpanels to crops panel
     */
    private void addToCropPanel(JComboBox<String> cropsCombo, BMSSearchPanel BMSSearchPanel, JPanel cropPanel) {
        cropPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 20, 5));
        Util.addDummyLabels(cropPanel, 4);
        JLabel selectACrop = new JLabel("Select a crop");
        selectACrop.setFont(sharedInformation.getBoldFont());
        cropPanel.add(selectACrop);
        cropPanel.add(cropsCombo);
        Util.addDummyLabels(cropPanel, 4);
        cropPanel.add(BMSSearchPanel);
        Util.addDummyLabels(cropPanel, 4);
    }

    /**
     * Download trial or study data and save in a file in result directory.
     *
     * @param e Action Event.
     */
    private void getData(ActionEvent e) {
        int selectedRow = selectionTable.table.getSelectedRow();
        if (selectedRow != -1) {
            DefaultTableModel model = (DefaultTableModel) selectionTable.table.getModel();
            List selectedData = new ArrayList((Collection) model.getDataVector().elementAt(selectedRow));
            setEnableForComponents(false);
            SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
                @Override
                protected Object doInBackground() throws Exception {
                    String status;
                    if (selectedData.get(7) == null || selectedData.get(7) == "")
                        status = client.getTrialData((String) selectedData.get(1), (String) selectedData.get(6));
                    else
                        status = client.getStudyData((String) selectedData.get(1), (String) selectedData.get(7));
                    if (status.equalsIgnoreCase(Constants.SUCCESS)) {
                        setVisible(false);
                    } else {
                        Util.showMessageDialog("Error: " + status);
                    }
                    layerUI.stop();
                    setEnableForComponents(true);
                    return null;
                }
            };
            worker.execute();
            layerUI.start();
        } else
            Util.showMessageDialog("Please select  a row.");
    }

    private void filterTableData(ActionEvent e) {
        setEnableForComponents(false);
        String programText = bmsSearchPanel.programInputField.getText();
        String trialText = bmsSearchPanel.trialInputField.getText();
        String locationText = bmsSearchPanel.locationInputField.getText();
        if (Objects.equals(programText, "") && Objects.equals(trialText, "") && Objects.equals(locationText, ""))
            selectionTable.table.setModel(selectionTable.defaultTableModel);
        DefaultTableModel newTableModel = new DefaultTableModel(Constants.bmsHeaders, 0);
        for (Object obj : selectionTable.defaultTableModel.getDataVector()) {
            List<String> row = (List<String>) obj;
            boolean programMatch = false, trialMatch = false, locationMatch = false;
            if (Objects.equals(programText, "")) {
                programMatch = true;
            } else if (Util.containsIgnoreCase(row.get(2), programText)) {
                programMatch = true;
            }
            if (Objects.equals(trialText, "")) {
                trialMatch = true;
            } else if (Util.containsIgnoreCase(row.get(3), trialText)) {
                trialMatch = true;
            }
            if (Objects.equals(locationText, "")) {
                locationMatch = true;
            } else if (Util.containsIgnoreCase(row.get(5), locationText)) {
                locationMatch = true;
            }
            if (programMatch && trialMatch && locationMatch)
                newTableModel.addRow(row.toArray());
        }
        selectionTable.table.setModel(newTableModel);
        setEnableForComponents(true);
    }

    private void resetTableData(ActionEvent e) {
        bmsSearchPanel.programInputField.setText("");
        bmsSearchPanel.trialInputField.setText("");
        bmsSearchPanel.locationInputField.setText("");
        selectionTable.table.setModel(selectionTable.defaultTableModel);
    }

    /**
     * Gets the trial information for selected crop.
     *
     * @param e Action Event.
     */
    private void getTrialInformation(ActionEvent e) {
        JComboBox cb = (JComboBox) e.getSource();
        String selectedCrop = (String) cb.getSelectedItem();
        setEnableForComponents(false);
        DefaultTableModel model = selectionTable.defaultTableModel;
        model.setRowCount(0);
        List<String[]> trialList = new ArrayList<>();
        SwingWorker<Object, Object> worker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                String triatstatus = client.getTrials(selectedCrop.trim(), trialList);
                sharedInformation.getLogger().info("Fetched List of Triats.");
                if (triatstatus.equalsIgnoreCase(Constants.SUCCESS)) {
                    long serialNo = 1;
                    for (String[] trial : trialList) {
                        String[] newTrial = new String[8];
                        newTrial[0] = String.valueOf(serialNo++);
                        System.arraycopy(trial, 0, newTrial, 1, trial.length);
                        model.addRow(newTrial);
                    }
                    selectionTable.table.setModel(model);
                } else {
                    Util.showMessageDialog("Error: " + triatstatus);
                }
                layerUI.stop();
                setEnableForComponents(true);
                return null;
            }
        };
        worker.execute();
        layerUI.start();
    }

    private void setEnableForComponents(boolean b) {
        cropsCombo.setEnabled(b);
        bmsSearchPanel.programInputField.setEnabled(b);
        bmsSearchPanel.trialInputField.setEnabled(b);
        bmsSearchPanel.locationInputField.setEnabled(b);
        bmsSearchPanel.searchButton.setEnabled(b);
        bmsSearchPanel.resetButton.setEnabled(b);
        submitPanel.submit.setEnabled(b);
    }

    /**
     * Makes a REST call and gets the list of Crops and add's to the combo box..
     *
     * @param cropsCombo Combo box.
     */
    private String addCrops(JComboBox<String> cropsCombo) {
        ArrayList<String> crops = new ArrayList<>();
        String status = client.getCrops(crops);
        if (status.equalsIgnoreCase(Constants.SUCCESS)) {
            for (String crop : crops) {
                cropsCombo.addItem("    " + crop + "    ");
            }
        } else {
            Util.showMessageDialog("Error: " + status);
        }
        return status;
    }

    public void setVisible(boolean visible) {
        dialogBox.setVisible(visible);
        DefaultTableModel model = (DefaultTableModel) selectionTable.table.getModel();
        model.setRowCount(0);
    }
}
