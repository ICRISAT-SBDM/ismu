args<-commandArgs(TRUE)
#source("bayesBparallel.R")
#source("bayesCpi.R")
#source("bayesBparallel.R")
#source("bayesLasso.R")
#source("BLRnonVerbose.R")
#source("pedigreeBLUP.R")
TFile<-tempfile()
#TFile
sink(TFile)
cat(args)
print(args)
StartTime<-Sys.time()
#path = "c:\Program Files\r\R-3.0.1\bin\x64"
#cd D:\ICRISAT\Work\Analysis\Work\ISMU\ISMU  

GetRFileName <- function (start = "RES_")
{
  paste(start, substring(date(), 5, 7), substring(date(), 9, 10), "_", substring(date(), 12, 13), substring(date(), 15, 16), substring(date(), 18, 19), abs(round(rnorm(1),3)), sep = "")  
}

require("genetics", quietly=T)
require(R2HTML)

#args <- c("C:/Users/admin/Documents/2", "Result_1516169348942.htm", -1, 2, -1, 1, 1,   1,  1,  1,  1, 2, -1, -1, "GSTemp.txt", 1, -1, -1, "Select", 1000, 100, 5, 1, 1, 100, "F:/GS/ismu/Utils/Scripts")
AWD       			<- args[1]
ARFILE    			<- args[2]
AGSMETHOD			<- args[3]
ANCORE				<- as.numeric(args[4])
AMISSCH				<- args[5]
ARR					<- args[6]
AKG					<- args[7]
ABB					<- args[8]
ABCPI				<- args[9]
ABL					<- args[10]
ARF					<- args[11]
ATraitNo 			<-as.numeric( args[12])
AMTYPE  			<- as.numeric( args[13])
ASEP    			<- args[14]
ATEMP_FILE_CORR    	<- args[15]   # Will Store: Method, Coorel and  P value
Engine  			<- as.numeric(args[16])   #
FRR      			<-  as.numeric(args[17])   # Fortran Method : RR
FBA     			<-  as.numeric(args[18])   # Fortran Method : BayesA
CovFileName    		<-  args[19]   # Fortran Method : BayesA
NIter 				<- args[20]  # This is Rounds
NBurnIn				<-args[21]  # This is Burn iN
NThin 				<- args[22]  # Thining is not in Fortran, But we have in R
Replications = as.numeric(args[23])
Folds= as.numeric(args[24])
NTree 				<- as.numeric(args[25])  # No. of forests
ASPATH 				<- args[26]

if(is.na(NIter)) NIter <-1000
if(is.na(NBurnIn)) NBurnIn <-100
if(is.na(NTree)) NTree <-100
if(is.na(NThin)) NThin <-5

cat(args)

 
if (Replications > 1) CrossValidation =1
 CrossValidation <- 0
if (Folds > 1) CrossValidation <- 1   # Cross Validation is only possible if we have Folds more than 1

oldwd<-setwd(AWD)
source(paste(ASPATH, "/genomicPredictionWrapper.R", sep=""))
source(paste(ASPATH,"/bayesBparallel.R",sep=""))
source(paste(ASPATH,"/bayesCpi.R",sep=""))
source(paste(ASPATH,"/bayesLasso.R",sep=""))
source(paste(ASPATH,"/BLRnonVerbose.R",sep=""))

load("gsdata.rdata")


########## Lets Do GS ################################

#########################################    Running Genomic Selection Scripts #############################################################
# Now we will read data written by previous script and run this file with comman line
# We will call this R script for each Method
###########################################################################################################################
#THRes<-source("genomicPredictionWrapper.R")

# Read Data
# Genotypic 
# Phenotypic




#par(mfrow=c(2,3))

#NameTrait<-colnames(FinalBLUE[,3:6])

#***************************************************************************#
#***********  Give Pheno Matrix to FinalPheno   ****************************#
#***************************************************************************#
#FinalPheno<-FinalBLUE

