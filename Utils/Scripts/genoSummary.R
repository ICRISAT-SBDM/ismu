args<-commandArgs(TRUE)
#print(args)
TFile<-tempfile()
#TFile
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
# 13 = BayesLASSO
# 14 = BayesCpi
# 15 = BayesB
# 16 = randomForest
# 17 = Trait Number
# 18 = Data Type (SNP/ DArT)
# 19 = Allele Seperator
# 20 =
# 21 =
# 22 =

#rscript  C:\Users\HI\Documents\2\ 1 1 1 -1 2 ? 1 Genotype043214.csv Ge_summary_Genotype043214.htm Ge_summary_Genotype043214.csv Phenotype043214.csv Ph_summary_Phenotype043214.htm

AWD         <- args[1]
AMISP	    <- as.numeric(args[2])
APIC	    <- as.numeric(args[3])
AMAF	    <- as.numeric(args[4])
AMTYPE      <- as.numeric(args[5])
ASEP        <- args[6]
AMISSCH     <- args[7]
#  Engine 
#  To use R , 1
#  To Use Fortran Alpha AGH 2
Engine      <- as.numeric(args[8])
AGDATA      <- args[9]
ARFILE      <- args[10]
SumFileName <- args [11]
APDATA      <- args [12]
FilePhSUM   <- args [13]

ppi<-300  # Resolution
IR <- .5  # Image Ratio
SG <- 3   #Significant Decimal Digits

######################################################################################################################
### Generating random file names

GetRFileName <- function (start = "RES_")
{
  paste(start, substring(date(), 5, 7), substring(date(), 9, 10), "_", substring(date(), 12, 13), substring(date(), 15, 16), substring(date(), 18, 19), abs(round(rnorm(1),3)), sep = "")  
}

oldwd<-setwd(AWD)

######################################################################################################################
### Reading Genotypic Data from directory

DArT1<-read.csv(AGDATA,row.names=1)

TDART<-as.data.frame(t(DArT1))

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
  ASEP<-""
}else  if (len==3)
{ # SNP with a Seperator in between
  AMTYPE<-1
  ASEP<-substr(MarkerPoint,2,2)
} else
{
  cat(paste("\nIncorrect Marker Data found.", MarkerPoint,".\n Please check Row No.", Row,  " Col No.", Col, sep=""))
  stop(paste("\nIncorrect Marker Data found.", MarkerPoint,".\n Please check Row No.", Row,  " Col No.", Col, sep=""))
}

if(Engine==1) # 1= R
{# Using R as Engine for Data Summary
### Calculating Table for Genotype Data Summary
MisRatio<-sapply (TDART, function(X) sum(is.na(X)) / length(X))

if (AMTYPE==1) GenSum<- lapply(TDART, function (X) summary(genotype(X,sep=ASEP)))
if (AMTYPE==2) GenSum<- lapply(TDART, function (X) summary(genotype(X,X)))

AlleleC<-NULL
MAF<-NULL
PIC<-NULL

for(i in 1:length(GenSum))
{
  M<-GenSum[i]
  AlleleC[i]<-length(M[[1]][[1]])
  AlleleFreq<- M[[1]][[2]][,"Proportion"]
  MAF[i]<-min(AlleleFreq,na.rm=T)
  if(MAF[i] ==1 ) MAF[i]=0
  
  PIC[i]<-M[[1]]$pic
}
names(PIC)<-names(GenSum)
names(MAF)<-names(GenSum)

MisP<-MisRatio*100

GenSummary<-data.frame(PIC=PIC,MisPercent=MisP,MAF=MAF,row.names=NULL)
GenSummary<-data.frame(Marker=rownames(DArT1),GenSummary)
GenSummary[,2] <- round(GenSummary[,2], SG)
GenSummary[,3] <- round(GenSummary[,3], SG)
GenSummary[,4] <- round(GenSummary[,4], SG)

}

