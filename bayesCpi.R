#################################################################
# Bayes-CPi
# y: vector of phenotypes, no missing values
# z: matrix of marker scores, lines in rows, markers in columns
# 	rows in same order as vector of phenotypes
# x: design matrix of fixed effects (there may be more than one)
# model is y = x %*% beta + z %*% alpha + e 
# where beta are the fixed effects and x the design matrix for them
# alpha are the marker effects and z the marker score matrix
# varA is a prior value for the additive genetic variance of the trait
#################################################################
bayesCpi <- function(y, z, x=matrix(1, nrow=length(y)), varA=NULL, nIter=2000, burnIn=500){

# Analysis dimensions
	nObs <- length(y)
	nFixed <- ncol(x)
	nMrk <- ncol(z)
	
# constants for sampling the fixed effects, beta
# this is overkill of you only have an intercept
	xTxInv <- solve(crossprod(x))
	sigmab_const <- t(chol(xTxInv))
	beta <- c(xTxInv %*% crossprod(x, y))

# constants for sampling marker effects
	allzTz <- apply(z, 2, crossprod)

# Parameters
	probPi <- 0.5
	logPi <- log(probPi)
	logPiComp <- log(1 - probPi)
	sumMrkVar <- sum(apply(z, 2, var))
	
# adjust y
	yAdj <- y - x %*% beta
	
# prior parameters for error effects
	dfE <- 3 # prior degrees of freedom for error
	scaleE <- var(yAdj) / 2 # prior variance for error. For error the data should overwhelm the prior.

# either the user gives the add gen var, varA, or its "empirical Bayes" and varA should be estimated
	if (is.null(varA)){
		print("WARNING: analysis may be sensitive to add gen var estimate (which was not given)")
		library(rrBLUP)
		ms.out <- mixed.solve(y, K=A.mat(z), X=x)
		varA <- ms.out$Vu
		scaleE <- ms.out$Ve # might as well get a good prior here too
		print(paste("Estimated add gen var:", varA))
	}
	dfA <- 4 # prior degrees of freedom for marker effect variance

# inital values
	varAlpha <- varA / ((1 - probPi) * sumMrkVar)
	scaleA <- (dfA - 2) / dfA * varAlpha
	alpha <- numeric(nMrk)

# Storage vectors and matricies
	meanVarE <- 0
	meanAlpha <- numeric(nMrk)
	meanBeta <- numeric(nFixed)
	meanPi <- 0
# how often a marker is in
	ppAlpha <- numeric(nMrk)

# mcmc sampling
for (iter in 1:nIter){
	######## Step 1.  Sample varE from an inverse chi-square posterior
	varE <- (scaleE*dfE + crossprod(yAdj)) / rchisq(1, nObs + dfE)

# sample possibly multiple fixed effects
	betaHat <- xTxInv %*% crossprod(x, yAdj) + beta
	betaNew <- c(betaHat + sigmab_const %*% rnorm(nFixed, 0, sqrt(varE)))
	yAdj <- yAdj + x %*% (beta - betaNew)
	beta <- betaNew
	
# sample delta and effect for each locus
	for (locus in 1:nMrk){
		yAdj <- yAdj + z[,locus]*alpha[locus]
		rhs <- crossprod(z[,locus], yAdj)
		zTz <- allzTz[locus]
		v0 <- zTz*varE
		v1 <- zTz^2*varAlpha + v0
		logDelta0 <- -0.5*(log(v0) + rhs^2/v0) + logPi 
		logDelta1 <- -0.5*(log(v1) + rhs^2/v1) + logPiComp
		probDelta1 <- 1/(1 + exp(logDelta0 - logDelta1))
		if (runif(1) < probDelta1) {
			invLhs <- 1 / (zTz/varE + 1/varAlpha)
			alpha[locus] <- rnorm(1, invLhs * rhs / varE, sqrt(invLhs))
			yAdj <- yAdj - z[,locus]*alpha[locus]
		} else alpha[locus] <- 0
	} #END go through all markers
	nonZeroVar <- alpha != 0
	
# sample common variance 
	nQTL <- sum(nonZeroVar)
	sumSq <- sum(alpha[nonZeroVar]^2)
	# nu^tilda_a = nQTL + nu_a
	# scaleA is S^2_alpha
	varAlpha <- (sumSq + dfA * scaleA) / rchisq(1, nQTL + dfA)
	
	if (iter %% 100 == 0){
		cat("Iteration ",iter," number of loci in model = ", nQTL,"\n")
	}
# sample Pi
	aa <- nMrk - nQTL + 1
	bb <- nQTL + 1
	probPi <- rbeta(1, aa, bb)
	scaleA <- (dfA - 2) / dfA * varA / ((1 - probPi) * sumMrkVar)
	logPi <- log(probPi)
	logPiComp <- log(1-probPi)

# save MCMC samples
	if (iter > burnIn){
		meanVarE <- meanVarE + varE
		meanAlpha <- meanAlpha + alpha
		meanBeta <- meanBeta + beta
		ppAlpha[nonZeroVar] <- ppAlpha[nonZeroVar] + 1
		meanPi <- meanPi + probPi
	}
}#END run through all the iterations
	nSaveIter <- nIter - burnIn
	meanVarE <- meanVarE / nSaveIter
	meanAlpha <- meanAlpha / nSaveIter
	meanBeta <- meanBeta / nSaveIter
	ppAlpha <- ppAlpha / nSaveIter
	meanPi <- meanPi / nSaveIter
	indBLUP <- z %*% meanAlpha

	return(list(predVals=indBLUP, fixedEffects=meanBeta, markerEffects=meanAlpha, markerInModel=ppAlpha, probNoEffect=meanPi, errorVar=meanVarE))
} #END predictUsingBayesCPi
