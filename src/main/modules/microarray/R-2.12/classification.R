# TOOL classification.R: Classification (Performs a classification analysis using the selected data set.)
# INPUT normalized.tsv: normalized.tsv TYPE GENE_EXPRS 
# INPUT META phenodata.tsv: phenodata.tsv TYPE GENERIC 
# OUTPUT classification.txt: classification.txt 
# PARAMETER method: method TYPE [knn: knn, lda: lda, dlda: dlda, slda: slda, qda: qda, rpart: rpart, svm: svm, lvq: lvq, naiveBayes: naiveBayes, nnet: nnet, bagging: bagging] DEFAULT knn (Analysis method)
# PARAMETER standardize: standardize TYPE [yes: yes, no: no] DEFAULT yes (Standardize genes before analysis)
# PARAMETER validation.type: validation.type TYPE [crossvalidate: crossvalidate, predict: predict] DEFAULT crossvalidate (Use crossvalidation)
# PARAMETER crossvalidation.type: crossvalidation.type TYPE [LOO: LOO] DEFAULT LOO (How to crossvalidate)
# PARAMETER feature.selection.in.crossvalidation: feature.selection.in.crossvalidation TYPE [yes: yes, no: no] DEFAULT no (Include a feature selection step in crossvalidation)
# PARAMETER feature.selection.threshold: feature.selection.threshold TYPE DECIMAL FROM 0 TO 1 DEFAULT 0.75 (What percentage of the t-test values to discard)
# PARAMETER group.column: group.column TYPE METACOLUMN_SEL DEFAULT group (Phenodata column describing the groups to test)
# PARAMETER training.column: training.column TYPE METACOLUMN_SEL DEFAULT EMPTY (Phenodata column describing the samples in the training groups)


# JTT 22.1.2009
# Prediction has not yet been implemented!

# Parameter settings (default) for testing purposes
#method<-"knn"
#standardize<-"yes"
#validation.type<-"crossvalidate"
#crossvalidation.type<-"LOO"
#feature.selection.in.crossvalidation<-"no"
#feature.selection.threshold<-0.75
#group.column<-"group"
#training.column<-"EMPTY"

# Loads the libraries
library(MLInterfaces)
library(genefilter)

# Transforming variable names
feature.sel.in.crossval<-feature.selection.in.crossvalidation

# Loads the data
file<-c("normalized.tsv")
dat<-read.table(file, sep="\t", header=T, row.names=1)

# Reads the phenodata table
phenodata<-read.table("phenodata.tsv", header=T, sep="\t")

# Separates expression values and flags
calls<-dat[,grep("flag", names(dat))]
dat2<-dat[,grep("chip", names(dat))]

date()
# Standardization
if(standardize=="yes") {
   dat2<-genescale(dat2, axis=1, method=c("Z"))
}
date()

# Creating a suitable dataset
dat3<-data.frame(group=phenodata[,grep(group.column, colnames(phenodata))], t(dat2))

# Assessory function from MLInterfaces
fsFun.rowtQ3 = function(formula, data) {
 mf = model.frame(formula, data)
 mm = model.matrix(formula, data)
 respind = attr( terms(formula, data=data), "response" )
 x = mm
 if ("(Intercept)" %in% colnames(x)) x = x[,-which(colnames(x) == "(Intercept)")]
 y = mf[, respind]
 respname = names(mf)[respind]
 nuy = length(unique(y))
 if (nuy > 2) warning("number of unique values of response exceeds 2")
 ans = abs( rowttests(t(x), factor(y), tstatOnly=TRUE)[[1]] )
 names(ans) = colnames(x)
 ans = names( ans[ which(ans > quantile(ans, feature.selection.threshold) ) ] )
 btick = function(x) paste("`", x, "`", sep="")  # support for nonsyntactic varnames
 as.formula( paste(respname, paste(btick(ans), collapse="+"), sep="~"))
}

