# This program  will call runCrossVal() and will take mean for all crossvalidation values
crossVR<- function(Replications =2, Folds =4,methods)
{ 
  hasPheno <- !is.na(pheno[,2])
  phenoNoNA <- pheno[hasPheno,]
  genoNoNA <- geno[hasPheno,]
  designFixedNoNA <- as.matrix(designFixed[hasPheno,])
  

  
  CVVector <- NULL
  correl<-runCrossVal(phenoNoNA, genoNoNA, designFixed=designFixedNoNA,designRandom=NULL,nFolds=Folds, nRepl=Replications, method=methods,nIter=NIter,burnIn=NBurnIn,thin=NThin,ntree=NTree)
  CVVector <- c(CVVector,   correl)
  print(correl)
  #cat (correl)
  
  
  print(CVVector)
  #cat(CVVector)
  res<-t.test(CVVector)
  
  
  sink()
  CVP = data.frame(CV=mean(CVVector), p.value=res$p.value)
  return(CVP)
  
}
