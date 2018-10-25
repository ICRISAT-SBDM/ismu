package com.icrisat.sbdm.ismu.util;

import com.sun.javafx.application.PlatformImpl;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Component
public class UtilHTML {
    private static SharedInformation sharedInformation;

    @Autowired
    public void setSharedConstants(SharedInformation sharedInformation) {
        UtilHTML.sharedInformation = sharedInformation;
    }

    /**
     * Appends file:/// before the start of image location.
     * Changes the file in file location object to new file
     *
     * @param fileLocation file location
     */
    public static String editHTML2DisplayImages(FileLocation fileLocation, String originalText, String replaceMentText) {
        String status = Constants.SUCCESS;
        String sourceFile = fileLocation.getFileLocationOnDisk();
        String outputFile = Util.stripFileExtension(sourceFile) + "-1." + Util.getFileExtension(sourceFile);

        try (BufferedReader br = Files.newBufferedReader(Paths.get(sourceFile));
             BufferedWriter bw = Files.newBufferedWriter(Paths.get(outputFile))) {
            String line;StringBuilder oldText = new StringBuilder();
            while ((line = br.readLine()) != null) {
                oldText.append(line).append("\r\n");
            }
            String newText = oldText.toString().replace(originalText, replaceMentText);
            bw.write(newText);
        } catch (IOException e) {
            sharedInformation.getLogger().error(e.getMessage() + e.getStackTrace());
            status = e.getMessage();
        }
        try {
            Files.move(Paths.get(outputFile), Paths.get(sourceFile), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    /**
     * Display HTML files
     *
     * @param genoHtmlFileLocation HTML File location
     */
    public static void displayHTMLFile(FileLocation genoHtmlFileLocation) {
        JFXPanel jfxPanel = new JFXPanel();
        jfxPanel.setName(genoHtmlFileLocation.getFileNameInApplication()+"   ");
        PlatformImpl.setImplicitExit(false);
        PlatformImpl.startup(() -> {
            Scene scene;
            scene = new Scene(new Browser(genoHtmlFileLocation), 900, 600, Color.web("#666970"));
            jfxPanel.setScene(scene);
        });
        sharedInformation.getTabbedPane().add(jfxPanel);
        sharedInformation.getTabbedPane().setSelectedIndex(sharedInformation.getTabbedPane().getTabCount() - 1);
    }

    /**
     * Adds how long it took to compute the result.
     *
     * @param htmlPath  FIle path
     * @param timeTaken Time taken
     * @return status
     */
    public static String addingProcessTime2HTMlFileSummary(String htmlPath, String timeTaken) {
        Logger logger = sharedInformation.getLogger();
        String status = Constants.SUCCESS;
        int noOfLines = Util.noOfLinesInFile(htmlPath);
        if (noOfLines < 0) {
            status = "Cannot display " + htmlPath + " file./n Please check whether the file exists or not.";
            return status;
        }
        try {
            List<String> lines = Files.readAllLines(Paths.get(htmlPath), StandardCharsets.UTF_8);
            String charCountOfLine = lines.get(noOfLines - 2);
            StringBuilder sb = new StringBuilder(lines.get(noOfLines - 2));
            int length = charCountOfLine.length() - 4;
            sb.insert(length, timeTaken);
            lines.remove(noOfLines - 2);
            lines.add(noOfLines - 2, sb.toString());
            Files.write(Paths.get(htmlPath), lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            logger.warn(ex.getMessage());
            status = ex.getMessage();
        }
        return status;
    }
}