if(! (CovFileName =="Select")) # i.e. Covariate Selected, as Defaul Value is "Select"
{
  #covariateF <-as.matrix(read.csv(CovFileName,header=T,row.names=1))
  covariateF <-read.csv(CovFileName,header=T,row.names=1)
  nCov<-ncol(covariateF)
  
} else {
  
  #cat("\nInside Else")
  #print("\nInside Else")
  #print(FinalBLUP)
  
  
  #covariateF<-matrix(1, nrow=nrow(FinalBLUP))
  covariateF<-data.frame(rep(1, nrow(FinalBLUP)),row.names=FinalBLUP[,1])
  nCov<-ncol(covariateF)
  
  #covariateF<-data.frame(covariateF)
  #cat("\n\nBefore")
  #print(rownames(covariateF))
  
  #rownames(covariateF) <- FinalBLUP[,1]
  
  
  #cat("\n\nAfter")
  
  #print(rownames(covariateF))
}

#print("\n\ncovariate:\n")
#cat("\n\ncovariate:\n")
#print(covariateF)

covariate <- covariateF[rownames(covariateF) %in% rownames(geno),]

if(nCov==1)
{
  covariate<-t(t(covariate))
  rownames(covariate)<-rownames(covariateF)[rownames(covariateF) %in% rownames(geno)]
}  

#cat("\nronames covariate")
#print(names(covariate))
#print(as.vector(rownames(covariate)))

#class(names(covariate))

#str(covariate)
#cat(order(rownames(covariate)))
covariate <- covariate[order(rownames(covariate)),] 
#nCov<-ncol(covariate)
#cat(nCov)



FinalPheno<-FinalBLUP

NameTrait<-colnames(FinalPheno[2:ncol(FinalPheno)])
NTraits<-length(NameTrait)
designFixed<-as.matrix(covariate,nrow= nrow(FinalPheno) ) # designFixed variable contains the covariate information




# As we will do it trait wise no Loop is required
i<-ATraitNo

#for (i in 1:NTraits )
#{
pheno<-FinalPheno[,c(1,1+i)]
ppi<-300  # Resolution
IR <- .5  # Image Ratio
SG <- 8   #Significant Decimal Digits


# ridge regression
if( ! (ARR == -1))
{  
  method<- "RidgeRegression"
  # Get Temporary and Unique Graph File Name
  PNGName <- paste(getwd(), "/", GetRFileName(), ".png", sep="");
  THRes<-png(PNGName, width=IR*10*ppi, height=IR*10*ppi, res=ppi)
  
  
  #   cat("\nPheno:", pheno)
  #   cat("\ndesignFixed:", designFixed)
  # #  cat(head(geno)[1:10])
  #   
  # Start RR BLUP 
  gsOut.rr <- genomicPrediction(pheno, geno,designFixed=designFixed,method="ridgeRegression")
  predicted<-designFixed%*% as.matrix(gsOut.rr$fixedEffects,ncol=1)+ matrix(gsOut.rr$predVals,ncol=1)
  correl<-cor.test(predicted,pheno[,2])
  THRes<-plot(predicted, pheno[,2], col="red", type="p", pch=19, main="Scatter Diagram", xlab="Predicted Phenotype", ylab="Observed Phenotype")
  
  GEBV<-data.frame(Genotype=rownames(geno), Observed= pheno[,2], GEBV=round(gsOut.rr$predVals,SG), row.names=NULL)
  hasPheno <- !is.na(pheno[,2])
  
  if(sum(hasPheno)<length(pheno[,2]))
  {
    TGEBV <- GEBV[hasPheno,]
    BGEBV <-GEBV[!hasPheno,]
  } else {
    TGEBV <- GEBV[hasPheno,]
    BGEBV<-NULL
  }
  
  
  # Close Device PNG  
  dev.off() # Graph Ready
  
  # Prepare and write Result to File  
  ResultSTR <- paste("\nRidgeRegression\t",  round(correl$estimate, SG), "\t",round(correl$p.value, SG), sep="", collapse="\n" )     
  GSRes <- file(ATEMP_FILE_CORR, "at")   #Open in APPEND Mode to write results of RR
  cat(ResultSTR, file=GSRes)
  
  
   if(CrossValidation==1)
     {
       THRes<-source(paste(ASPATH, "/runCrossVal.R", sep=""))
       THRes<-source(paste(ASPATH, "/crossVR.R", sep=""))
       CVRes <- crossVR(Replications =Replications, Folds =Folds,methods="ridgeRegression")
       ResultSTR <- paste("\nCrossValidation(R=",Replications, ",F=", Folds, ")\t",  round(CVRes[1], SG), "\t",round(CVRes [2], SG), sep="", collapse="\n" )     
       cat(ResultSTR, file=GSRes)
     }
    
  close(GSRes)
}


