package com.icrisat.sbdm.ismu.ui.mainFrame.project;

import com.icrisat.sbdm.ismu.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FolderSelectionPanel extends JPanel {

    JButton browseBtn;
    public JTextField fileName, folderPath;
    SharedInformation sharedInformation;
    NewProjectDialog newProjectDialog;

    FolderSelectionPanel(SharedInformation sharedInformation, NewProjectDialog newProjectDialog) {
        this.sharedInformation = sharedInformation;
        this.newProjectDialog = newProjectDialog;
        Font font = sharedInformation.getFont();
        setLayout(new GridBagLayout());
        setSize(new Dimension(400, 75));
        // -----------------------------Geno----------------------------------------
        JLabel fileNameLabel = new JLabel(" Project name ");
        fileNameLabel.setFont(font);

        fileName = new JTextField(24);

        JLabel folderPathDirectory = new JLabel("Location ");
        folderPathDirectory.setFont(font);

        folderPath = new JTextField(24);
        folderPath.setEditable(false);

        browseBtn = new JButton("  Browse  ");
        browseBtn.setFont(font);
        browseBtn.addActionListener(this::projectBrowseAction);

        Insets noInsets = new Insets(0, 0, 0, 0);
        Insets bottomInsets = new Insets(0, 0, 10, 0);
        Insets leftBottomInsets = new Insets(0, 10, 10, 0);
        Insets leftInsets = new Insets(0, 10, 0, 0);
        int x = 0;
        int y = 0;
        add(fileNameLabel, getGridBag(x, y, GridBagConstraints.FIRST_LINE_START, bottomInsets));

        x = x + 1;
        add(fileName, getGridBag(x, y, GridBagConstraints.LINE_END, leftBottomInsets));

        x = 0;
        y = y + 1;
        add(folderPathDirectory, getGridBag(x, y, GridBagConstraints.LINE_START, noInsets));

        x = x + 1;
        add(folderPath, getGridBag(x, y, GridBagConstraints.LINE_END, leftInsets));

        x = x + 1;
        add(browseBtn, getGridBag(x, y, GridBagConstraints.LINE_END, leftInsets));
    }

    private void projectBrowseAction(ActionEvent actionEvent) {
        String projName = fileName.getText().trim();
        if (projName.equals(""))
            Util.showMessageDialog("Please enter a valid project name");
        else {
            NativeJFileChooser fileChooser = Util.getFolderChooser("Select a folder to create project");
            if (fileChooser.showOpenDialog(sharedInformation.getMainFrame()) == JFileChooser.APPROVE_OPTION) {
                if (Files.isDirectory(Paths.get(fileChooser.getSelectedFile().toString()))) {
                    String directory = fileChooser.getSelectedFile().toString();
                    String projectPath = directory + "/" + projName;
                    new File(projectPath).mkdir();
                    folderPath.setText(directory);
                    setResultDir(projectPath);
                    browseBtn.setEnabled(false);
                } else {
                    // folder not exits
                    Util.showMessageDialog("Please select a valid folder");
                }
            }
        }
    }

    /**
     * Sets result dir.
     * Enables buttons to select genotype and phenotype files.
     */
    private void setResultDir(String resDir) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            resDir = resDir + "\\";
            sharedInformation.setOS(Constants.WINDOWS);
        } else {
            resDir = resDir + "/";
            sharedInformation.setOS(Constants.OTHEROS);
        }
        PathConstants.resultDirectory = resDir;
        newProjectDialog.setEnableGenoPhenoSelection(true);
        sharedInformation.getLogger().info("Project created");
    }

    private GridBagConstraints getGridBag(int x, int y, int anchor, Insets insets) {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = x;
        gridBagConstraints.gridy = y;
        gridBagConstraints.anchor = anchor;
        gridBagConstraints.insets = insets;
        return gridBagConstraints;
    }
}
