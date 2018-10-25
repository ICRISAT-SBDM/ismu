
### R Program to generate descriptive statistics for a numneric vector
BasicStats<-function (x)
{
      Nbrna <- sum(as.numeric(is.na(x)))
      x <- x[!is.na(x)]
      Nbrval <- length(x)
      Nbrnull <- sum(as.numeric(x == 0))
      Min <- min(x)
      Max <- max(x)
      Range <- Max - Min
      Sum <- sum(x)
      Median <- median(x)
      Mean <- mean(x)
      Var <- var(x)
      StdDev <- sqrt(Var)
      SEMean <- StdDev/sqrt(Nbrval)
      p <- 0.95
      CIMean <- qt((0.5 + p/2), (Nbrval - 1)) * SEMean
      names(CIMean) <- p
      CoefVar <- StdDev/Mean*100
      Skew <- sum((x - mean(x))^3)/((length(x)-1) * sqrt(var(x))^3)
      Kurt <- sum((x - mean(x))^4)/((length(x)-1) * var(x)^2) -  3
      Skew.SE <- sqrt(6 * Nbrval * (Nbrval - 1)/(Nbrval -  2)/(Nbrval + 1)/(Nbrval + 3))
      Skew.2SE <- Skew/(2 * Skew.SE)
      Kurt.SE <- sqrt(24 * Nbrval * ((Nbrval - 1)^2)/(Nbrval -  3)/(Nbrval - 2)/(Nbrval + 3)/(Nbrval + 5))
      Kurt.2SE <- Kurt/(2 * Kurt.SE)

      Values <- c(Nbrval,Nbrnull,Nbrna,Min,Max,Range,
                Sum,Median,Mean,SEMean,CIMean,Var,StdDev, 
                CoefVar,Skew,Skew.SE,Kurt,Kurt.SE)
      
      Statistic <-c("No.values","No.null", "No.missing","Minimum", "Maximum", "Range",
                   "Sum","Median","Mean", "SE.mean","CI.mean", "Variance", "Std.dev",
                   "Coef.var", "Skewness", "Skew.SE", "Kurtosis", "Kurt.SE")
                   
       
      Res<-data.frame(Statistic, Values)

      ### Normality test using Shapiro Wilk and Kolmogorov Smirnov
      SWtest <- shapiro.test(x)
      KStest<- ks.test(x,"pnorm",mean(x),sd(x))
      Test <- c("Shapiro-Wilk", "Kolmogorov-Smirnov")
      Statistic <- c(SWtest$statistic,KStest$statistic)
      p.value <- c(SWtest$p.value,KStest$p.value)
      NormalityT<-data.frame(Test, Statistic, p.value,row.names=NULL)   
      Result <- list(Basicstatistics=Res, Normality=NormalityT) 
      return(Result)

}  
  


