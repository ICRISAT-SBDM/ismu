package com.icrisat.sbdm.ismu.ui.mainFrame.project;

import com.icrisat.sbdm.ismu.ui.mainFrame.CreateMainFrameComponents;
import com.icrisat.sbdm.ismu.ui.mainFrame.DynamicTree;
import com.icrisat.sbdm.ismu.util.*;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.event.ActionEvent;
import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Component
public class Project {
    static final String ISMU_PROJECT_FILE = "proj.ismu";
    static final String GENOTYPE_FILES = "GENOTYPE_FILES";
    static final String PHENOTYPE_FILES = "PHENOTYPE_FILES";
    static final String OTHER_FILES = "OTHER_FILES";
    static final String IMAGES = "IMAGES";
    public static final String CSS = "ISMU.CSS";
    static final String CSS_FILE = "CSS";
    static final String PROJ_CORRUPTED = "CORRUPTED PROJECT. COULD NOT LOAD";
    public static final String SUMMARY_RELATION = "GENO SUMMARY RELATION";
    static final String END = "END";

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
    public void saveProject(ActionEvent e) {
        String status;
        // Result directory is not yet set. No need to save anything.
        if (PathConstants.resultDirectory != null) {
            int choice = JOptionPane.showConfirmDialog((java.awt.Component) e.getSource(), "\n" + "Do you want to save current project?",
                    "Save project", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                String filePath = PathConstants.resultDirectory + ISMU_PROJECT_FILE;
                status = deleteIfFileExists(filePath);
                if (status.equalsIgnoreCase(Constants.SUCCESS))
                    status = writeToProjectFile(filePath);
                Util.showMessageDialog(status);
            }
        }
    }

