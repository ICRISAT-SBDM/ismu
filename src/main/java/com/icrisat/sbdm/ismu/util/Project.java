package com.icrisat.sbdm.ismu.util;

import com.icrisat.sbdm.ismu.ui.mainFrame.DynamicTree;
import com.icrisat.sbdm.ismu.ui.openDialog.OpenDialog;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component
public class Project {

    private SharedInformation sharedInformation;
    private List<FileLocation> genotypeFiles, phenotypeFiles, resultFiles, imageFiles;
    private Map<String, String> relations;
    private DynamicTree dynamicTree;

    @Autowired
    public Project(SharedInformation sharedInformation, DynamicTree dynamicTree) {
        this.sharedInformation = sharedInformation;
        this.dynamicTree = dynamicTree;
    }

    /**
     * Save the project
     */
    public String saveProject() {
        String status;
        // Result directory is not yet set. So no processing happened. No need to save anything.
        if (sharedInformation.getPathConstants().resultDirectory == null) {
            //"No need to save.";
            return Constants.SUCCESS;
        }
        String filePath = sharedInformation.getPathConstants().resultDirectory + Constants.ISMU_PROJECT_FILE;
        status = deleteIfFileExists(filePath);
        if (!status.equalsIgnoreCase(Constants.SUCCESS)) {
            return status;
        }
        return writeToProjectFile(filePath);
    }

    /**
     * Opens a saved project.
     * Steps
     * 1. Check that the proj file exists and read it.
     * 2. Close all the open Panels.
     *
     * @param e Action Event
     * @return status message
     */
    public String openProject(ActionEvent e) {
        String status = Constants.SUCCESS;
        NativeJFileChooser fileChooser = Util.getFolderChooser("Select folder with saved project");
        if (fileChooser.showOpenDialog((java.awt.Component) e.getSource()) == JFileChooser.APPROVE_OPTION) {
            if (Files.isDirectory(Paths.get(fileChooser.getSelectedFile().toString()))) {
                String projDir = fileChooser.getSelectedFile().toString();
                String os = System.getProperty("os.name").toLowerCase();
                if (os.contains("win")) {
                    projDir = projDir + "\\";
                } else {
                    projDir = projDir + "/";
                }
                status = readProjFile(projDir);
                if (!status.equalsIgnoreCase(Constants.SUCCESS)) return status;
                status = processProjFile(projDir);
                if (!status.equalsIgnoreCase(Constants.SUCCESS)) return status;
                clearCurrentApplicationState();
                status = populateApplicationState(projDir);
                if (!status.equalsIgnoreCase(Constants.SUCCESS)) return status;
                addToTree();
            } else {
                // Folder does not exits
                status = "Selected folder does not exits.";
                return status;
            }
        }
        return status;
    }

    public void newProject(ActionEvent e) {
        clearCurrentApplicationState();
        OpenDialog openDialog = sharedInformation.getOpenDialog();
        PathConstants pathConstants = sharedInformation.getPathConstants();
        openDialog.getTxtResultDir().setText("");
        openDialog.actionResultDir(e);
        if (pathConstants.tempResultDirectory != null) {
            openDialog.getResultPanel().txtResult.setText(pathConstants.tempResultDirectory);
            pathConstants.resultDirectory = pathConstants.tempResultDirectory;
            openDialog.getResultBrowseBtn().setEnabled(false);
            Logger log = Util.createLogger(Project.class, getClass().getResource("/logback.xml"));
            log.info("Logger restarted");
        }
    }

    /**
     * Adds the file to the tree and displays it.
     */
    private void addToTree() {
        for (FileLocation file : sharedInformation.getPathConstants().genotypeFiles) {
            dynamicTree.addObject(dynamicTree.getGenotypeNode(), file, Boolean.TRUE);
            UtilCSV.addCSVToTabbedPanel(file, true);
        }
        for (FileLocation file : sharedInformation.getPathConstants().phenotypeFiles) {
            dynamicTree.addObject(dynamicTree.getPhenotypeNode(), file, Boolean.TRUE);
            UtilCSV.addCSVToTabbedPanel(file, false);
        }
        for (FileLocation file : sharedInformation.getPathConstants().resultFiles) {
            dynamicTree.addObject(dynamicTree.getResultsNode(), file, Boolean.TRUE);
            if (file.getFileNameInApplication().endsWith(Constants.HTM)) UtilHTML.displayHTMLFile(file);
            else UtilCSV.addCSVToTabbedPanel(file, false);
        }
    }

