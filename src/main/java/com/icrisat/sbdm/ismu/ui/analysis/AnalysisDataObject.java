package com.icrisat.sbdm.ismu.ui.analysis;

import com.icrisat.sbdm.ismu.util.SharedInformation;
import com.icrisat.sbdm.ismu.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
@PropertySource("classpath:configuration.properties")
@Scope("singleton")
public class AnalysisDataObject {
    @Value("${rEngine.relativePath}")
    private String rEngineRelativePath;
    @Value("${scripts.relativeLocation}")
    private String scriptsRelativePath;
    @Value("${rScript.a}")
    private String aScriptName;
    @Value("${rScript.ab}")
    private String abScriptName;
    @Value("${rScript.b}")
    private String bScriptName;
    @Value("${rScript.c}")
    private String cScriptName;
    private SharedInformation sharedInformation;
    private String genoFile, phenoFile;
    // No of methods.
    //Order RidgeRegression BLUP  - R, Bayes CPI, BayesB, BayesLASSO, RandomForest, Kinship Gauss, RidgeRegressionBLUP, BayesA
    private List<Integer> methodsSelected;
    private List<String> phenoTraits;
    private String percentMissingMarkers;
    private String PICValue;
    private String MAF;
    private String bayesRounds;
    private String bayesBurning;
    private String bayesThinning;
    private String forests;
    private String cores;
    private String replication;
    private String fold;
    private String resultFileName, resultFileStub;
    private String enginePath, scriptsPath, aScriptPath, abScriptPath, bScriptPath, cScriptPath;

    @Autowired
    public void setSharedInformation(SharedInformation sharedInformation) {
        this.sharedInformation = sharedInformation;
    }

    public String getaScriptPath() {
        return aScriptPath;
    }

    public String getabScriptPath() {
        return abScriptPath;
    }

    public String getbScriptPath() {
        return bScriptPath;
    }

    public String getcScriptPath() {
        return cScriptPath;
    }

    public String getScriptsPath() {
        return scriptsPath;
    }

    public String getGenoFile() {
        return genoFile;
    }

    public String getPhenoFile() {
        return phenoFile;
    }

    public List<Integer> getMethodsSelected() {
        return methodsSelected;
    }

    public List<String> getPhenoTraits() {
        return phenoTraits;
    }

    public String getPercentMissingMarkers() {
        return percentMissingMarkers;
    }

    public void setPercentMissingMarkers(String percentMissingMarkers) {
        this.percentMissingMarkers = percentMissingMarkers;
    }

    public String getPICValue() {
        return PICValue;
    }

    public void setPICValue(String PICValue) {
        this.PICValue = PICValue;
    }

    public String getMAF() {
        return MAF;
    }

    public void setMAF(String MAF) {
        this.MAF = MAF;
    }

    public String getBayesRounds() {
        return bayesRounds;
    }

    public void setBayesRounds(String bayesRounds) {
        this.bayesRounds = bayesRounds;
    }

    public String getBayesBurning() {
        return bayesBurning;
    }

    public void setBayesBurning(String bayesBurning) {
        this.bayesBurning = bayesBurning;
    }

    public String getBayesThinning() {
        return bayesThinning;
    }

    public void setBayesThinning(String bayesThinning) {
        this.bayesThinning = bayesThinning;
    }

    public String getForests() {
        return forests;
    }

    public void setForests(String forests) {
        this.forests = forests;
    }

    public String getCores() {
        return cores;
    }

    public void setCores(String cores) {
        this.cores = cores;
    }

    public String getReplication() {
        return replication;
    }

    public void setReplication(String replication) {
        this.replication = replication;
    }

    public String getFold() {
        return fold;
    }

    public void setFold(String fold) {
        this.fold = fold;
    }

    public String getResultFileName() {
        return resultFileName;
    }

    public void setResultFileName(String resultFileName) {
        this.resultFileName = resultFileName;
    }

    public String getResultFileStub() {
        return resultFileStub;
    }

    public String getEnginePath() {
        return enginePath;
    }

    public void clearValues() {
        methodsSelected = new ArrayList<>();
        phenoTraits = new ArrayList<>();
        percentMissingMarkers = "10";
        PICValue = "0";
        MAF = "0";
        bayesRounds = "1000";
        bayesBurning = "100";
        bayesThinning = "5";
        forests = "100";
        cores = "2";
        replication = "1";
        fold = "1";

        scriptsPath = sharedInformation.getWorkingDirectory() + scriptsRelativePath;
        aScriptPath = scriptsPath + aScriptName;
        abScriptPath = scriptsPath + abScriptName;
        bScriptPath = scriptsPath + bScriptName;
        cScriptPath = scriptsPath + cScriptName;
        enginePath = sharedInformation.getWorkingDirectory() + rEngineRelativePath;
    }

    void setInputFile(String genoFile, String phenoFile) {
        this.genoFile = genoFile;
        this.phenoFile = phenoFile;
        resultFileStub = "result_" + new SimpleDateFormat("hhmmss").format(new Date());
        resultFileName = resultFileStub;
    }
}