    public String saveProjWithoutConfirmation() {
        String status = Constants.SUCCESS;
        // Result directory is not yet set. No need to save anything.
        if (PathConstants.resultDirectory != null) {
            String filePath = PathConstants.resultDirectory + ISMU_PROJECT_FILE;
            status = deleteIfFileExists(filePath);
            if (status.equalsIgnoreCase(Constants.SUCCESS))
                status = writeToProjectFile(filePath);
            Util.showMessageDialog(status);
        }
        return status;
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
        NativeJFileChooser fileChooser = Util.getFolderChooser("Select project to open");
        int openOption = fileChooser.showOpenDialog((java.awt.Component) e.getSource());
        if (openOption == JFileChooser.APPROVE_OPTION) {
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
                Util.clearCurrentApplicationState(sharedInformation, dynamicTree);
                status = populateApplicationState(projDir);
                if (!status.equalsIgnoreCase(Constants.SUCCESS)) return status;
                addToTree();
            } else {
                // Folder does not exits
                status = "Selected folder does not exits.";
                return status;
            }
        } else if (openOption == JFileChooser.CANCEL_OPTION){
            status = "Cancelled opening project..";
        }
        return status;
    }

    public void newProject(ActionEvent e) {
        Util.clearCurrentApplicationState(sharedInformation, dynamicTree);
        new NewProjectDialog(sharedInformation, dynamicTree);
        if (PathConstants.resultDirectory != null) {
            CreateMainFrameComponents.setEnableComponenents(true);
            copyCSSFile();
        }
    }

    void copyCSSFile() {
        if (!new File(PathConstants.resultDirectory + CSS).exists()) {
            String cssPath = Util.getJarDirectory(this.getClass());
            cssPath = cssPath + "/doc/" + CSS;
            try {
                FileChannel inputFileChannel = new FileInputStream(new File(cssPath)).getChannel();
                FileChannel outputFileChannel = new FileOutputStream(new File(PathConstants.resultDirectory + CSS)).getChannel();
                outputFileChannel.transferFrom(inputFileChannel, 0, inputFileChannel.size());
                inputFileChannel.close();
                outputFileChannel.close();
            } catch (Exception ex) {
                sharedInformation.getLogger().error("ISSUE IN COPYING CSS.");
            }
        }
    }

    /**
     * Adds the file to the tree and displays it.
     */
    private void addToTree() {
        for (FileLocation file : PathConstants.genotypeFiles) {
            dynamicTree.addObject(dynamicTree.getGenotypeNode(), file, Boolean.TRUE);
        }
        for (FileLocation file : PathConstants.phenotypeFiles) {
            dynamicTree.addObject(dynamicTree.getPhenotypeNode(), file, Boolean.TRUE);
        }
        for (FileLocation file : PathConstants.resultFiles) {
            dynamicTree.addObject(dynamicTree.getResultsNode(), file, Boolean.TRUE);
        }
        //TODO: SHow msg that project loaded
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

        PathConstants.resultDirectory = projDir;
        PathConstants.lastChosenFilePath = projDir;
        PathConstants.genotypeFiles = genotypeFiles;
        PathConstants.phenotypeFiles = phenotypeFiles;
        relations.forEach((k, v) -> PathConstants.summaryFilesMap.put(k, v));
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
            PathConstants.resultFiles.add(file);
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

        if (!new File(projDir + CSS).exists()) {
            return "File " + CSS + " does not exist.";
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
        if (!Files.exists(Paths.get(projDir + ISMU_PROJECT_FILE))) {
            status = "Selected folder " + projDir + " does not contain project file.";
            return status;
        }
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(projDir + ISMU_PROJECT_FILE))) {
            String line = reader.readLine();
            if (line == null || !line.equals(GENOTYPE_FILES))
                return PROJ_CORRUPTED;
            if (!readGenoFiles(reader, projDir).equalsIgnoreCase(PHENOTYPE_FILES))
                return PROJ_CORRUPTED;
            if (!readPhenoFiles(reader, projDir).equalsIgnoreCase(OTHER_FILES))
                return PROJ_CORRUPTED;
            if (!readOtherFiles(reader, projDir).equalsIgnoreCase(IMAGES))
                return PROJ_CORRUPTED;

            if (!readImageFiles(reader, projDir).equalsIgnoreCase(SUMMARY_RELATION)) {
                return PROJ_CORRUPTED;
            }
            // Map geno files and its summary.
            if (!readRelations(reader, projDir).equalsIgnoreCase(CSS_FILE)) {
                return PROJ_CORRUPTED;
            }

            if (!reader.readLine().equals(CSS)) return PROJ_CORRUPTED;
            if (!reader.readLine().equals(END)) return PROJ_CORRUPTED;
            return Constants.SUCCESS;
        } catch (Exception e) {
            status = e.toString();
            if (status.equalsIgnoreCase("java.lang.NullPointerException"))
                status = PROJ_CORRUPTED;
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
        while (line != null && !line.equals(PHENOTYPE_FILES)) {
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
        while (line != null && !line.equals(OTHER_FILES)) {
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
        while (line != null && !line.equals(IMAGES)) {
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
        while (line != null && !line.equals(SUMMARY_RELATION)) {
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
        while (line != null && !line.equals(CSS_FILE)) {
            String[] split = line.split("@@@");
            relations.put(split[0], split[1]);
            line = reader.readLine();
        }
        return line;
    }

    /**
     * Saves the project contents in file specified by filepath
     *
     * @param filePath File path
     */
    private String writeToProjectFile(String filePath) {
        final String[] status = new String[1];
        genotypeFiles = PathConstants.genotypeFiles;
        phenotypeFiles = PathConstants.phenotypeFiles;
        resultFiles = PathConstants.resultFiles;
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath))) {
            writer.write(GENOTYPE_FILES);
            writer.newLine();
            for (FileLocation genotypeFile : genotypeFiles) {
                writer.write(genotypeFile.getFileNameInApplication());
                writer.newLine();
            }
            writer.write(PHENOTYPE_FILES);
            writer.newLine();
            for (FileLocation phenotypeFile : phenotypeFiles) {
                writer.write(phenotypeFile.getFileNameInApplication());
                writer.newLine();
            }
            writer.write(OTHER_FILES);
            writer.newLine();
            for (FileLocation otherFile : resultFiles) {
                writer.write(otherFile.getFileNameInApplication());
                writer.newLine();
            }
            writer.write(IMAGES);
            writer.newLine();
            status[0] = writeRequiredImages(writer);
            if (!status[0].equalsIgnoreCase(Constants.SUCCESS)) return status[0];
            writer.write(SUMMARY_RELATION);
            writer.newLine();
            final String[] sumStatus = {Constants.SUCCESS};
            PathConstants.summaryFilesMap.forEach((String k, String v) -> {
                try {
                    writer.write(k + "@@@" + v);
                    writer.newLine();
                } catch (IOException e) {
                    sumStatus[0] = e.getMessage();
                }
            });
            if (!sumStatus[0].equals(Constants.SUCCESS)) return sumStatus[0];

            DefaultMutableTreeNode childAt = (DefaultMutableTreeNode) dynamicTree.getLogNode().getChildAt(0);
            FileLocation logFileSource = (FileLocation) childAt.getUserObject();
            Path source = Paths.get(logFileSource.getFileLocationOnDisk());
            Path destination = Paths.get(PathConstants.resultDirectory + logFileSource.getFileNameInApplication());
            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            writer.write(CSS_FILE);
            writer.newLine();
            writer.write(CSS);
            writer.newLine();
            writer.write(END);
            status[0] = "Project saved successfully";
        } catch (Exception e1) {
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
        List<Path> files = Files.list(Paths.get(PathConstants.resultDirectory))
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
