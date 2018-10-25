package com.icrisat.sbdm.ismu.ui.components;

import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import com.icrisat.sbdm.ismu.util.UtilCSV;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Selects columns from a file and creates a new file.
 * If cancelled or all columns are selected, no new file is created.
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ColumnSelection {

    private JDialog dialogBox;
    private SharedInformation sharedInformation;
    private ColumnSelectionPanel columnSelectionPanel;
    private JPanel buttonPanel;
    boolean isCancelled = false;

    public ColumnSelectionPanel getColumnSelectionPanel() {
        return columnSelectionPanel;
    }

    /**
     * Creates the dialog box.
     *
     * @param sharedInformation shared Info
     */
    public void createDialog(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
        this.columnSelectionPanel = new ColumnSelectionPanel(sharedInformation);
        dialogBox = new JDialog(sharedInformation.getMainFrame(), Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setTitle("Select required field's ");
        dialogBox.setSize(new Dimension(600, 300));
        dialogBox.setLocation(Util.getLocation(600, 550));
        dialogBox.setLayout(new BorderLayout());
        dialogBox.add(columnSelectionPanel, BorderLayout.CENTER);
        buttonPanel = new JPanel();
        addButtons();
        dialogBox.add(buttonPanel, BorderLayout.SOUTH);
        dialogBox.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Add buttons to the panel.
     */
    private void addButtons() {
        JButton submit = new JButton("Submit");
        submit.setFont(sharedInformation.getBoldFont());
        submit.addActionListener(e -> submitAction());
        JButton cancel = new JButton("Cancel");
        cancel.setFont(sharedInformation.getBoldFont());
        cancel.addActionListener(this::cancelAction);
        buttonPanel.setLayout(new GridLayout(1, 6));
        Util.addDummyLabels(buttonPanel, 2);
        buttonPanel.add(submit);
        buttonPanel.add(cancel);
        Util.addDummyLabels(buttonPanel, 2);
    }

    /**
     * Checks that at-least one field is selected.
     * Copies that field into the new file. Rename it to the old file and exits.
     */
    private void submitAction() {
        isCancelled = false;
        DefaultListModel selectedModel = (DefaultListModel) columnSelectionPanel.getSelectedColumns().getModel();
        if (selectedModel.getSize() == 0) {
            Util.showMessageDialog("Please select the fields required.");
        } else {
            List<String> requiredFields = new ArrayList<>();
            for (int i = 0; i < selectedModel.getSize(); i++) {
                requiredFields.add((String) selectedModel.getElementAt(i));
            }
            columnSelectionPanel.setOutputFileName(Util.stripFileExtension(columnSelectionPanel.getFileName()) + "_selected" + ".csv");
            UtilCSV.createCSVwithRequiredFields(columnSelectionPanel.getFileName(), columnSelectionPanel.getOutputFileName(), requiredFields);
            dialogBox.setVisible(false);
        }
    }

    /**
     * Displays panel to select few columns.
     *
     * @param fileName    File name
     * @param noOfHeaders no of headers
     */
    public String selectColumns(String fileName, int noOfHeaders) {
        String status = columnSelectionPanel.populateAllColumns(fileName, noOfHeaders);
        if (status.equals(Constants.SUCCESS)) {
            this.dialogBox.setVisible(true);
        }
        if(isCancelled)return Constants.USER_CANCELLED;
        return status;
    }

    private void cancelAction(ActionEvent e) {
        dialogBox.setVisible(false);
        isCancelled = true;
    }
}
