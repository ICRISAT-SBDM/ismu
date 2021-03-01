#################################################################
# Bayesian LASSO
# y: vector of phenotypes, no missing values
# z: matrix of marker scores, lines in rows, markers in columns
# x: design matrix of fixed effects (there may be more than one)
# model is y = x %*% beta + z %*% alpha + e 
# where beta are the fixed effects and x the design matrix for them
# alpha are the marker effects and z the marker score matrix
# varA is a prior value for the additive genetic variance of the trait
# Use the BLR package and its BLR function to perform a Bayesian LASSO
# Important reference: Perez et al. The Plant Genome 3:106-116
# NOTE: the BLR package can fit a much broader range of models than is included here
#################################################################
bayesLasso <- function(y, z, x=matrix(1, nrow=length(y)), varA=NULL, nIter=2000, burnIn=500, thin=5){
	
# estimate variance components on phenotypes adjusted for fixed effects
	xTxInv <- solve(crossprod(x))
	beta <- c(xTxInv %*% crossprod(x, y))
	yAdj <- y - x %*% beta
	dfE <- 3 # prior degrees of freedom for error
	varE <- var(yAdj) / 2 # prior variance for error. For error the data should overwhelm the prior.

# either the user gives the add gen var, varA, or its "empirical Bayes" and varA should be estimated
	if (is.null(varA)){
		print("WARNING: analysis may be sensitive to add gen var estimate (which was not given)")
		library(rrBLUP)
		ms.out <- mixed.solve(y, K=A.mat(z), X=x)
		varA <- ms.out$Vu
		varE <- ms.out$Ve # might as well get a good prior here too
		print(paste("Estimated add gen var:", varA))
	}
	
# set up prior values to call BLR
	adjustPerez <- sum(colMeans(z)^2) # this formula is recommended in the published article
	adjustJannink <- mean(rowSums(z^2)) # I think this formula is correct. I don't think the two differ much
	lambda <- sqrt(2 * varE / varA * adjustPerez)
	prior <- list(
		varE=list(S=varE * (dfE + 2), df=dfE),
		lambda=list(type="fixed", value=lambda)
	)
	blr.out <- BLR(y=y, XF=x, XL=z, nIter=nIter, burnIn=500, thin=thin, prior=prior)
	return(list(predVals=z %*% blr.out$bL, fixedEffects=blr.out$bF, markerEffects=blr.out$bL, errorVar=blr.out$varE))
}
