package com.icrisat.sbdm.ismu.ui.mainFrame;

import com.icrisat.sbdm.ismu.ui.analysis.Analysis;
import com.icrisat.sbdm.ismu.ui.components.ColumnSelection;
import com.icrisat.sbdm.ismu.ui.dataSummary.DataSummary;
import com.icrisat.sbdm.ismu.ui.mainFrame.ActionalListeners.GenoPhenoPanel;
import com.icrisat.sbdm.ismu.ui.openDialog.components.GenotypeDB;
import com.icrisat.sbdm.ismu.ui.openDialog.components.PhenotypeDB;
import com.icrisat.sbdm.ismu.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class MainFrameActionListeners {

    private SharedInformation sharedInformation;
    private DynamicTree dynamicTree;
    private DataSummary dataSummary;
    private Analysis analysis;
    private ColumnSelection columnSelection;
    private Project project;
    private PdfConverter pdfConverter;
    private PhenotypeDB phenotypeDB;
    private GenotypeDB genotypeDB;

    @Autowired
    public void setGenotypeDB(GenotypeDB genotypeDB) {
        this.genotypeDB = genotypeDB;
    }

    @Autowired
    public void setPhenotypeDB(PhenotypeDB phenotypeDB) {
        this.phenotypeDB = phenotypeDB;
    }

    @Autowired
    public void setPdfConverter(PdfConverter pdfConverter) {
        this.pdfConverter = pdfConverter;
    }

    @Autowired
    public void setProject(Project project) {
        this.project = project;
    }

    @Autowired
    public void setColumnSelection(ColumnSelection columnSelection) {
        this.columnSelection = columnSelection;
    }

    @Autowired
    public void setAnalysis(Analysis analysis) {
        this.analysis = analysis;
    }

    @Autowired
    public void setDynamicTree(DynamicTree dynamicTree) {
        this.dynamicTree = dynamicTree;
    }

    @Autowired
    public void setSharedInformation(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
    }

    @Autowired
    public void setDataSummary(DataSummary dataSummary) {
        this.dataSummary = dataSummary;
    }

    /**
     * Action item to select a Genotype file.
     *
     * @param e Action Event
     */
    void selectGenotypeFileActionItem(ActionEvent e) {
        new GenoPhenoPanel(sharedInformation, dynamicTree, Constants.GENO);
        // genoPanel.btnConnect.addActionListener(ae -> genoConnectAction(ae, genoPanel, dialogBox));
    }

    private void genoConnectAction(ActionEvent ae, GenoPhenoPanel genoPanel, JDialog dialogBox) {
        genotypeDB.setVisible(true);
        String genofile = genoPanel.txtbox.getText();
        //   addPanelTo(genofile, Constants.GENO, false);
        genotypeDB.setVisible(false);
        dialogBox.setVisible(false);
    }

    /**
     * Action item for Phenotype file.
     *
     * @param e Action Event
     */
    void selectPhenotypeFileActionItem(ActionEvent e) {
        new GenoPhenoPanel(sharedInformation, dynamicTree, Constants.PHENO);
    }

    private void phenoConnectAction(ActionEvent ae, GenoPhenoPanel phenoPanel, JDialog dialogBox) {
        phenotypeDB.setVisible(true);
//        pathConstants.isBrapiCallPheno = true;
        String phenofile = phenoPanel.txtbox.getText();
        if (phenofile.equals("")) return;
        //  addPanelTo(phenofile, Constants.PHENO, true);
        phenotypeDB.setVisible(false);
        dialogBox.setVisible(false);
    }

    /**
     * Data summary action item.
     *
     * @param e Action Event
     */
    void dataSummaryActionItem(ActionEvent e) {
        if (PathConstants.genotypeFiles.size() > 0 || PathConstants.phenotypeFiles.size() > 0) {
            dataSummary.setVisible(true);
        } else {
            Util.showMessageDialog("Import at least one Genotype and Phenotype files");
        }
    }

    /**
     * Analysis action item.
     *
     * @param e Action Event
     */
    void analysisActionItem(ActionEvent e) {
        if (PathConstants.genotypeFiles.size() > 0 && PathConstants.phenotypeFiles.size() > 0) {
            analysis.setVisible();
        } else {
            Util.showMessageDialog("Import at least one Genotype and Phenotype files");
        }
    }

    /**
     * Saves current project
     *
     * @param e Action Event
     */
    void saveProjectActionItem(ActionEvent e) {
        String status = project.saveProject();
        if (status.equalsIgnoreCase(Constants.SUCCESS))
            Util.showMessageDialog("Project saved successfully");
        else
            Util.showMessageDialog(status);
    }

    /**
     * Opens a saved project.
     * To handle switching the projects, user is prompted to save current project
     *
     * @param e Action Event
     */
    void openProjectActionItem(ActionEvent e) {
        String status;
        status = project.saveProject();
        if (status.equalsIgnoreCase(Constants.SUCCESS)) status = project.openProject(e);
        else {
            int choice = JOptionPane.showConfirmDialog((java.awt.Component) e.getSource(), status + "\n" + "Do you want to continue without saving current project?",
                    "Open project", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) status = project.openProject(e);
            else return;
        }
        if (!status.equalsIgnoreCase(Constants.SUCCESS))
            Util.showMessageDialog(status);
    }

    /**
     * Opens a new project.
     * To handle switching the projects, user is prompted to save current project
     *
     * @param e Action event
     */
    void newProjectActionItem(ActionEvent e) {
        String status = project.saveProject();
        if (status.equalsIgnoreCase(Constants.SUCCESS)) project.newProject(e);
        else {
            int choice = JOptionPane.showConfirmDialog(sharedInformation.getMainFrame(), status + "\n" + "Do you want to continue without saving current project?",
                    "New project", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) project.newProject(e);
        }
    }

    /**
     * Exit the application
     *
     * @param e Action Event
     */
    void exitApplicationActionItem(ActionEvent e) {
        if (Util.closeApplication(e, project)) sharedInformation.getMainFrame().dispose();
    }

    /**
     * open save dialog box in which we can specify required file name and directory.
     * if file extension is  html then it convert to pdf
     * if file file extension is .csv then path to the file is displayed.
     * Example http://www.baeldung.com/pdf-conversions-java
     */
    void saveAsActionItem(ActionEvent e) {
        ClosableTabbedPane tabbedPane = sharedInformation.getTabbedPane();
        String selectedFile = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
        selectedFile = selectedFile.trim();
        String filePath = getFilePath(selectedFile);
        if (filePath != null) {
            NativeJFileChooser pdfFileChooser = Util.getFolderChooser("Select a folder to save the file");
            if (pdfFileChooser.showOpenDialog((java.awt.Component) e.getSource()) == JFileChooser.APPROVE_OPTION) {
                String folder = pdfFileChooser.getSelectedFile().toString();
                String savedFileName = folder + "/" + Util.stripFileExtension(selectedFile) + ".pdf";
                pdfConverter.convertToPdf(e, filePath, savedFileName, sharedInformation);
            }
        } /*else if (ext.equalsIgnoreCase(Constants.CSV)) {
            NativeJFileChooser csvFileChooser = Util.getFolderChooser("Select a folder to save the file", "CSV");
            if (csvFileChooser.showOpenDialog((java.awt.Component) e.getSource()) == JFileChooser.APPROVE_OPTION) {
                String savedFileName = csvFileChooser.getSelectedFile().toString();
                if (!savedFileName.endsWith(Constants.CSV)) savedFileName = savedFileName + ".csv";
                List<FileLocation> genotypeFiles = sharedInformation.getPathConstants().genotypeFiles;
                List<FileLocation> phenotypeFiles = sharedInformation.getPathConstants().phenotypeFiles;
                List<FileLocation> resultFiles = sharedInformation.getPathConstants().resultFiles;
                String status = "";
                for (FileLocation file : genotypeFiles) {
                    if (file.getFileNameInApplication().equalsIgnoreCase(selectedFile))
                        status = Util.copyFile(file.getFileLocationOnDisk(), savedFileName);
                }
                for (FileLocation file : phenotypeFiles) {
                    if (file.getFileNameInApplication().equalsIgnoreCase(selectedFile))
                        status = Util.copyFile(file.getFileLocationOnDisk(), savedFileName);
                }

                for (FileLocation file : resultFiles) {
                    if (file.getFileNameInApplication().equalsIgnoreCase(selectedFile))
                        status = Util.copyFile(file.getFileLocationOnDisk(), savedFileName);
                }
                if (status.equalsIgnoreCase(Constants.SUCCESS))
                    JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), "File saved in location \n"
                            + savedFileName);
                else
                    JOptionPane.showMessageDialog((java.awt.Component) e.getSource(), status);
            }
        } */ else {
            Util.showMessageDialog("Only HTML files can be saved to PDF.");
        }
    }

    private String getFilePath(String selectedFile) {
        if (PathConstants.resultDirectory == null) return null;
        try {
            DirectoryStream<Path> htm = Files.newDirectoryStream(Paths.get(PathConstants.resultDirectory), "*.htm");
            for (Path entry : htm) {
                if (entry.getFileName().toString().equalsIgnoreCase(selectedFile))
                    return entry.toString();
            }
        } catch (IOException e) {
            return null;

        }
        return null;
    }


}

