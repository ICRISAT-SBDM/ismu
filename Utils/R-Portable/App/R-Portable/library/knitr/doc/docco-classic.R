
## ----setup, echo=FALSE, results='asis'-----------------------------------
x = readLines('docco-linear.Rmarkdown')[-(1:7)]
x = gsub('linear', 'classic', x)
x = gsub('Rmarkdown', 'Rmkd', x)
i = grep('^knit2html[(][.]{3}', x)
x[i - 1] = '```{r}'
x[i] = 'head(knitr::rocco, 5)'
library(knitr)
cat(knit_child(text = x, quiet = TRUE), sep = '\n')