if(Engine==2)  # 2= Fortran
{
#  DArT1<-read.csv(AGDATA, na.strings=c(AMISSCH,"NA","NN", "9"), stringsAsFactors=F,row.names=1)
  if(AMTYPE==1)  # SNP data
  {
      source(paste(oldwd, "/", "CodedSNP.R", sep="") ) # Function for converting allelic data to 1 0 -1 data 
      n<-nrow(TDART)
      c<-ncol(TDART)
      DCodedSNP<-matrix(rep(0,n*c),nrow=n)
      
      for(i in 1:c)
      {
        DCodedSNP[,i]<-CodedSNP(TDART[i])
      }  
      rownames(DCodedSNP)<-rownames(TDART)
      colnames(DCodedSNP)<-colnames(TDART)
      FinalG<-DCodedSNP
      FinalG <- FinalG+1
  }
  
  if(AMTYPE==2)  # DArt/Dominant  data
  { # Replace 1 as 2.
    TDART [TDART==1] <- 2
    FinalG <- TDART
  }
  FinalG <- t(FinalG)
  write.csv(cbind(Marker=row.names(FinalG), FinalG), "TempG.csv", na="9", row.names=F, quote=F)
  Individuals <- dim(FinalG)[2]

  source(paste(oldwd, "/", "summaryAGH.R", sep="") ) # Function for Runing AlphaAGH
  FortranSumName <- "PicMissingGenotypesMafRecDomNum.txt" # not used any where
  
  Res<-AlphaAGHSummary(FilePATH=AWD, GenoFileName="TempG.csv", Individuals=Individuals,  MISS_T=10, MAF_T=.1, PIC_T=.1, FortranSumName=FortranSumName )
  GenSummary<-read.table("PicMissingGenotypesMafRecDomNum.txt", header=T)
  GenSummary <-GenSummary[,-c(5,6)]
  PIC  <-  round(GenSummary[,2], SG)
  MisP <-  round(GenSummary[,3], SG)
  MAF  <-  round(GenSummary[,4], SG)
}

names(GenSummary)<-c("Marker","PIC", "Missing(%)", "MAF")
######################################################################################################################

SelectedS <- c(1,APIC, AMISP, AMAF) + 1
SelectedS <- as.logical(SelectedS)
GSum<-GenSummary[SelectedS]

### Histogram for Polymorphic Information Content
PNGNamePIC <- paste(getwd(), "/", GetRFileName(), ".png", sep="");
THRes<-png(PNGNamePIC , width=IR*10*ppi, height=IR*10*ppi, res=ppi)

nbreaks<-seq(0,0.5,by=0.05)
b<-hist(PIC,breaks=nbreaks,plot=F)
per_PIC<-round((b$count/length(PIC)*100),2)
lab<-paste(b$count,"\n(", per_PIC, "%)", sep="")
par(cex=0.5,xpd=T)
hist(PIC,breaks=nbreaks,col="lightblue",axes=F,labels=lab,main="Polymorphics Information Content", xlab="PIC", ylab="Number of Markers")
rug(PIC)
axis(2)
axis(1, at=nbreaks,las=2)
dev.off()

### Histogram for % missing markers
PNGNameMISP <- paste(getwd(), "/", GetRFileName(), ".png", sep="");
THRes<-png(PNGNameMISP , width=IR*10*ppi, height=IR*10*ppi, res=ppi)
nbreaks<-seq(0,100,by=10)
b<-hist(MisP,breaks=nbreaks,plot=F)
per_miss<-round((b$count/length(MisP)*100),2)
lab<-paste(b$count,"\n(", per_miss, "%)", sep="")
par(cex=0.5,xpd=T)
hist(MisP,breaks=nbreaks,col="lightblue",axes=F,labels=lab,main="Histogram of %Missing Markers", xlab="%Missing", ylab="Number of Markers")
rug(MisP)
axis(2)
axis(1, at=nbreaks,las=2)
dev.off()

### Histogram for Minor Allele Frequency

