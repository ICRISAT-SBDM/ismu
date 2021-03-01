#################################################################
# Install the packages that are needed for these scripts
# NOTE: the repository to go get those packages (repos=...) is good if you are in Ithaca, NY
# maybe not so good if you are in Hyderabad, AP
# needPackages <- c("rrBLUP", "multicore", "BLR", "randomForest")
# needPackages <- needPackages[!needPackages %in% installed.packages()]
# if (length(needPackages) > 0) install.packages(needPackages, repos="http://lib.stat.cmu.edu/R/CRAN")

#################################################################
# Genomic Prediction
# Designed for a two-step analysis where, in a prior analysis, one
# phenotypic value is attributed to each unique individual
# pheno: nObs x 2 matrix with lineIDs in column 1 and phenotypes in column 2
# geno: nInd x nMrk matrix of marker scores, lines in rows, markers in columns
# 	row names are unique lineIDs
# NOTE: individuals need not have been observed
# 	i.e., lineIDs may exist in geno that do not exist in pheno
# pedigree: nInd x 3 dataframe with the columns lineID, damID, and sireID
# the pedigree may include individuals who have no phenotype.
# designFixed: nObs x nFixed design matrix for fixed effects
# designRandom: nObs x nInd design matrix of individuals treated as random effects
# this only works for some methods
# method: must be among "ridgeRegression", "BayesCpi", "BayesB", "BayesLASSO",
# "pedigreeBLUP", "randomForest"
# ... : the various methods have different parameters that are needed so you
# have to know that and supply them else defaults will be used

# At a minimum, genomicPrediction should return
# predVals: predictions for all individuals with a genotype or that are in the pedigree
# fixedEffects: estimates of fixed effects
# varE: estimate of the error variance

