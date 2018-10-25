# Program to divide data and do crossvalidation
runCrossVal <- function(pheno, geno=NULL, designFixed, pedigree=NULL, nFolds, nRepl, n.core=1, inGUI=TRUE, method,nIter,burnIn,thin,ntree, ...)
{
  cat ("\nDoing Crossvalidation for \"", method, "\"...\n");
  # Make the cross validation replicates into a list for parallel analysis
  crossValList <- list()
  for (repl in 1:nRepl){
    folds <- sample(rep(1:nFolds, length.out=nrow(pheno)))
    
    for (fold in 1:nFolds){
      validPop <- which(folds == fold)
      crossValList <- c(crossValList, list(validPop))
    }
  }
  
  # Function to run the cross validation given all the objects in crossValObj
    oneCrossVal <- function(validPopIdx){
    phenoTrain <- pheno
    phenoTrain[validPopIdx, 2] <- NA
    gp.out <- genomicPrediction(phenoTrain, geno,designFixed=designFixed, pedigree, method,nIter,burnIn, ntree, ...)
    if (!method=="randomForest")
    {
      predicted<-designFixed%*% as.matrix(gp.out$fixedEffects,ncol=1)+ matrix(gp.out$predVals,ncol=1)
      fixedEffects.out<-gp.out$fixedEffects
      #return(cat("Step \n",fixedEffects.out))
      return(cor(pheno[validPopIdx, 2], predicted[validPopIdx], use="pairwise.complete.obs"))
    } else {
      predicted<-matrix(gp.out$predVals,ncol=1)
      #return(cat("Step \n",fixedEffects.out))
      return(cor(pheno[validPopIdx, 2], predicted[validPopIdx], use="pairwise.complete.obs")) 
    }
    
  }
  
  if (inGUI | n.core == 1){
    res<-unlist(lapply(crossValList, oneCrossVal))
    cat ("\nCrossvalidation for \"", method, "\"finished.\n");
    
    return(res)
  } else{
    res<-unlist(mclapply(crossValList, oneCrossVal, mc.cores=n.core))
    cat ("\nCrossvalidation for \"", method, "\"finished.\n");
    
    return(res)
  }
  
}
#END runCrossVal