# kinship from Gaussian kernel
if( ! (AKG == -1))
{	
  method<-"KinshipGauss"
  # Get Temporary and Unique Graph File Name
  PNGName <- paste(getwd(), "/", GetRFileName(), ".png", sep="");
  THRes<-png(PNGName, width=IR*10*ppi, height=IR*10*ppi, res=ppi)
  
  # Start KG BLUP 
  gsOut.kg <- genomicPrediction(pheno, geno,designFixed=designFixed,method="kinshipGauss")
  predicted<-designFixed%*% as.matrix(gsOut.kg$fixedEffects,ncol=1)+ matrix(gsOut.kg$predVals,ncol=1)
  correl<-cor.test(predicted,pheno[,2])
  THRes<-plot(predicted, pheno[,2], col="red", type="p", pch=19, main="Scatter Diagram", xlab="Predicted Phenotype", ylab="Observed Phenotype")
  
  GEBV<-data.frame(Genotype=rownames(geno),Observed= pheno[,2], GEBV=round(gsOut.kg$predVals,SG),row.names=NULL)
  hasPheno <- !is.na(pheno[,2])
  if(sum(hasPheno)<length(pheno[,2]))
  {
    TGEBV <- GEBV[hasPheno,]
    BGEBV <-GEBV[!hasPheno,]
  } else {
    TGEBV <- GEBV[hasPheno,]
    BGEBV<-NULL
  }
  
  # Close Device PNG  
  dev.off() # Graph Ready
  
  # Prepare and write Result to File  
  ResultSTR <- paste("\nKinshipGauss\t",  round(correl$estimate, SG), "\t",round(correl$p.value, SG), sep="", collapse="\n" )  
  GSRes <- file(ATEMP_FILE_CORR, "at")   #Open in APPEND Mode to write results of RR
  cat(ResultSTR, file=GSRes)
  
     if(CrossValidation==1)
     {
       THRes<-source(paste(ASPATH, "/runCrossVal.R", sep=""))
       THRes<-source(paste(ASPATH, "/crossVR.R", sep=""))
       CVRes <- crossVR(Replications =Replications, Folds =Folds,methods="kinshipGauss")
       ResultSTR <- paste("\nCrossValidation(R=",Replications, ",F=", Folds, ")\t",  round(CVRes[1], SG), "\t",round(CVRes [2], SG), sep="", collapse="\n" )     
       cat(ResultSTR, file=GSRes)
     }
     
  close(GSRes)
}

# BayesCpi
if( ! (ABCPI == -1))
{	
  method<-"BayesCpi"
  # Get Temporary and Unique Graph File Name
  PNGName <- paste(getwd(), "/", GetRFileName(), ".png", sep="");
  THRes<-png(PNGName, width=IR*10*ppi, height=IR*10*ppi, res=ppi)
  
  # Start BCPi BLUP 
  gsOut.bc <- genomicPrediction(pheno, geno, designFixed=designFixed, method="BayesCpi", nIter=NIter, burnIn=NBurnIn, thin=NThin)
  predicted<-designFixed%*% as.matrix(gsOut.bc$fixedEffects,ncol=1)+ matrix(gsOut.bc$predVals,ncol=1)
  correl<-cor.test(predicted,pheno[,2])
  THRes<-plot(predicted, pheno[,2], col="green", type="p", pch=19, main="Scatter Diagram", xlab="Predicted Phenotype", ylab="Observed Phenotype")
  
  GEBV<-data.frame(Genotype=rownames(geno),Observed= pheno[,2], GEBV=round(gsOut.bc$predVals,SG),row.names=NULL)
  hasPheno <- !is.na(pheno[,2])
  if(sum(hasPheno)<length(pheno[,2]))
  {
    TGEBV <- GEBV[hasPheno,]
    BGEBV <-GEBV[!hasPheno,]
  } else {
    TGEBV <- GEBV[hasPheno,]
    BGEBV<-NULL
  }
  # Close Device PNG  
  dev.off() # Graph Ready
  
  # Prepare and write Result to File  
  ResultSTR <- paste("\nBayesCpi\t",  round(correl$estimate, SG), "\t",round(correl$p.value, SG), sep="", collapse="\n" )  
  GSRes <- file(ATEMP_FILE_CORR, "at")   #Open in APPEND Mode to write results of RR
  cat(ResultSTR, file=GSRes)
  
     if(CrossValidation==1)
     {
       THRes<-source(paste(ASPATH, "/runCrossVal.R", sep=""))
       THRes<-source(paste(ASPATH, "/crossVR.R", sep=""))
       CVRes <- crossVR( Replications =Replications, Folds =Folds,methods="BayesCpi")
       ResultSTR <- paste("\nCrossValidation(R=",Replications, ",F=", Folds, ")\t",  round(CVRes[1], SG), "\t",round(CVRes [2], SG), sep="", collapse="\n" )     
       cat(ResultSTR, file=GSRes)
     }
  
  close(GSRes)
  
}

