
# function definition for converting allelic data to 1 0 -1 data
CodedSNP <- function (Marker)
{
  # A:A = 1
  # A:T = 0
  # T:A = 0
  # T:T = -1
  TotalAlleles <- length(levels(as.factor(Marker[,1])))
  MLevels <- levels(as.factor(Marker[,1]))
  Nucleotides <- vector("character", 2)
  Nucleotides[1]<-  substr(MLevels[1], 1, 1)
  NoNucleotides<-1;
  
  for( i in 1:length(MLevels) )
  {
    for(j in c(1,3) )
    {
      if (! (substr(MLevels[i], j, j) %in% Nucleotides )  )
      {
        NoNucleotides <- NoNucleotides + 1
        Nucleotides[NoNucleotides] <- substr(MLevels[i], j, j)        
      }
    }
  }
  NumMarker <- rep(-1, nrow(Marker))
  for( i in 1:nrow(Marker) )
  {
    if(is.na(Marker[i,1])==FALSE)
    {
      A1<-   substr(Marker[i,1],1,1) %in% Nucleotides[1]
      A2<-   substr(Marker[i,1],3,3) %in% Nucleotides[1]
      Total <- A1+A2 
      NumMarker[i] <- NumMarker[i] +Total;
    } else     
    {  
      NumMarker[i]=NA 
    }
  }
  return (NumMarker)
}







