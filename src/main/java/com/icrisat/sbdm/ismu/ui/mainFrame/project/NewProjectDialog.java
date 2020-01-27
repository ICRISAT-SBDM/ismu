package com.icrisat.sbdm.ismu.ui.mainFrame.project;

import com.icrisat.sbdm.ismu.ui.mainFrame.DynamicTree;
import com.icrisat.sbdm.ismu.util.PathConstants;
import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


public class NewProjectDialog {

    private SharedInformation sharedInformation;
    private DynamicTree dynamicTree;
    private JDialog dialogBox;
    FolderSelectionPanel folderSelectionPanel;
    GenoPhenoSelectionPanel genoPhenoSelectionPanel;
    JButton submitButton;

    public void setSharedInformation(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
    }

    /**
     * Set the visibility of Dialog
     *
     * @param value true/false.
     */
    public void setVisible(boolean value) {
        dialogBox.setVisible(value);
    }

    /**
     * Create open dialog.
     *
     * @param sharedInformation
     * @param dynamicTree
     */
    public NewProjectDialog(SharedInformation sharedInformation, DynamicTree dynamicTree) {
        this.sharedInformation = sharedInformation;
        this.dynamicTree = dynamicTree;
        dialogBox = new JDialog(this.sharedInformation.getMainFrame(), Dialog.ModalityType.APPLICATION_MODAL);
        dialogBox.setLocation(Util.getLocation(500, 210));
        dialogBox.setSize(new Dimension(500, 210));
        dialogBox.setTitle("New Project");

        createComponents();
        dialogBox.setResizable(false);

        dialogBox.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                actionCancel();
            }

            public void windowClosing(WindowEvent e) {
                actionCancel();
            }
        });
        dialogBox.setVisible(true);
    }

    /**
     * Create the components to be added to dialog box.
     */
    private void createComponents() {
        folderSelectionPanel = new FolderSelectionPanel(sharedInformation, this);
        genoPhenoSelectionPanel = new GenoPhenoSelectionPanel(sharedInformation, dynamicTree);
        submitButton = new JButton("Submit");
        submitButton.setFont(sharedInformation.getFont());
        submitButton.addActionListener(this::actionSubmit);
        JPanel mainPanel = new JPanel();
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 30, 30, 20));
        mainPanel.add(folderSelectionPanel);
        mainPanel.add(genoPhenoSelectionPanel);
        mainPanel.add(submitButton);
        dialogBox.add(mainPanel);
        setEnableGenoPhenoSelection(false);
    }

    private void actionSubmit(ActionEvent actionEvent) {
        if (PathConstants.recentPhenotypeFile == null || PathConstants.recentPhenotypeFile == null)
            Util.showMessageDialog("Please select a genotype and phenotype file.");
        else
            dialogBox.setVisible(false);
    }

    void setEnableGenoPhenoSelection(boolean value) {
        genoPhenoSelectionPanel.setEnable(value);
    }

    /**
     * Cancel directory button action.
     */
    private void actionCancel() {
        Util.showMessageDialog("Cancelled project creation, please start from creating New project next time.");
        Util.clearCurrentApplicationState(sharedInformation, dynamicTree);
        dialogBox.setVisible(false);
    }
}