# Important references:
# Endelman, J.B. 2011. Ridge regression and other 
# 	kernels for genomic selection with R package rrBLUP. The Plant Genome 4:250-255.
# Perez et al. The Plant Genome 3:106-116
#################################################################
genomicPrediction <- function(pheno, geno=NULL, pedigree=NULL, designFixed=NULL, designRandom=NULL, method="ridgeRegression", ...){
	methods <- c("ridgeRegression", "kinshipGauss", "BayesCpi", "BayesB", "BayesLASSO", "pedigreeBLUP", "randomForest")
# Make sure method is one that is supported
	whichMethod <- which(method == methods)
	if (length(whichMethod) == 0) stop(paste("Method must be among", methods))
	if (is.null(geno) & is.null(pedigree)) stop("Must have either marker genotypes or pedigree")
	
# Extra arguments
# Each method may require (or at least benefit from) some extra arguments
# Those can be passed when calling genomicPrediction (see Example)
# For each method, the function checks whether the argument has been passed [e.g., if(!exists("n.core"))]
# and if not assigns a default value
# NOTE: the defaults assigned may not provide maximal accuracy: that needs to be checked by cross validation
	#extraArgs <- list(...)
	#for (v in 1:length(extraArgs)) assign(names(extraArgs)[v], extraArgs[[v]])

# Analysis dimensions
	nInd <- nrow(geno)
	hasPheno <- !is.na(pheno[,2])

# If needed calculate the random effect incidence matrix
	if (is.null(designRandom)){
		calcZcolumn <- function(lineName, lineIDs){
			return(ifelse(lineName == lineIDs, 1, 0))
		}
		if (whichMethod == 6){
			colnames(pedigree) <- c("lineID", "damID", "sireID")
			designRandom <- sapply(pedigree$lineID, calcZcolumn, pheno[,1])
		} else{
			designRandom <- sapply(rownames(geno), calcZcolumn, pheno[,1])
		}
	}
	
# If needed set designFixed as a vector of ones
	if (is.null(designFixed)) designFixed <- matrix(1, nrow=nrow(pheno))
	
######## ridge regression
# mixed.solve makes this very easy
# this method can handle a "one-step" approach
	if (whichMethod == 1){ # ridgeRegression
		library(rrBLUP)
		if(!exists("n.core")) n.core <- 1
		relMat <- A.mat(geno, n.core=n.core)
		rr.out <- mixed.solve(pheno[,2], designRandom, relMat, designFixed)
		return(list(predVals=rr.out$u, fixedEffects=rr.out$beta, varA=rr.out$Vu, varE=rr.out$Ve))
	}
	
######## kinship matrix is a Gaussian kernel
# kinship.BLUP makes this very easy
# this method can handle a "one-step" approach
	if (whichMethod == 2){ # kinshipGauss
		library(rrBLUP)
		if(!exists("n.core")) n.core <- 1
		
		# split genotypes according to whether they have a phenotype or not
		hasPheno <- rownames(geno) %in% pheno[,1]
		genoTrain <- geno[hasPheno,]
		genoPred <- geno[!hasPheno,]
		kg.out <- kinship.BLUP(pheno[,2], genoTrain, genoPred, designFixed, designRandom, "GAUSS", n.core=n.core)
		predVals <- numeric(nInd)
		predVals[hasPheno] <- kg.out$g.train
		predVals[!hasPheno] <- kg.out$g.pred
		return(list(predVals=predVals, fixedEffects=kg.out$beta))
	}
	
######## BayesCpi
# this method cannot handle a "one-step" approach
	if (whichMethod == 3){
		#source(paste(oldwd, "/bayesCpi.R", sep=""))
		if(!exists("varA")) varA <- NULL
		if(!exists("nIter")) nIter <- 2000
		if(!exists("burnIn")) burnIn <- 500

		# take out missing phenotypes and the corresponding rows of designFixed and designRandom
		pheno <- pheno[hasPheno, 2]
		designFixed <- designFixed[hasPheno,,drop=FALSE]
		genoTrain <- geno[hasPheno,]
    
		################################################################
		### This is for Dart markers...Have to modify code for SNP data
		MColSum <-colSums(genoTrain) 
		ColPolyM<-(MColSum == nrow (genoTrain)) | (MColSum  == 0)
		geno2<-genoTrain[,!ColPolyM]
		genoTrain<-geno2
		geno<-geno[,!ColPolyM]
		################################################################
		
    
    
		bc.out <- bayesCpi(pheno, genoTrain, designFixed, varA, nIter, burnIn)
		predVals <- numeric(nInd)
		predVals[hasPheno] <- bc.out$predVals
		predVals[!hasPheno] <- geno[!hasPheno,] %*% bc.out$markerEffects
		return(list(predVals=predVals, fixedEffects=bc.out$fixedEffects, markerEffects=bc.out$markerEffects, markerInModel=bc.out$markerInModel, probNoEffect=bc.out$probNoEffect, varE=bc.out$errorVar))
	}
	
######## BayesB
# this method cannot handle a "one-step" approach
	if (whichMethod == 4){
		#source(paste(oldwd, "/bayesBparallel.R", sep=""))
		if(!exists("varA")) varA <- NULL
		if(!exists("probPi")) probPi <- 0.9
		if(!exists("nIter")) nIter <- 2000
		if(!exists("burnIn")) burnIn <- 500
		if(!exists("thin")) thin <- 5
		if(!exists("n.core")) n.core <- 1
		if(!exists("nChain")) nChain <- n.core
		# if there are more cores than requested chains, might as well take advantage of it
		nChain <- max(nChain, n.core)

		# take out missing phenotypes and the corresponding rows of designFixed and designRandom
		pheno <- pheno[hasPheno, 2]
		designFixed <- designFixed[hasPheno,,drop=FALSE]
		genoTrain <- geno[hasPheno,]
		
		################################################################
		### This is for Dart markers...Have to modify code for SNP data
		MColSum <-colSums(genoTrain) 
		ColPolyM<-(MColSum == nrow (genoTrain)) | (MColSum  == 0)
		geno2<-genoTrain[,!ColPolyM]
		genoTrain<-geno2
		geno<-geno[,!ColPolyM]
		################################################################
		
    
    
    
    bc.out <- bayesBparallel(pheno, genoTrain, designFixed, varA, probPi, nIter, burnIn, thin, nChain)
		predVals <- numeric(nInd)
		predVals[hasPheno] <- bc.out$predVals
		predVals[!hasPheno] <- geno[!hasPheno,] %*% bc.out$markerEffects
		return(list(predVals=predVals, fixedEffects=bc.out$fixedEffects, markerEffects=bc.out$markerEffects, markerVar=bc.out$markerVar, markerInModel=bc.out$markerInModel, varE=bc.out$errorVar))
	}
	
######## Bayesian LASSO
# this method cannot handle a "one-step" approach
	if (whichMethod == 5){
		library(BLR)
		#source(paste(oldwd, "/bayesLasso.R", sep=""))
		BLRenv <- environment(BLR)
		#source(paste(oldwd, "/BLRnonVerbose.R", sep=""))
		environment(BLR) <- BLRenv
		if(!exists("varA")) varA <- NULL
		if(!exists("nIter")) nIter <- 2000
		if(!exists("burnIn")) burnIn <- 500
		if(!exists("thin")) thin <- 5
		
		# take out missing phenotypes and the corresponding rows of designFixed and designRandom
		pheno <- pheno[hasPheno, 2]
		designFixed <- designFixed[hasPheno,,drop=FALSE]
		genoTrain <- geno[hasPheno,]

		################################################################
		### This is for Dart markers...Have to modify code for SNP data
		MColSum <-colSums(genoTrain) 
		ColPolyM<-(MColSum == nrow (genoTrain)) | (MColSum  == 0)
		geno2<-genoTrain[,!ColPolyM]
		genoTrain<-geno2
		geno<-geno[,!ColPolyM]
		################################################################
		
    
    bl.out <- bayesLasso(pheno, genoTrain, designFixed, varA, nIter, burnIn, thin)
		predVals <- numeric(nInd)
		predVals[hasPheno] <- bl.out$predVals
		predVals[!hasPheno] <- geno[!hasPheno,] %*% bl.out$markerEffects
		return(list(predVals=predVals, fixedEffects=bl.out$fixedEffects, markerEffects=bl.out$markerEffects, varE=bl.out$errorVar))
	}
	
######## BLUP from pedigree-based additive relationship matrix
# this method can handle a "one-step" approach
	if (whichMethod == 6){ # pedigreeBLUP
		#source(paste(oldwd, "/pedigreeBLUP.R", sep=""))
		
		pb.out <- pedigreeBLUP(pheno, pedigree, designFixed, designRandom)
		return(list(predVals=pb.out$predVals, fixedEffects=pb.out$beta, varA=pb.out$addVar, varE=pb.out$errorVar))
	}
	
######## Random Forest
# this method cannot handle a "one-step" approach
	if (whichMethod == 7){
		library(randomForest)
		
		# NOTE: randomForest doesn't know what to do with fixed or random design matrices
		if(!exists("ntree")) ntree <- 1000
		
		# take out missing phenotypes and the corresponding rows of designFixed and designRandom
		pheno <- pheno[hasPheno, 2]
		genoTrain <- geno[hasPheno,]
		rf.out <- randomForest(genoTrain, pheno, geno, ntree=ntree)
		return(list(predVals=rf.out$test$predicted, markerImportance=rf.out$importance, varE=rf.out$mse))
	}
}#END genomicPrediction
