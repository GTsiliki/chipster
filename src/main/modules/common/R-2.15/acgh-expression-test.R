# TOOL acgh-expression-test.R: "Test for copy-number-induced expression changes" (Nonparametric testing for changes in expression induced by a change in DNA copy number. Before running this tool, the copy number and expression data must be first matched together using the Match copy number and expression features tool.)
# INPUT matched-cn-and-expression.tsv: matched-cn-and-expression.tsv TYPE GENE_EXPRS 
# INPUT META phenodata.tsv: phenodata.tsv TYPE GENERIC 
# OUTPUT cn-induced-expression.tsv: cn-induced-expression.tsv 
# PARAMETER test.statistic: test.statistic TYPE [wcvm: wcvm, wmw: wmw] DEFAULT wcvm (The test statistic to use.)
# PARAMETER analysis.type: analysis.type TYPE [univariate: univariate, regional: regional] DEFAULT univariate (The type of the analysis.)
# PARAMETER number.of.permutations: number.of.permutations TYPE INTEGER DEFAULT 10000 (The number of permutations used for the p-value calculation.)

# Ilari Scheinin <firstname.lastname@gmail.com>
# 2013-03-20

source(file.path(chipster.common.path, 'CGHcallPlus.R'))
library(intCNGEan)

# read the input files
file <- 'matched-cn-and-expression.tsv'
dat <- read.table(file, header=TRUE, sep='\t', quote='', row.names=1, as.is=TRUE, check.names=FALSE)
phenodata <- read.table('phenodata.tsv', header=TRUE, sep='\t', as.is=TRUE)

# check if the matched data was produced witha n old version
if (length(grep("^exprs\\.", names(dat)))!=0)
  stop('CHIPSTER-NOTE: The input file matched-cn-and-expression.tsv has been produced with an old version of the Match copy number and expression probes script. Please re-run that script first, and use the output from the new version.')

# check that the input file seems to be coming from the script used to match the two data sets
pos <- c('chromosome','cn.start','cn.end','exp.start','exp.end')
if (length(setdiff(pos, colnames(dat)))!=0)
  stop('CHIPSTER-NOTE: This tool can only be run on the output file from the tool Match copy number and expression probes (matched-cn-and-expression.tsv).')

# build the necessary object
dat$chromosome[dat$chromosome=='X'] <- 23
dat$chromosome[dat$chromosome=='Y'] <- 24
dat$chromosome[dat$chromosome=='MT'] <- 25
dat$chromosome <- as.integer(dat$chromosome)

exprs <- as.matrix(dat[,grep("^chip\\.", names(dat))])
rownames(exprs) <- dat$exp.probe
arrays <- phenodata$description

calls <- as.matrix(dat[,grep("^flag\\.", names(dat))])
copynumber <- as.matrix(dat[,grep("^logratio\\.", names(dat))])
segmented <- as.matrix(dat[,grep("^segmented\\.", names(dat))])
probloss <- as.matrix(dat[,grep("^probloss\\.", names(dat))])
probnorm <- as.matrix(dat[,grep("^probnorm\\.", names(dat))])
probgain <- as.matrix(dat[,grep("^probgain\\.", names(dat))])

cgh <- new('cghCall', assayData=assayDataNew(calls=calls, copynumber=copynumber, segmented=segmented, probloss=probloss, probnorm=probnorm, probgain=probgain), featureData=new('AnnotatedDataFrame', data=data.frame(Chromosome=dat$chromosome, Start=dat$cn.start, End=dat$cn.end, row.names=row.names(dat))))

exp <- new("ExpressionSet", exprs=exprs, featureData=new("AnnotatedDataFrame", data=data.frame(Chromosome=dat$chromosome, Start=dat$exp.start, End=dat$exp.end, row.names=dat$exp.probe)))

matched <- list(CNdata.matched=cgh, GEdata.matched=exp)

# tune and test
tuned <- intCNGEan.tune(matched$CNdata.matched, matched$GEdata.matched, test.statistic=test.statistic)
result <- intCNGEan.test(tuned, analysis.type=analysis.type, test.statistic=test.statistic, nperm=number.of.permutations)

# for some reason, columns in the data.frame appear as lists in R-2.12.
# convert back to vectors
if (class(result[,1]) == 'list')
  for (i in 1:ncol(result))
    result[,i] <- unlist(result[,i])

if (nrow(tuned$ann) != nrow(result))
  stop('CHIPSTER-NOTE: The number of rows for the objects tuned and result do not match. Please report this to Ilari Scheinin.')

# format and write result table
rownames(result) <- rownames(tuned$ann)
colnames(result)[1:3] <- tolower(colnames(result)[1:3])

if ('symbol' %in% colnames(dat))
  result$symbol <- dat[rownames(tuned$datafortest), 'symbol']
if ('description' %in% colnames(dat))
  result$description <- dat[rownames(tuned$datafortest), 'description']

result$probes <- rownames(tuned$datafortest)
arrays <- colnames(tuned$datafortest)[(2*tuned$nosamp+1):(3*tuned$nosamp)]
colnames(tuned$datafortest) <- c(sub('^chip\\.', 'prob1.', arrays), sub('^chip\\.', 'prob2.', arrays), arrays)
result <- cbind(result, tuned$datafortest)
result <- result[order(result$adj.p),]

result$chromosome <- as.character(result$chromosome)
result$chromosome[result$chromosome=='23'] <- 'X'
result$chromosome[result$chromosome=='24'] <- 'Y'
result$chromosome[result$chromosome=='25'] <- 'MT'

options(scipen=10)
write.table(result, file='cn-induced-expression.tsv', quote=FALSE, sep='\t', col.names=TRUE, row.names=TRUE)

# EOF