# BayesB
# NOTE: because BayesB uses multicore, it has to be done from the command line, not the GUI
#   unless you specify n.core=1
if( ! (ABB == -1))
{	
  method<-"BayesB"
  # Get Temporary and Unique Graph File Name
  PNGName <- paste(getwd(), "/", GetRFileName(), ".png", sep="");
  THRes<-png(PNGName, width=IR*10*ppi, height=IR*10*ppi, res=ppi)
  
  # Start BB 
  gsOut.bb <- genomicPrediction(pheno, geno, designFixed=designFixed,method="BayesB", nIter=NIter, burnIn=NBurnIn, thin=NThin, nChain=2, n.core=n.core)
  predicted<-designFixed%*% as.matrix(gsOut.bb$fixedEffects,ncol=1)+ matrix(gsOut.bb$predVals,ncol=1)
  correl<-cor.test(predicted,pheno[,2])
  THRes<-plot(predicted, pheno[,2], col="maroon", type="p", pch=19, main="Scatter Diagram", xlab="Predicted Phenotype", ylab="Observed Phenotype")
  
  GEBV<-data.frame(Genotype=rownames(geno),Observed= pheno[,2],GEBV=round(gsOut.bb$predVals,SG),row.names=NULL)
  hasPheno <- !is.na(pheno[,2])
  if(sum(hasPheno)<length(pheno[,2]))
  {
    TGEBV <- GEBV[hasPheno,]
    BGEBV <-GEBV[!hasPheno,]
  } else {
    TGEBV <- GEBV[hasPheno,]
    BGEBV<-NULL
  }
  # Close Device PNG  
  dev.off() # Graph Ready
  
  # Prepare and write Result to File  
  ResultSTR <- paste("\nBayesB\t",  round(correl$estimate, SG), "\t",round(correl$p.value, SG), sep="", collapse="\n" )  
  GSRes <- file(ATEMP_FILE_CORR, "at")   #Open in APPEND Mode to write results of RR
  cat(ResultSTR, file=GSRes)
     if(CrossValidation==1)
     {
       THRes<-source(paste(ASPATH, "/runCrossVal.R", sep=""))
       THRes<-source(paste(ASPATH, "/crossVR.R", sep=""))
       CVRes <- crossVR( Replications =Replications, Folds =Folds,methods="BayesB")
       ResultSTR <- paste("\nCrossValidation(R=",Replications, ",F=", Folds, ")\t",  round(CVRes[1], SG), "\t",round(CVRes [2], SG), sep="", collapse="\n" )     
       cat(ResultSTR, file=GSRes)
     }
     
  close(GSRes)
  
} 

