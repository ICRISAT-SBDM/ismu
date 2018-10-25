#####################################################
# BayesB (Meuwissen et al. 2001)
#####################################################
# Requires the multicore package for parallel processing of MCMC chains
# If the additive genetic variance has not been estimated a prior, requires the rrBLUP package to estimate it
# y: vector of phenotypic values
# z: matrix of marker scores 
# x: incidence matrix for fixed effects. Defaults to an intercept.
# model is y = x %*% beta + z %*% alpha + e 
# where beta are the fixed effects and x the design matrix for them
# alpha are the marker effects and z the marker score matrix
# varA is a prior value for the additive genetic variance of the trait
# probPi: prior probability that a marker has NO effect
# nIter: total number of MCMC iterations, including burnIn
# burnIn: number of MCMC iterations used to reach the stationary distribution from initial values
# thin: samples from MCMC iterations are serially correlated. Keep one out of every [thin] samples.
# nChain: you can assess convergence more reliably if you run > 1 chain
# Though I do not give any convergence diagnostics here.  This function runs chains in parallel, so
# running more chains hardly takes more time if you have a multicore processor.
bayesBparallel <- function(y, z, x=matrix(1, nrow=length(y)), varA=NULL, probPi=0.9, nIter=6000, burnIn=1000, thin=5, nChain=2, n.core=1){
	
# Parameters needed to optimize algorithm (don't honestly know what optimizes speed versus good sampling)
	nMHperIter <- 10 # To improve mixing, several attempts at changing marker effects are done in each iteration
# Analysis dimensions
	nObs <- length(y)
	nFixed <- ncol(x)
	nMrk <- ncol(z)

# Variables for fixed effects, beta
	xTxInv <- solve(crossprod(x))
	sigmab_const <- t(chol(xTxInv))
	beta <- xTxInv %*% crossprod(x, y)

# Variables for marker effects
	allzTz <- apply(z, 2, crossprod)
	alpha <- numeric(nMrk)
	varAlpha <- numeric(nMrk)

# Adjust y
	yAdj <- y - x %*% beta
	
# prior parameters for error effects
	dfE <- 3 # prior degrees of freedom for error
	scaleE <- var(yAdj) / 2 # prior variance for error. For error the data should overwhelm the prior.

# prior parameters for marker effect variances
	dfA <- 3 # prior degrees of freedom for marker effect variance
	sumMrkVar <- sum(apply(z, 2, var))
	# either the user gives the add gen var, varA, or its "empirical Bayes" and varA should be estimated
	if (is.null(varA)){
		print("WARNING: analysis may be sensitive to add gen var estimate (which was not given)")
		library(rrBLUP)
		ms.out <- mixed.solve(y, K=A.mat(z), X=x)
		varA <- ms.out$Vu
		scaleE <- ms.out$Ve # might as well get a good prior here too
		print(paste("Estimated add gen var:", varA))
	}
	scaleA <- (dfA - 2) / dfA * varA / ((1 - probPi) * sumMrkVar)

# Storage vectors and matricies
	meanVarE <- 0
	meanBeta <- numeric(nFixed)
	meanAlpha <- numeric(nMrk)
	meanVarAlpha <- numeric(nMrk)
	ppAlpha <- numeric(nMrk)
	
# Make the list of parameters so that you can run using lapply
	bayesBparms <- as.list(environment())
	for (chain in 1:nChain) if (chain == 1) parms <- list(bayesBparms) else parms <- c(parms, list(bayesBparms))
		
######################################################################################################
# Define a function so that you can mclapply the chains
runBayesBchain <- function(bayesBparms){
	# Assign all variables in bayesBparms to the function environment
	for (v in 1:length(bayesBparms)) assign(names(bayesBparms)[v], bayesBparms[[v]])
	
### Beginning of MCMC iterations 
	for (iter in 1:nIter){
		######## Step 1.  Sample varE from an inverse chi-square posterior
		varE <- (scaleE*dfE + crossprod(yAdj)) / rchisq(1, nObs + dfE)
		
		######## Step 2.  Sample possibly multiple fixed effects from a normal posterior
		betaHat <- xTxInv %*% crossprod(x, yAdj) + beta
		betaNew <- betaHat + sigmab_const %*% rnorm(nFixed, 0, sqrt(varE))
		yAdj <- yAdj + x %*% (beta - betaNew)
		beta <- betaNew
				
   		######## Step 3.  Sample the alpha (marker effect) from a normal distribution 
		for (mrk in 1:nMrk){
			yAdj <- yAdj + z[,mrk] * alpha[mrk]
			rhs <- crossprod(z[,mrk], yAdj)
			zTz <- allzTz[mrk]
			v0 <- zTz*varE
			logDelta0 <- -0.5*(log(v0) + rhs^2/v0)
			logDataOld <- ifelse(varAlpha[mrk],
								{v1 <- zTz^2*varAlpha[mrk] + v0; -0.5*(log(v1) + rhs^2/v1)}, logDelta0)
			nNZattempts <- rbinom(1, nMHperIter, 1 - probPi)
			nzAttemptIdx <- sample(nMHperIter, nNZattempts)
			varCan <- numeric(nMHperIter)
			varCan1 <- scaleA * dfA / rchisq(nNZattempts, dfA)
			varCan[nzAttemptIdx] <- varCan1
			v1Can <- zTz^2*varCan1 + v0
			logDataNew <- rep(logDelta0, nMHperIter)
			logDataNew[nzAttemptIdx] <- -0.5*(log(v1Can) + rhs^2/v1Can)
			for (attempt in 1:nMHperIter){
				if ((varAlpha[mrk] | varCan[attempt]) && (runif(1) < exp(logDataNew[attempt] - logDataOld))){
					logDataOld <- logDataNew[attempt]
					varAlpha[mrk] <- varCan[attempt]
				}
			}
			if (varAlpha[mrk]){
				invLhs <- 1 / (zTz/varE + 1/varAlpha[mrk])
				alpha[mrk] <- rnorm(1, invLhs * rhs / varE, sqrt(invLhs))
				yAdj <- yAdj - z[,mrk]*alpha[mrk]
			} else alpha[mrk] <- 0
		}

		if (iter %% 100 == 0){
			cat("Iteration ",iter," number of loci in model = ", sum(varAlpha > 0) ,"\n")
		}
		
		# Save results after burnin 
		if (iter > burnIn && (iter %% thin) == 0){
			meanVarE <- meanVarE + varE
			meanBeta <- meanBeta + beta
			meanAlpha <- meanAlpha + alpha
			meanVarAlpha <- meanVarAlpha + varAlpha
			ppAlpha <- ppAlpha + (varAlpha > 0)
		}
		
	}#END MCMC iterations
	
	nSaveIter <- (nIter - burnIn) / thin
	meanVarE <- meanVarE / nSaveIter
	meanBeta <- meanBeta / nSaveIter
	meanAlpha <- meanAlpha / nSaveIter
	meanVarAlpha <- meanVarAlpha / nSaveIter
	ppAlpha <- ppAlpha / nSaveIter
	
	indBLUP <- z %*% meanAlpha
	
	return(list(predVals=indBLUP, fixedEffects=meanBeta, markerEffects=meanAlpha, markerVar=meanVarAlpha, markerInModel=ppAlpha, errorVar=meanVarE))
}#END runBayesBchain

################################################################################################################
# Here's where you actually run the chains
# NOTE: this would be better if for each chain you initialized with random samples from the prior
	if (n.core == 1){
		results <- lapply(parms, runBayesBchain)
	} else{
		library(multicore)
		results <- mclapply(parms, runBayesBchain, mc.cores=n.core)
	}

	# if you want to see each chain separately, don't take the averages
	indBLUP <- numeric(nObs)
	for (chain in 1:nChain){
		indBLUP <- indBLUP + results[[chain]]$predVals / nChain
		meanVarE <- meanVarE + results[[chain]]$errorVar / nChain
		meanBeta <- meanBeta + results[[chain]]$fixedEffects / nChain
		meanAlpha <- meanAlpha + results[[chain]]$markerEffects / nChain
		meanVarAlpha <- meanVarAlpha + results[[chain]]$markerVar / nChain
		ppAlpha <- ppAlpha + results[[chain]]$markerInModel / nChain
	}
	return(list(predVals=indBLUP, fixedEffects=meanBeta, markerEffects=meanAlpha, markerVar=meanVarAlpha, markerInModel=ppAlpha, errorVar=meanVarE))
}#END BayesB
