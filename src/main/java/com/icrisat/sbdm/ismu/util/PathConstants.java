package com.icrisat.sbdm.ismu.util;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component("pathConstants")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PathConstants {
    public static String resultDirectory = null;
    public static String lastChosenFilePath = null;
    public static String recentGenotypeFile = null;
    public static String recentPhenotypeFile = null;
    public static List<FileLocation> genotypeFiles = new ArrayList<>();
    public static HashMap<String, String> summaryFilesMap = new HashMap<>();
    public static List<FileLocation> phenotypeFiles = new ArrayList<>();
    public static List<FileLocation> resultFiles = new ArrayList<>();

    public static void resetPathConstants() {
        resultDirectory = null;
        lastChosenFilePath = null;
        recentGenotypeFile = null;
        genotypeFiles = new ArrayList<>();
        summaryFilesMap = new HashMap<>();
        recentPhenotypeFile = null;
        phenotypeFiles = new ArrayList<>();
        resultFiles = new ArrayList<>();
    }

}