# Performing the analysis
if(method=="knn") {
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="no") {
      fit<-MLearn(as.factor(group)~., data=dat3, knnI(k=5, l=1), xvalSpec("LOO"))
   }
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="yes") {
      fit<-MLearn(as.factor(group)~., data=dat3, knnI(k=5, l=1), xvalSpec("LOO", fsFun=fsFun.rowtQ3))
   }
}

if(method=="lda") {
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="no") {
      fit<-MLearn(as.factor(group)~., data=dat3, ldaI, xvalSpec("LOO"))
   }
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="yes") {
      fit<-MLearn(as.factor(group)~., data=dat3, ldaI, xvalSpec("LOO", fsFun=fsFun.rowtQ3))
   }
}

if(method=="dlda") {
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="no") {
      fit<-MLearn(as.factor(group)~., data=dat3, dldaI, xvalSpec("LOO"))
   }
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="yes") {
      fit<-MLearn(as.factor(group)~., data=dat3, dldaI, xvalSpec("LOO", fsFun=fsFun.rowtQ3))
   }
}

if(method=="slda") {
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="no") {
      fit<-MLearn(as.factor(group)~., data=dat3, sldaI, xvalSpec("LOO"))
   }
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="yes") {
      fit<-MLearn(as.factor(group)~., data=dat3, sldaI, xvalSpec("LOO", fsFun=fsFun.rowtQ3))
   }
}

if(method=="qda") {
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="no") {
      fit<-MLearn(as.factor(group)~., data=dat3, qdaI, xvalSpec("LOO"))
   }
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="yes") {
      fit<-MLearn(as.factor(group)~., data=dat3, qdaI, xvalSpec("LOO", fsFun=fsFun.rowtQ3))
   }
}

if(method=="rpart") {
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="no") {
      fit<-MLearn(as.factor(group)~., data=dat3, rpartI, xvalSpec("LOO"))
   }
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="yes") {
      fit<-MLearn(as.factor(group)~., data=dat3, rpartI, xvalSpec("LOO", fsFun=fsFun.rowtQ3))
   }
}

if(method=="svm") {
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="no") {
      fit<-MLearn(as.factor(group)~., data=dat3, svmI, xvalSpec("LOO"))
   }
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="yes") {
      fit<-MLearn(as.factor(group)~., data=dat3, svmI, xvalSpec("LOO", fsFun=fsFun.rowtQ3))
   }
}

if(method=="lvq") {
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="no") {
      fit<-MLearn(as.factor(group)~., data=dat3, lvqI, xvalSpec("LOO"))
   }
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="yes") {
      fit<-MLearn(as.factor(group)~., data=dat3, lvqI, xvalSpec("LOO", fsFun=fsFun.rowtQ3))
   }
}

if(method=="naiveBayes") {
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="no") {
      fit<-MLearn(as.factor(group)~., data=dat3, naiveBayesI, xvalSpec("LOO"))
   }
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="yes") {
      fit<-MLearn(as.factor(group)~., data=dat3, naiveBayesI, xvalSpec("LOO", fsFun=fsFun.rowtQ3))
   }
}

if(method=="nnet") {
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="no") {
      fit<-MLearn(as.factor(group)~., data=dat3, nnetI, xvalSpec("LOO"))
   }
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="yes") {
      fit<-MLearn(as.factor(group)~., data=dat3, nnetI, xvalSpec("LOO", fsFun=fsFun.rowtQ3))
   }
}

if(method=="bagging") {
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="no") {
      fit<-MLearn(as.factor(group)~., data=dat3, baggingI, xvalSpec("LOO"))
   }
   if(validation.type=="crossvalidate" & feature.sel.in.crossval=="yes") {
      fit<-MLearn(as.factor(group)~., data=dat3, baggingI, xvalSpec("LOO", fsFun=fsFun.rowtQ3))
   }
}

# Writing output
sink("classification.txt")
confuMat(fit)
sink()