    /**
     * Populates the application state
     *
     * @param projDir project directory
     */
    private String populateApplicationState(String projDir) {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) sharedInformation.setOS(Constants.WINDOWS);
        else sharedInformation.setOS(Constants.OTHEROS);

        PathConstants pathConstants = sharedInformation.getPathConstants();
        pathConstants.resultDirectory = projDir;
        pathConstants.tempResultDirectory = projDir;

        Logger log = Util.createLogger(Project.class, getClass().getResource("/logback.xml"));
        log.info("Logger restarted");
        sharedInformation.getOpenDialog().getTxtResultDir().setText(projDir);
        sharedInformation.getOpenDialog().getResultBrowseBtn().setEnabled(false);

        pathConstants.lastChosenFilePath = projDir;
        pathConstants.genotypeFiles = genotypeFiles;
        pathConstants.phenotypeFiles = phenotypeFiles;
        relations.forEach((k, v) -> {
            pathConstants.summaryFilesMap.put(k, v);
        });
        String imageDirectory = null;
        for (FileLocation file : resultFiles) {
            if (file.getFileLocationOnDisk().endsWith(Constants.HTM)) {
                if (imageDirectory == null) {
                    try (BufferedReader br = Files.newBufferedReader(Paths.get(file.getFileLocationOnDisk()))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            if (line.contains("src='file")) {
                                imageDirectory = line.substring(line.indexOf("src='file") + 13, line.indexOf("border") - 2);
                                imageDirectory = new File(imageDirectory).getParent();
                                if (sharedInformation.getOS().equalsIgnoreCase(Constants.WINDOWS))
                                    imageDirectory += "\\";
                                else imageDirectory += "/";
                                imageDirectory = imageDirectory.replace("\\", "/");
                                break;
                            }
                        }
                    } catch (IOException e) {
                        sharedInformation.getLogger().error(e.getMessage() + e.getStackTrace());
                        return "HTML file " + file.getFileLocationOnDisk() + " is corrupted.";
                    }
                }
                UtilHTML.editHTML2DisplayImages(file, imageDirectory, projDir);
            }
            pathConstants.resultFiles.add(file);
        }
        return Constants.SUCCESS;
    }

    /**
     * Process proj file. It checks all the files that are required are in existence.
     *
     * @param projDir project directory
     * @return status
     */
    private String processProjFile(String projDir) {
        for (FileLocation genoFile : genotypeFiles) {
            if (!new File(genoFile.getFileLocationOnDisk()).exists()) {
                return "File " + genoFile.getFileLocationOnDisk() + " does not exist.";
            }
        }
        for (FileLocation phenoFile : phenotypeFiles) {
            if (!new File(phenoFile.getFileLocationOnDisk()).exists()) {
                return "File " + phenoFile.getFileLocationOnDisk() + " does not exist.";
            }
        }
        for (FileLocation otherFile : resultFiles) {
            if (!new File(otherFile.getFileLocationOnDisk()).exists()) {
                return "File " + otherFile.getFileLocationOnDisk() + " does not exist.";
            }
        }
        for (FileLocation imageFile : imageFiles) {
            if (!new File(imageFile.getFileLocationOnDisk()).exists()) {
                return "File " + imageFile.getFileLocationOnDisk() + " does not exist.";
            }
        }

        if (!new File(projDir + Constants.CSS).exists()) {
            return "File " + Constants.CSS + " does not exist.";
        }

        return Constants.SUCCESS;
    }

    /**
     * Reads proj file. This ensures that file is in correct order doesn't care about existence of files.
     *
     * @param projDir project directory
     * @return status
     */
    private String readProjFile(String projDir) {
        String status;
        if (!Files.exists(Paths.get(projDir + Constants.ISMU_PROJECT_FILE))) {
            status = "Selected folder " + projDir + " does not contain project file.";
            return status;
        }
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(projDir + Constants.ISMU_PROJECT_FILE))) {
            String line = reader.readLine();
            if (line == null || !line.equals(Constants.GENOTYPE_FILES))
                return Constants.PROJ_CORRUPTED;
            if (!readGenoFiles(reader, projDir).equalsIgnoreCase(Constants.PHENOTYPE_FILES))
                return Constants.PROJ_CORRUPTED;
            if (!readPhenoFiles(reader, projDir).equalsIgnoreCase(Constants.OTHER_FILES))
                return Constants.PROJ_CORRUPTED;
            if (!readOtherFiles(reader, projDir).equalsIgnoreCase(Constants.IMAGES))
                return Constants.PROJ_CORRUPTED;

            if (!readImageFiles(reader, projDir).equalsIgnoreCase(Constants.SUMMARY_RELATION)) {
                return Constants.PROJ_CORRUPTED;
            }
            // Map geno files and its summary.
            if (!readRelations(reader, projDir).equalsIgnoreCase(Constants.CSS_FILE)) {
                return Constants.PROJ_CORRUPTED;
            }

            if (!reader.readLine().equals(Constants.CSS)) return Constants.PROJ_CORRUPTED;
            if (!reader.readLine().equals(Constants.END)) return Constants.PROJ_CORRUPTED;
            return Constants.SUCCESS;
        } catch (Exception e) {
            status = e.toString();
            if (status.equalsIgnoreCase("java.lang.NullPointerException"))
                status = Constants.PROJ_CORRUPTED;
            return status;
        }
    }

    /**
     * Read till we encounter empty line or PHENOTYPE_FILE
     *
     * @param reader  file reader
     * @param projDir project directory
     * @return status
     * @throws IOException IO exception
     */
    private String readGenoFiles(BufferedReader reader, String projDir) throws IOException {
        String line = reader.readLine();
        genotypeFiles = new ArrayList<>();
        while (line != null && !line.equals(Constants.PHENOTYPE_FILES)) {
            genotypeFiles.add(new FileLocation(line, projDir + line));
            line = reader.readLine();
        }
        return line;
    }

    /**
     * Read till we encounter empty line or other files
     *
     * @param reader  file reader
     * @param projDir project directory
     * @return status
     * @throws IOException IO exception
     */
    private String readPhenoFiles(BufferedReader reader, String projDir) throws IOException {
        String line = reader.readLine();
        phenotypeFiles = new ArrayList<>();
        while (line != null && !line.equals(Constants.OTHER_FILES)) {
            phenotypeFiles.add(new FileLocation(line, projDir + line));
            line = reader.readLine();
        }
        return line;
    }

    /**
     * @param reader  file reader
     * @param projDir project directory
     * @return status status
     * @throws IOException exception
     */
    private String readOtherFiles(BufferedReader reader, String projDir) throws IOException {
        String line = reader.readLine();
        resultFiles = new ArrayList<>();
        while (line != null && !line.equals(Constants.IMAGES)) {
            resultFiles.add(new FileLocation(line, projDir + line));
            line = reader.readLine();
        }
        return line;
    }

    /**
     * @param reader  file reader
     * @param projDir project directory
     * @return status status
     * @throws IOException exception
     */
    private String readImageFiles(BufferedReader reader, String projDir) throws IOException {
        String line = reader.readLine();
        imageFiles = new ArrayList<>();
        while (line != null && !line.equals(Constants.SUMMARY_RELATION)) {
            imageFiles.add(new FileLocation(line, projDir + line));
            line = reader.readLine();
        }
        return line;
    }

    /**
     * @param reader  file reader
     * @param projDir project directory
     * @return status status
     * @throws IOException exception
     */
    private String readRelations(BufferedReader reader, String projDir) throws IOException {
        String line = reader.readLine();
        relations = new HashMap<>();
        while (line != null && !line.equals(Constants.CSS_FILE)) {
            String[] split = line.split("@@@");
            relations.put(split[0], split[1]);
            line = reader.readLine();
        }
        return line;
    }


    /**
     * Cleans the current application state;
     * 1. clear dynamic tree.
     * 2. Shared information
     * 3. Path constants
     */
    private void clearCurrentApplicationState() {
        dynamicTree.remove(dynamicTree.getRootNode(), false);
        sharedInformation.setOS(null);
        PathConstants pathConstants = sharedInformation.getPathConstants();
        pathConstants.resultDirectory = null;
        pathConstants.tempResultDirectory = null;
        pathConstants.lastChosenFilePath = null;
        pathConstants.recentGenotypeFile = null;
        pathConstants.isFirstGenoFile = true;
        pathConstants.isFirstPhenoFile = true;
        pathConstants.genotypeFiles = new ArrayList<>();
        pathConstants.summaryFilesMap = new HashMap<>();
        pathConstants.recentPhenotypeFile = null;
        pathConstants.phenotypeFiles = new ArrayList<>();
        pathConstants.resultFiles = new ArrayList<>();
        // Default engine is R unless changed.
        pathConstants.engine = "R";
        pathConstants.isBrapiCallPheno = false;
    }

    /**
     * Saves the project contents in file specified by filepath
     *
     * @param filePath File path
     */
    private String writeToProjectFile(String filePath) {
        final String[] status = new String[1];
        genotypeFiles = sharedInformation.getPathConstants().genotypeFiles;
        phenotypeFiles = sharedInformation.getPathConstants().phenotypeFiles;
        resultFiles = sharedInformation.getPathConstants().resultFiles;
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            writer.write(Constants.GENOTYPE_FILES);
            writer.newLine();
            for (FileLocation genotypeFile : genotypeFiles) {
                writer.write(genotypeFile.getFileNameInApplication());
                writer.newLine();
            }
            writer.write(Constants.PHENOTYPE_FILES);
            writer.newLine();
            for (FileLocation phenotypeFile : phenotypeFiles) {
                writer.write(phenotypeFile.getFileNameInApplication());
                writer.newLine();
            }
            writer.write(Constants.OTHER_FILES);
            writer.newLine();
            for (FileLocation otherFile : resultFiles) {
                writer.write(otherFile.getFileNameInApplication());
                writer.newLine();
            }
            writer.write(Constants.IMAGES);
            writer.newLine();
            status[0] = writeRequiredImages(writer);
            if (!status[0].equalsIgnoreCase(Constants.SUCCESS)) return status[0];
            writer.write(Constants.SUMMARY_RELATION);
            writer.newLine();
            final String[] sumStatus = {Constants.SUCCESS};
            sharedInformation.getPathConstants().summaryFilesMap.forEach((String k, String v) -> {
                try {
                    writer.write(k + "@@@" + v);
                    writer.newLine();
                } catch (IOException e) {
                    sumStatus[0] = e.getMessage();
                }
            });
            if (!sumStatus[0].equals(Constants.SUCCESS)) return sumStatus[0];
            writer.write(Constants.CSS_FILE);
            writer.newLine();
            writer.write(Constants.CSS);
            writer.newLine();
            writer.write(Constants.END);
            status[0] = Constants.SUCCESS;
        } // the file will be automatically closed
        catch (Exception e1) {
            status[0] = e1.getMessage();
        }
        return status[0];
    }

    /**
     * Reads the HTML files and identifies required images.
     * If an image is deleted corresponding status is returned.
     *
     * @param writer file writer
     * @return status
     * @throws IOException exception
     */
    private String writeRequiredImages(BufferedWriter writer) throws IOException {
        imageFiles = new ArrayList<>();
        for (FileLocation file : resultFiles) {
            if (Util.getFileExtension(file.getFileNameInApplication()).equalsIgnoreCase(Constants.HTM)) {
                try (BufferedReader br = Files.newBufferedReader(Paths.get(file.getFileLocationOnDisk()))) {
                    String line, imageName;
                    while ((line = br.readLine()) != null) {
                        if (line.contains("src='file")) {
                            imageName = line.substring(line.indexOf("src='file") + 13, line.indexOf("border") - 2);
                            imageFiles.add(new FileLocation(new File(imageName).getName(), imageName));
                        }
                    }
                } catch (IOException e) {
                    sharedInformation.getLogger().error(e.getMessage() + e.getStackTrace());
                    throw e;
                }
            }
        }
        List<Path> files = Files.list(Paths.get(sharedInformation.getPathConstants().resultDirectory))
                // .filter(Files::isRegularFile)
                .collect(toList());
        for (FileLocation image : imageFiles) {
            if (files.contains(Paths.get(image.getFileLocationOnDisk()))) {
                writer.write(image.getFileNameInApplication());
                writer.newLine();
            } else {
                return "Image " + image + " deleted.";
            }
        }
        return Constants.SUCCESS;
    }

    /**
     * Deletes if the file exists.
     *
     * @param filePath file path
     */
    private String deleteIfFileExists(String filePath) {
        String status = Constants.SUCCESS;
        Logger logger = sharedInformation.getLogger();
        if (new File(filePath).exists()) {
            logger.info("File " + filePath + " Exists.");
            try {
                Files.delete(Paths.get(filePath));
                logger.info("Deleted " + filePath);
            } catch (IOException e1) {
                logger.error("Could not delete the file." + e1.getMessage());
                status = "Could not delete existing project file.\n Please check log for further details.";
            }
        }
        return status;
    }
}
