args<-commandArgs(TRUE)
#source("CodedSNP.R") # Function for converting allelic data to 1 0 -1 data 
#TFile<-tempfile()
TFile <-"Atemp_a.r.txt"
sink(TFile)

require("genetics", quietly=T)
require(R2HTML)


# 1 = Parameter will be Path
# 2 = name of GenoFile
# 3 = Phenotypic File
# 4 = Result File Name
# 5 = Missing %
# 6 = MAF 
# 7=  PIC = 0
# 8 = Method Name
# 9 = No of Core
# 10 = Missing CH
# 11 = ridgeRegression
# 12 = kinshipGauss
# 13 = BayesB
# 14 = BayesCpi
# 15 = BayesLASSO
# 16 = randomForest
# 17 = Trait Number
# 18 = Data Type (SNP/ DArT)
# 19 = Allele Seperator
# 20 =
# 21 =
# 22 =
#args <- c("/home/Mohan/Desktop/GS-Project/ISMU-10Sep", "SNP.csv", "Phenotype.csv", "Result.htm" ,"0.1", "0.1", "0.1", -1, 2, "?", 1, 1, -1, -1, -1, 1, 2,1,":",  "GSResTMP.txt", "TMP1.txt", "TMP2.txt")
# args <- c("D:/ICRISAT/Work/Analysis/Work/ISMU/ISMU", "Genotype.csv", "Phenotype.csv", "Result.htm" ,"0.3", "0.2", "0.2", -1, 2, "?", 1, 1, -1, -1, -1, 1, 2,1,":",  "GSResTMP.txt", "TMP1.txt", "TMP2.txt", 2)
# args <- c("f:/ismutest", "Genotype.csv", "Phenotype.csv", "Result.htm" ,"0.3", "0.2", "0.2", -1, 2, "?", 3,1,":", 1)
AWD       <- args[1]
AGDATA    <- args[2]
APDATA  	<- args[3]
ARFILE		<- args[4]
AMISP		  <- as.numeric(args[5])
APIC		  <- as.numeric(args[6])
AMAF		  <- as.numeric(args[7])
AGSMETHOD	<- args[8]
ANCORE		<- as.numeric(args[9])
AMISSCH		<- args[10]
ATraitNo <-as.numeric( args[11])
AMTYPE  <- as.numeric(args[12])
ASEP    <- args[13]
Engine  <- as.numeric(args[14])   #
#  Engine 
#  To use R , 1
#  To Use Fortran Alpha AGH 2

###################################################################################################################################################
oldwd<-setwd(AWD)

# Read Data from User File (Markers in rows and Genotypes in column)
DArT1<-read.csv( AGDATA, na.strings=c(AMISSCH,"NA","NN", "N","9"), stringsAsFactors=F,row.names=1,check.names=F)
TDART<-as.data.frame(t(DArT1))

#

Row<-1
Col<-1

while(TRUE)
{
  if( !is.na(DArT1[Row, Col]) )
  {
    MarkerPoint<-DArT1[Row, Col]
    break
  }
  Col<-Col+1
}

len<-nchar(MarkerPoint)

if(len == 1 )
{ # DART Data
  AMTYPE<-2
  
} else if (len==2)
{ # SNP Data
  AMTYPE<-1
  ASEP=""
}else  if (len==3)
{ # SNP with a Seperator in between
  AMTYPE<-1
  ASEP<-substr(MarkerPoint,2,2)
} else
{
  cat(paste("\nIncorrect Marker Data found.", MarkerPoint,".\n Please check Row No.", Row,  " Col No.", Col, sep=""))
  stop(paste("\nIncorrect Marker Data found.", MarkerPoint,".\n Please check Row No.", Row,  " Col No.", Col, sep=""))
}


# Read Phenotype data from User File 
BLUP <- read.csv(APDATA, na.strings=c(AMISSCH,"NA","NN"), stringsAsFactors=F)

# Make common set of genotypes

MGeno <- rownames(TDART)
BName <- BLUP[,1]
Index <- BName %in% MGeno
FinalBLUP1 <- BLUP [Index, ]
IndexM <- MGeno  %in% BName
FinalM1 <- TDART[IndexM,]
FinalM2<-data.frame(Genotype=rownames(FinalM1 ),FinalM1)
FinalBLUP<- FinalBLUP1[order(FinalBLUP1[,1]),]
FinalM <- FinalM2[order(FinalM2$Genotype),]

