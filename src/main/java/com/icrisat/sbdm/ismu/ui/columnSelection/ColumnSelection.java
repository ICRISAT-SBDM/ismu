package com.icrisat.sbdm.ismu.ui.columnSelection;

import com.icrisat.sbdm.ismu.util.Constants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
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
    boolean isCancelled = false;
    List<String> selectedFields;

    /**
     * Creates the dialog box.
     *
     * @param sharedInformation shared Info
     */
    public void createDialog(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
        this.columnSelectionPanel = new ColumnSelectionPanel(sharedInformation);
        selectedFields = new ArrayList<>();
        isCancelled = true;
        dialogBox = new JDialog(sharedInformation.getMainFrame(), Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setTitle("Select required field's ");
        dialogBox.setSize(new Dimension(600, 300));
        dialogBox.setLocation(Util.getLocation(600, 550));
        dialogBox.setLayout(new BorderLayout());
        dialogBox.add(columnSelectionPanel, BorderLayout.CENTER);
        dialogBox.add(getButtonPanel(), BorderLayout.SOUTH);
        dialogBox.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    }

    /**
     * Add buttons to the panel.
     */
    private JPanel getButtonPanel() {
        JPanel buttonPanel = new JPanel();
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
        return buttonPanel;
    }

    /**
     * Checks that at-least one field is selected.
     * Copies that field into the new file. Rename it to the old file and exits.
     */
    private void submitAction() {
        isCancelled = false;
        DefaultListModel selectedModel = (DefaultListModel) columnSelectionPanel.getSelectedColumns().getModel();
        if (selectedModel.getSize() == 0) {
            Util.showMessageDialog("Please select atleast one field.");
        } else {
            selectedFields = new ArrayList<>();
            for (int i = 0; i < selectedModel.getSize(); i++) {
                String elementAt = (String) selectedModel.getElementAt(i);
                /*
                 * elementAt = ""TraitName""
                 * elementAt = "TraitName"
                 */
                elementAt = elementAt.substring(1, elementAt.length() - 1);
                selectedFields.add(elementAt);
            }
            dialogBox.setVisible(false);
        }
    }

    /**
     * Displays panel to select few columns.
     *
     * @param columnNames Column Names
     */
    public String selectColumns(List<String> columnNames) {
        String status = columnSelectionPanel.populateAllColumns(columnNames);
        if (status.equals(Constants.SUCCESS)) {
            this.dialogBox.setVisible(true);
        }
        if (isCancelled) status = Constants.USER_CANCELLED;
        return status;
    }

    public List<String> getSelectedFields() {
        return selectedFields;
    }

    private void cancelAction(ActionEvent e) {
        dialogBox.setVisible(false);

        isCancelled = true;
    }
}
