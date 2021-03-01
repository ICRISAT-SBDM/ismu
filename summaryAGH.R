#args <- c("D:/ICRISAT/Work/Analysis/Work/ISMU/ISMU", "Genotype.csv", "Result.htm" ,1, 1, -1, -1, 2, "?", "Summary.csv", 1)
#a<-system2("C:\Users\Arathore\Desktop\ISMU\MSWindows\ISMU2.0\bin\x64\alphaAGH.exe", args=c("test1.bat", shQuote ("2") ), minimized = FALSE, invisible = F)

#Res<-AlphaAGHSummary(FilePATH=AWD, GenoFileName=AGDATA, Individuals=49,  MISS_T=10, MAF_T=.1, PIC_T=.1, FortranSumName=FortranSumName )

AlphaAGHSummary <- function(FilePATH = FilePATH, 
                           GenoFileName = GenoFileName,
                           Individuals =Individuals,
                           MISS_T=MISS_T,
                           MAF_T = MAF_T,
                           PIC_T = PIC_T,
                           FortranSumName=FortranSumName
                           )
{

IPFileAGH <- "AlphaAGHSpec.txt"
FilePATH <- FilePATH   # Same as AWD
# Genotypic Data
GenoFileName <-GenoFileName
Individuals <-Individuals

MISS_T  <-MISS_T
MAF_T <- MAF_T
PIC_T <- PIC_T
FortranSumName <- FortranSumName


# Make input file
FRes <- file(IPFileAGH, "wt")  

cat("##############################Input parameters############################", fill=T, file=FRes)
cat("InputFolder\t,\"", FilePATH , "/\"", sep="", fill=T, file=FRes)
cat("OutputFolder\t,\"", FilePATH ,  "/\"", sep="",  fill=T, file=FRes)
cat("GenotypeFile\t,", GenoFileName, fill=T, file=FRes)
cat("GenotypeFileHeader\t,", "Yes",fill=T,  file=FRes)
cat("PedigreeFile\t,","None",fill=T,  file=FRes)
cat("WeightFile\t,","None",fill=T, file=FRes)
cat("NumberOfTraits\t,",1,fill=T, file=FRes)
cat("NumberOfSnp\t,",Individuals ,fill=T, file=FRes)
cat("InversionOfG\t,", "Ordinary",fill=T, file=FRes)
cat("DiagonalFudgeFactor\t,",0.001,fill=T, file=FRes)
cat("MissingPercTreshold\t,",MISS_T ,fill=T, file=FRes)
cat("MafTreshold\t,",MAF_T,fill=T, file=FRes)
cat("PicTreshold\t,",PIC_T,fill=T, file=FRes)
cat("##############################Matrices to be constructed##################", fill=T, file=FRes)
cat("MakeG\t,","No", fill=T, file=FRes)
cat("MakeGinverse\t,","No", fill=T, file=FRes)
cat("MakeA\t,","No", fill=T, file=FRes)
cat("MakeAinverse\t,","No", fill=T, file=FRes)
cat("MakeH\t,","No", fill=T, file=FRes)
cat("MakeHinverse\t,","No", fill=T, file=FRes)
cat("##############################Output options##############################",fill=T, file=FRes)
cat("SummaryStatistics\t,","Yes", fill=T, file=FRes)
cat("DominantMarkers\t,","No", fill=T, file=FRes)
cat("VanRadenGFullMat\t,","No", fill=T, file=FRes)
cat("VanRadenGIJA\t,","No", fill=T, file=FRes)
cat("InverseVanRadenGFullMat\t,","No", fill=T, file=FRes)
cat("InverseVanRadenGIJA\t,","No", fill=T, file=FRes)
cat("AmatFullMat\t,","No", fill=T, file=FRes)
cat("AmatIJA\t,","No", fill=T, file=FRes)
cat("InverseAmatFullMat\t,","No", fill=T, file=FRes)
cat("InverseAmatIJA\t,","No", fill=T, file=FRes)
cat("HmatFullMat\t,","No", fill=T, file=FRes)
cat("HmatIJA\t,","No", fill=T, file=FRes)
cat("InverseHmatFullMat\t,","No", fill=T, file=FRes)
cat("InverseHmatIJA\t,","No", fill=T, file=FRes)

close(FRes)

INFName <- paste( FilePATH,"/", IPFileAGH, sep="")
AGHExePath <- paste(oldwd, "/", "AlphaAGH", sep="")
a<-system2(AGHExePath, args=c("--par", shQuote (INFName) ), minimized = FALSE, invisible = T, stdout=F)

# cat("\nCAT: ", FortranSumName ,  "\n")
# print(paste("\nPRINT: ", FortranSumName , "\n"))
# 
# file.copy(from="PicMissingGenotypesMafRecDomNum.txt", to=FortranSumName, overwrite=T)
# cat("\nCAT: ", FortranSumName , "\n")
# print(paste("\nPRINT: ", FortranSumName , "\n"))

#a<-system2("C:\\Users\\Arathore\\Desktop\\ISMU\\MSWindows\\ISMU2.0\\bin\\x64\\alphaAGH.exe", args=c("--par ", INFName ), minimized = FALSE, invisible = F)
}