FinalM3<-FinalM[,-1]

if (Engine == 1)  # Use R to subset Data
{

    
    # Calculating Table for Genotype Data Summary
    MisRatio<-sapply (FinalM3, function(X) sum(is.na(X)) / length(X))
    
    if (AMTYPE==1) GenSum<- lapply(FinalM3, function (X) summary(genotype(X,sep=ASEP)))
    if (AMTYPE==2) GenSum<- lapply(FinalM3, function (X) summary(genotype(X,X)))
    
    AlleleC<-NULL
    MAF<-NULL
    PIC<-NULL
    for(i in 1:length(GenSum))
    {
      M<-GenSum[i]
      AlleleC[i]<-length(M[[1]][[1]])
      AlleleFreq<- M[[1]][[2]][,"Proportion"]
      MAF[i]<-min(AlleleFreq,na.rm=T)
      PIC[i]<-M[[1]]$pic
    }
    names(PIC)<-names(GenSum)
    names(MAF)<-names(GenSum)
    
    MISP<-AMISP
    
    if(!MISP==-1) # -1 = No Checks on Data, Take All data as it is.
    {
      MVec<- MisRatio <= MISP
      CleanDart<-FinalM3[,MVec]
      dim(CleanDart)
      cat("\n Missing Threshold = ", MISP , "\n Markers Removed = ", length(MVec)-sum(MVec))
      if(length(MVec[!MVec])>=1) cat("\n List of Markers Removed :", names(MVec[!MVec])) else cat("\n List of Markers Removed :", NULL) 
      cat("\n-----------------------------------------------------------------------------------------------")
    } else 
    {
      cat("\n------------------------------------------------------------")
      cat("\nWarning: No Threshold for missing % of marker was specified!!!")
      CleanDart<-TDART
    }
    
    PICT<-APIC
    
    if(!PICT==-1) # -1 = No Checks on Data, Take All data as it is.
    {
      SSV<- PIC[names(CleanDart)]>PICT
      SSDART<-CleanDart[,SSV]
      dim(SSDART)
      cat("\n PIC Threshold = ", PICT, "\n Markers Removed = ", length(SSV)-sum(SSV))
      if(length(SSV[!SSV])>=1) cat("\n List of Markers Removed :", names(SSV[!SSV])) else cat("\n List of Markers Removed :", NULL) 
      cat("\n-----------------------------------------------------------------------------------------------")
    } else 
    {
      cat("\n------------------------------------------------------------")
      cat("\nWarning: No Threshold for PIC was specified!!!")
      SSDART<-CleanDart
    }
    
    MAFT<-AMAF
    
    if(!MAFT==-1) # -1 = No Checks on Data, Take All data as it is.
    {
      SSVMF<-MAF[names(SSDART)]>=MAFT 
      SSDART2<-SSDART[,SSVMF]
      dim(SSDART2)
      cat("\n Minor Allele Threshold = ", MAFT, "\n Markers Removed = ", length(SSVMF)-sum(SSVMF))
      if(length(SSVMF[!SSVMF])>=1) cat("\n List of Markers Removed :", names(SSVMF[!SSVMF])) else cat("\n List of Markers Removed :", NULL) 
      cat("\n-----------------------------------------------------------------------------------------------")
    } else {
      cat("\n------------------------------------------------------------")
      cat("\nWarning: No Threshold for MAF was specified!!!")
      SSDART2<-SSDART
    }
      
###################################################################################################################################################

# Conversion of allelic Data to 1 0 -1 format

if(AMTYPE==1)
{
  source(paste(oldwd, "/", "CodedSNP.R", sep="") ) # Function for converting allelic data to 1 0 -1 data 
  n<-nrow(SSDART2)
  c<-ncol(SSDART2)
  DCodedSNP<-matrix(rep(0,n*c),nrow=n)
  
  for(i in 1:c)
  {
    DCodedSNP[,i]<-CodedSNP(SSDART2[i])
  }  
  rownames(DCodedSNP)<-rownames(SSDART2)
  colnames(DCodedSNP)<-colnames(SSDART2)
  FinalG<-DCodedSNP
} 

if (AMTYPE==2) FinalG<-SSDART2

###################################################################################################################################################

# Imputation of Marker Data 
THRes<-require(imputation)
geno1<-as.matrix(FinalG)

geno2<-kNNImpute(t(geno1),10)$x

geno<-t(geno2)

###################################################################################################################################################
} # Finished with R to Fileter Data