# Bayesian LASSO
if( ! (ABL == -1))
{	
  method<-"BayesianLASSO"
  # Get Temporary and Unique Graph File Name
  PNGName <- paste(getwd(), "/", GetRFileName(), ".png", sep="");
  THRes<-png(PNGName, width=IR*10*ppi, height=IR*10*ppi, res=ppi)
  
  # Start BL 
  
  gsOut.bl <- genomicPrediction(pheno, geno, designFixed=designFixed, method="BayesLASSO", nIter=NIter, burnIn=NBurnI,thin=NThin)
  predicted<-designFixed%*% as.matrix(gsOut.bl$fixedEffects,ncol=1)+ matrix(gsOut.bl$predVals,ncol=1)
  correl<-cor.test(predicted,pheno[,2])
  THRes<-plot(predicted, pheno[,2], col="magenta", type="p", pch=19, main="Scatter Diagram", xlab="GEBV", ylab="Observed Phenotype")
  
  GEBV<-data.frame(Genotype=rownames(geno),Observed= pheno[,2],GEBV=round(gsOut.bl$predVals,SG),row.names=NULL)
  hasPheno <- !is.na(pheno[,2])
  if(sum(hasPheno)<length(pheno[,2]))
  {
    TGEBV <- GEBV[hasPheno,]
    BGEBV <-GEBV[!hasPheno,]
  } else {
    TGEBV <- GEBV[hasPheno,]
    BGEBV<-NULL
  }
  # Close Device PNG  
  dev.off() # Graph Ready
  
  # Prepare and write Result to File  
  ResultSTR <- paste("\nBayesianLASSO\t",  round(correl$estimate, SG), "\t",round(correl$p.value, SG), sep="", collapse="\n" )  
  GSRes <- file(ATEMP_FILE_CORR, "at")   #Open in APPEND Mode to write results of RR
  cat(ResultSTR, file=GSRes)
  
     if(CrossValidation==1)
     {
       THRes<-source(paste(ASPATH, "/runCrossVal.R", sep=""))
       THRes<-source(paste(ASPATH, "/crossVR.R", sep=""))
       CVRes <- crossVR(Replications =Replications, Folds =Folds,methods="BayesLASSO")
       ResultSTR <- paste("\nCrossValidation(R=",Replications, ",F=", Folds, ")\t",  round(CVRes[1], SG), "\t",round(CVRes [2], SG), sep="", collapse="\n" )     
       cat(ResultSTR, file=GSRes)
     }
     
  close(GSRes)
  
}  

# pedigree BLUP
# gsOut.pb <- genomicPrediction(pheno, pedigree=pedigree, method="pedigreeBLUP")
# plot(gsOut.rr$predVals, gsOut.pb$predVals)

if( ! (ARF == -1))
{	
  # random Forest
  method<-"RandomForest"
  # Get Temporary and Unique Graph File Name
  PNGName <- paste(getwd(), "/", GetRFileName(), ".png", sep="");
  THRes<-png(PNGName, width=IR*10*ppi, height=IR*10*ppi, res=ppi)
  
  # Start RF 
  gsOut.rf <- genomicPrediction(pheno, geno, method="randomForest", ntree=NTree)
  predicted<-matrix(gsOut.rf$predVals,ncol=1)
  correl<-cor.test(predicted,pheno[,2])
  THRes<-plot(predicted, pheno[,2], col="gold", type="p", pch=19, main="Scatter Diagram", xlab="Predicted Phenotype", ylab="Observed Phenotype")
  
  GEBV<-data.frame(Genotype=rownames(geno),Observed= pheno[,2],GEBV=round(gsOut.rf$predVals,SG),row.names=NULL)
  hasPheno <- !is.na(pheno[,2])
  if(sum(hasPheno)<length(pheno[,2]))
  {
    TGEBV <- GEBV[hasPheno,]
    BGEBV <-GEBV[!hasPheno,]
  } else {
    TGEBV <- GEBV[hasPheno,]
    BGEBV<-NULL
  }
  # Close Device PNG  
  dev.off() # Graph Ready
  
  # Prepare and write Result to File  
  ResultSTR <- paste("\nRandomForest\t",  round(correl$estimate, SG), "\t",round(correl$p.value, SG), sep="", collapse="\n" )  
  GSRes <- file(ATEMP_FILE_CORR, "at")   #Open in APPEND Mode to write results of RR
  cat(ResultSTR,file=GSRes)
  
     if(CrossValidation==1)
     {
       THRes<-source(paste(ASPATH, "/runCrossVal.R", sep=""))
       THRes<-source(paste(ASPATH, "/crossVR.R", sep=""))
       CVRes <- crossVR( Replications =Replications, Folds =Folds,methods="randomForest")
       ResultSTR <- paste("\nCrossValidation(R=",Replications, ",F=", Folds, ")\t",  round(CVRes[1], SG), "\t",round(CVRes [2], SG), sep="", collapse="\n" )     
       cat(ResultSTR, file=GSRes)
     }
     
  close(GSRes)
  
}

