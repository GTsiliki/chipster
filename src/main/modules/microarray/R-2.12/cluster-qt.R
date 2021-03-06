# TOOL cluster-qt.R: "Quality Threshold (QT\)" (Quality threshold clustering for genes. Divides the genes of the selected data set in a number of clusters. The number and tightness of the clusters is controlled by the option radius. A suitable radius is dependent on the variability of the data set, and it may vary from one data set to another.)
# INPUT normalized.tsv: normalized.tsv TYPE GENE_EXPRS 
# OUTPUT qt.tsv: qt.tsv 
# OUTPUT qt.pdf: qt.pdf 
# PARAMETER radius.for.similarity: radius.for.similarity TYPE DECIMAL FROM 0 TO 1000 DEFAULT 20 (A radius for similar genes)
# PARAMETER image.width: image.width TYPE INTEGER FROM 200 TO 3200 DEFAULT 600 (Width of the resampling image)
# PARAMETER image.height: image.height TYPE INTEGER FROM 200 TO 3200 DEFAULT 600 (Height of the resampling image)


# K-means clustering
# JTT 22.6.2006

# Parameter settings (default) for testing purposes
#radius.for.similarity<-20
#image.width<-600
#image.height<-600

# Renaming variables
rad<-radius.for.similarity
w<-image.width
h<-image.height

# Load the libraries
library(flexclust)

# Loads the data file
file<-c("normalized.tsv")
dat<-read.table(file, header=T, sep="\t", row.names=1)

# Separates expression values and flags
calls<-dat[,grep("flag", names(dat))]
dat2<-dat[,grep("chip", names(dat))]
dat3<-dat2

# Calculates the K-means clustering result
# Needs a parameter for groups radius
# rad<-c(20)
qtc<-qtclust(dat3, radius=rad)

# Plotting the clustering
max.dat2<-max(dat3)
min.dat2<-min(dat3)
k<-length(unique(qtc@cluster))
pdf(file="qt.pdf", width=w/72, height=h/72)
par(mfrow=c(ceiling(sqrt(k)), ceiling(sqrt(k))))
for(i in 1:k) {
   matplot(t(dat3[qtc@cluster==i,]), type="l", main=paste("cluster:", i), ylab="log expression", col=1, lty=1, ylim=c(min.dat2, max.dat2))
}
dev.off()

# Writing a table
# Creates a table with one column giving the cluster membership
dat4<-data.frame(dat3,cluster=qtc@cluster)
dat4<-na.omit(dat4)
write.table(dat4, "qt.tsv", sep="\t", row.names=T, col.names=T, quote=F)