PNGNameMAF <- paste(getwd(), "/", GetRFileName(), ".png", sep="");
THRes<-png(PNGNameMAF , width=IR*10*ppi, height=IR*10*ppi, res=ppi)

nbreaks<-seq(0,1,by=0.1)
c<-hist(MAF,breaks=nbreaks,plot=F)
per_MAF<-round((c$count/length(MAF)*100),2)
lab<-paste(c$count,"\n(", per_MAF, "%)", sep="")
par(cex=0.5,xpd=T)
hist(MAF,breaks=nbreaks,col="lightblue",axes=F,labels=lab,main="Minor Allele Frequency", xlab="MAF", ylab="Number of Markers",)
rug(MAF)
axis(2)
axis(1, at=nbreaks,las=2)
dev.off()

######################################################################################################################

write.csv(GSum,SumFileName, quote=F)
cat("\n Genotype Data Summary File Saved!\n")

# Create HTML File
# As there are Problems with HTMLStart() in UNIX , we willl write our own code to create a HTML file

GSRes <- file(ARFILE, "wt")   #Open in APPEND Mode to write results of RR

DCTitle <-"ISMU Genomic Selection Pipeline V.2.0"
cat(paste("<html> <head>  <title>", DCTitle,"</title>  <link rel=stylesheet href=ISMU.css type=text/css></head><body>"), file=GSRes)
close(GSRes) 

# Copy ISMU.CSS File to Current Working Directoy
FromCSSFile <- paste(oldwd, "/ISMU.css", sep="")
TOCSSFile <- paste(getwd(), "/ISMU.css", sep="")

file.copy(FromCSSFile, TOCSSFile)

# DONE, Creating HTML File
# Write TOp heading in HTML File
THRes<-HTML.title(paste("Genotype Summary ") , HR=1, file=ARFILE, Append=T)

#  THRes<-HTMLStart(outdir=getwd(), file=ARFILE, extension="", HTMLframe=F, Title=Title, BackGroundColor = "lightblue", autobrowse=F)
if(!APIC == -1)
{
  TRes<-HTMLInsertGraph(PNGNamePIC,file=ARFILE , WidthHTML="50%", append=T) 
  TRes<-HTML("<div align=\"center\">", file=ARFILE, Append=T)
  TRes<-HTML(paste("<b align=\"center\">PIC </b><br>"), HR=4, file=ARFILE, Append=T, Align="center")
}

if(!AMISP == -1)
{
  TRes<-HTMLInsertGraph(PNGNameMISP,file=ARFILE , WidthHTML="50%",append=T) 
  TRes<-HTML("<div align=\"center\">", file=ARFILE, Append=T)
  TRes<-HTML(paste("<b align=\"center\">% of Missing Markers </b><br>"), HR=4, file=ARFILE, Append=T, Align="center")
}

if(!AMAF == -1)
{
  TRes<-HTMLInsertGraph(PNGNameMAF,file=ARFILE , WidthHTML="50%",append=T) 
  TRes<-HTML("<div align=\"center\">", file=ARFILE, Append=T)
  TRes<-HTML(paste("<b align=\"center\">MAF</b><br>"), HR=4, file=ARFILE, Append=T, Align="center")
}

TRes<-HTML(paste("<b align=\"center\">Marker Statistics</b><br>"), HR=4, file=ARFILE, Append=T, Align="center")
TRes<-HTML(GSum, file=ARFILE, Append=T)
TRes<-HTML("</div>", file=ARFILE, Append=T)

# Write Time and Logo
TRes<-HTML("<hr/>", file=ARFILE, Append=T)
TRes<-HTML(paste("<p align=left>Results Generated by ISMU 3.0 : ", format(Sys.time(), "%a %b %d %X %Y")), file=ARFILE, Append=T)

# Close HTML FIle
GSRes <- file(ARFILE, "at")   #Open in APPEND Mode to write results of RR
cat("</body></html>", file=GSRes)
close(GSRes) 

setwd(oldwd)
sink()
file.remove(TFile)