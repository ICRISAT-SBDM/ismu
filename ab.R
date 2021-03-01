args<-commandArgs(TRUE)
TFile<-tempfile()
#TFile
sink(TFile)

#path = "c:\Program Files\r\R-3.0.1\bin\x64"
#cd D:\ICRISAT\Work\Analysis\Work\ISMU\ISMU  

GetRFileName <- function (start = "RES_")
{
  paste(start, substring(date(), 5, 7), substring(date(), 9, 10), "_", substring(date(), 12, 13), substring(date(), 15, 16), substring(date(), 18, 19), abs(round(rnorm(1),3)), sep = "")  
}

require("genetics", quietly=T)
require(R2HTML)

#args <- c("C:/Users/admin/Documents/2", "Result.htm", -1, 2, -1, 2,-1,-1,"GSResTMP.txt")

AWD       <- args[1]
ARFILE	  <- args[2]
AGSMETHOD <- args[3]
ANCORE	  <- as.numeric(args[4])
AMISSCH	  <- args[5]
ATraitNo  <-as.numeric( args[6])
AMTYPE    <- as.numeric( args[7])
ASEP      <- args[8]
ATEMP_FILE_CORR    <- args[9]   # Will Store: Method, Coorel and  P value

oldwd<-setwd(AWD)



####################################################################
# Creat a New Temp File for this session, This name will be same for all GS Runs

GSRes <- file(ATEMP_FILE_CORR, "wt")   #Overwrite all times for each trait
cat("Method\tCorrelation\tProb>t", file=GSRes)
# File closed. Now will be opened in several GS Modules in "at" mode
close(GSRes)

#load data prepared by a.r
load("gsdata.rdata")
# Now read Trait Name from here
TraitName<-colnames(FinalBLUP)[ATraitNo+1]

# Create a new file for GS HTML
Title <- "ISMU Genomic Selection Pipeline V.3.0"
#THRes<-HTMLInitFile(outdir=getwd(), file=ARFILE, extension="", HTMLframe=F, Title=Title)
#THRes<-HTMLStart(outdir=getwd(), file=ARFILE, extension="", HTMLframe=F, Title=Title, BackGroundColor = "lightblue", autobrowse=F)

# Create HTML File
# As there are Problems with HTMLStart() in UNIX , we willl write our own code to create a HTML file

GSRes <- file(ARFILE, "wt")   #Open in APPEND Mode to write results of RR


cat(paste("<html> <head>  <title>", Title,"</title>  <link rel=stylesheet href=ISMU.css type=text/css></head><body>"), file=GSRes)
close(GSRes) 

# Copy ISMU.CSS File to Current Working Directoy
FromCSSFile <- paste(oldwd, "/ISMU.css", sep="")
TOCSSFile <- paste(getwd(), "/ISMU.css", sep="")

file.copy(FromCSSFile, TOCSSFile)

# DONE, Creating HTML File




THRes<-HTML.title(paste("GS Analysis Results for ",TraitName) , HR=1, file=ARFILE, Append=T )

PSummary <- summary(FinalBLUP[,ATraitNo+1])
TRes<-HTML.title("Phenotypic Summary", HR=3, file=ARFILE, Append=T)
TRes<-HTML(PSummary, HR=1, file=ARFILE,  Append=T)

ppi<-300  # Resolution
IR <- .5  # Image Ratio
SG <- 3   #Significant Decimal Digits


PNGNameTrait1 <- paste(getwd(), "/", GetRFileName(), ".png", sep="");
THRes<-png(PNGNameTrait1, width=IR*10*ppi, height=IR*10*ppi, res=ppi)
TRes<-hist(FinalBLUP[,ATraitNo+1], xlab=TraitName,main="Histogram",col="lightblue")
TRes<-rug(FinalBLUP[,ATraitNo+1])
TRes<-box()
THRes<-dev.off()

PNGNameTrait2 <- paste(getwd(), "/", GetRFileName(), ".png", sep="");
THRes<-png(PNGNameTrait2, width=IR*10*ppi, height=IR*10*ppi, res=ppi)
TRes<-boxplot(FinalBLUP[,ATraitNo+1], xlab=TraitName,main="Boxplot",col="lightblue")
TRes<-box()
THRes<-dev.off()



TRes<- HTMLInsertGraph(PNGNameTrait1, file=ARFILE)
TRes<-HTML("<div align=\"center\">", file=ARFILE, Append=T)
TRes<-HTML(paste("<b align=\"center\">Histogram of ", TraitName,"</b>"), HR=4, file=ARFILE, Append=T, Align="center")
TRes<-HTML("</div>", file=ARFILE, Append=T)

TRes<- HTMLInsertGraph(PNGNameTrait2, file=ARFILE)
TRes<-HTML("<div align=\"center\">", file=ARFILE, Append=T)
TRes<-HTML(paste("<b align=\"center\">Boxplot of ", TraitName,"</b>"), HR=4, file=ARFILE, Append=T, Align="center")
TRes<-HTML("</div>", file=ARFILE, Append=T)




# THRes<-HTML.title("GS REsults for", HR=2)
# THRes<-HTML.title("GS REsults for", HR=3)
# THRes<-HTML.title("GS REsults for", HR=4)
# THRes<-HTML.title("GS REsults for", HR=5)
# HTMLStop()

sink()

setwd(oldwd)
file.remove(TFile)