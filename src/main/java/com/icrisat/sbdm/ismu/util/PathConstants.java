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
    public String resultDirectory = null;
    public String tempResultDirectory = null;
    public String lastChosenFilePath = null;
    public String recentGenotypeFile = null;
    public boolean isFirstGenoFile = true;
    public boolean isFirstPhenoFile = true;
    public List<FileLocation> genotypeFiles = new ArrayList<>();
    public HashMap<String, String> summaryFilesMap = new HashMap<>();
    public String recentPhenotypeFile = null;
    public List<FileLocation> phenotypeFiles = new ArrayList<>();
    public List<FileLocation> resultFiles = new ArrayList<>();
    // Default engine is R unless changed.
    public String engine = "R";
    public boolean isBrapiCallPheno = false;
    public int noOfHeadersPheno = 0;
    public List<String> qualitativeTraits = new ArrayList<>();
}