# Now Write Results to HTML Page ;-)
# Calculate Time
EndTime <- Sys.time()
DiffTime <- EndTime-StartTime 
DiffTimeInString <- paste("\nTime take for ", method, " : ", round(c(DiffTime), 4), "Seconds")
# 
# # 1 : Start Phenotype Summary 
# PSummary <- summary(FinalPheno[,i])
#   TRes<-HTML.title("Phenotypic Summary", HR=3, file=ARFILE, Append=T)
#   TRes<-HTML(PSummary, HR=1, file=ARFILE,  Append=T)
# Make PNG of Historgam
# 
# PNGNameTrait <- paste(getwd(), "/", GetRFileName(), ".png", sep="");
# THRes<-png(PNGNameTrait, width=IR*10*ppi, height=IR*10*ppi, res=ppi)
# TRes<-hist(FinalPheno[,i], xlab=NameTrait[i])
# dev.off()
#   TRes<- HTMLInsertGraph(PNGNameTrait, file=ARFILE)

# Now put GS Results
# First Heading with Selected method
TRes<-HTML.title(paste("Genomic Selection Method: ", method,"(R)"), HR=3, file=ARFILE, Append=T)
TRes<- HTMLInsertGraph(PNGName, file=ARFILE)

# Write Graph Title

TRes<-HTML("<div align=\"center\">", file=ARFILE, Append=T)
TRes<-HTML(paste("<b align=\"center\">Observed Phenotype Vs GEBV (", method,")</b>"), HR=4, file=ARFILE, Append=T, Align="center")
TRes<-HTML("<br/>", file=ARFILE, Append=T)

# Write Correlations
TRes<-data.frame(Method= method, Correlation = round(correl$estimate, SG), "p.value" = round(correl$p.value, SG), row.names=NULL)
Res<-HTML(TRes, file=ARFILE, Append=T)

# 
# if(CrossValidation==1)
# {
#   TRes<-data.frame("Cross Validation"= paste("Repl=",Replications, ", Folds=", Folds, sep="" ), "Correlation" = round(CVRes[1,1], SG), "p.value" = round(CVRes [2], SG), row.names=NULL)
#   Res<-HTML(TRes, file=ARFILE, Append=T)
# }
# 


# Write GEBVs
TRes<-HTML("<br/>", file=ARFILE, Append=T)
TRes<-HTML(paste("<b align=\"center\">GEBV for ", colnames(pheno[2]),"</b>"), HR=4, file=ARFILE, Append=T, Align="center")
Res<-HTML(GEBV, file=ARFILE, Append=T,  digits=6, nsmall = 6)

# Close Center Alignement
TRes<-HTML("</div>", file=ARFILE, Append=T)


# This and below may go to c.r 

# Write All Correlation and P Value

# Res<-read.table(ATEMP_FILE_CORR, header=T)
# TRes<-HTML(Res, file=ARFILE, Append=T)

# Write Time and Logo
# TRes<-HTML("<hr/><p align=center> ", file=ARFILE, Append=T)
# TRes<-HTML(paste("Results Generated by ISMU 2.0 : ", format(Sys.time(), "%a %b %d %X %Y")), file=ARFILE, Append=T)
# TRes<-HTMLEndFile(file , file=ARFILE)

# TRes<-HTML("<table border=1><tr class= firstline><td>Method</td><td>Correlation</td><td>Prob>t</td></tr>", file=ARFILE, Append=T)
# TRes<-HTML(paste("<tr><td>", method, "</td><td>", round(correl$estimate, SG), "</td><td>", round(correl$p.value, SG),"</td></tr>"), file=ARFILE, Append=T)

sink()

setwd(oldwd)
file.remove(TFile)