if(Engine==2 ) # If Engine is ForTran
{
  if(AMTYPE==1)  # SNP data
  {
    source(paste(oldwd, "/", "CodedSNP.R", sep="") ) # Function for converting allelic data to 1 0 -1 data 
    n<-nrow(FinalM3)
    c<-ncol(FinalM3)
    DCodedSNP<-matrix(rep(0,n*c),nrow=n)
    
    for(i in 1:c)
    {
      DCodedSNP[,i]<-CodedSNP(FinalM3[i])
    }  
    rownames(DCodedSNP)<-rownames(FinalM3)
    colnames(DCodedSNP)<-colnames(FinalM3)
    FinalG<-DCodedSNP
    FinalG <- FinalG+1 # This is because Fortran Prog expect 0, 1 and 2
  }
  
  if(AMTYPE==2)  # DArt/Dominant  data
  { # Replace 1 as 2.
    FinalM3 [FinalM3==1] <- 2    # This is because Fortran Prog expect 0 and 2
    FinalG <- FinalM3
  }
  
  
  FinalG <- t(FinalG)
  write.csv(cbind(Marker=row.names(FinalG), FinalG), "TempG.csv", na="9", row.names=F, quote=F)
  Individuals <- dim(FinalG)[2]
  
  source(paste(oldwd, "/", "summaryAGH.R", sep="") ) # Function for Runing AlphaAGH
  FortranSumName <- "PicMissingGenotypesMafRecDomNum.txt" # not used any where
  
  Res<-AlphaAGHSummary(FilePATH=AWD, GenoFileName="TempG.csv", Individuals=Individuals,  MISS_T=AMISP, MAF_T=AMAF, PIC_T=APIC, FortranSumName=FortranSumName )
  # Now Read Subset File created by Fortran Prog
  GenSubSet<-read.table("GenotypeFileSubset.txt", header=T,na.strings=9, nrows=-1)
  
  #write.table(GenSubSet,"CMImData_For_Analysis.csv", row.names=F, quote=F, na="9")
  
  geno <- GenSubSet
  
#   GenSubSetT <-t(GenSubSet[,-1])
#   colnames(GenSubSetT)<-GenSubSet[,1] 
#   GenSubSet <-GenSubSetT
#   #GenSubSet <- GenSubSet[,-1]  
#   
# # Now back transforma data in to -1 0 1 froamt
# 
#   if (AMTYPE==1) GenSubSet <- GenSubSet -1   # If SNP
# 
#   if (AMTYPE==2)  # if DART
#     {
#      GenSubSet[ GenSubSet == 2] <- 1     
#     }
# 
# # Imputation of Marker Data 
#   THRes<-require(imputation)
#   geno1<-as.matrix(GenSubSet)
#   
#   geno2<-kNNImpute(t(geno1),10)$x
#   
#   geno<-t(geno2)



} # End of Fortran

# Write Cleaned Genotype Data to Disk
# SSDART3<-data.frame(Genotype=rownames(SSDART2), SSDART2,row.names=NULL)
# write.csv(SSDART3, "CMData_All.csv",row.names=F)
# cat("\n Marker File Saved!\n")

# Write Pheno Final Genotype and Phenotype Data to Disk
# write.csv(FinalBLUP, "CPData.csv",row.names=F)
# write.csv(FinalG, "CMData_For_Analysis.csv",row.names=F)
 write.csv(geno,"CMImData_For_Analysis.csv")
# cat("\n Common Marker and Phenotype File Saved!\n")

# save(FinalBLUP, FinalG, geno, file="gsdata.rdata")
save(FinalBLUP, geno, file="gsdata.rdata")
sink()
setwd(oldwd)
#file.remove(TFile)

