# TOOL qc-agilent.R: "Agilent 2-color" (Agilent quality control using boxplots, density plots, and MA plots. This tool should be run on normalized data.)
# INPUT normalized.tsv: normalized.tsv TYPE GENE_EXPRS 
# OUTPUT boxplot.pdf: boxplot.pdf 
# OUTPUT densityplot.pdf: densityplot.pdf 
# OUTPUT OPTIONAL MA-plot.pdf: MA-plot.pdf 
# PARAMETER image.width: image.width TYPE INTEGER FROM 200 TO 3200 DEFAULT 600 (Width of the plotted network image)
# PARAMETER image.height: image.height TYPE INTEGER FROM 200 TO 3200 DEFAULT 600 (Height of the plotted network image)


# Quality control of Agilent chips
# 15.1.2008 JTT
# modified, IS, 12.10.2012, to cope with tables with gene descriptions (that typically contain 's)

# Loads the libraries
library(limma)

# Renaming variables
w<-image.width
h<-image.height

# Reads in the data
file <- 'normalized.tsv'
dat <- read.table(file, header=TRUE, sep='\t', quote='', row.names=1, check.names=FALSE)

# Separates expression values and flags
calls<-dat[,grep("flag", names(dat))]
dat2<-dat[,grep("chip", names(dat))]
A<-dat[,grep("average", names(dat))]

# Producing some basic plots of the data

# Boxplot
pdf(file="boxplot.pdf", width=w/72, height=h/72)
boxplot(as.data.frame(dat2), las=2, cex.axis=0.5)
dev.off()

# Density plot
pdf(file="densityplot.pdf", width=w/72, height=h/72)
x<-c()
y<-c()
for(i in 1:ncol(dat2)) {
   x<-cbind(x, density(dat2[,i])$x)
   y<-cbind(y, density(dat2[,i])$y)
}
plot(x[,1], y[,1], col=1, ylim=c(0, max(y)), main="M intensities", type="l", lwd=2, lty=1, xlab = "M", ylab = "Density")
for(i in 2:ncol(dat2)) {
   lines(x[,i], y[,i], col=i, lwd=2, lty=1)
}
legend(legend=colnames(dat2), x="topright", lty=1, cex=0.75, col=c(1:ncol(dat2)))
dev.off()

# MA plot
if (ncol(A) > 0) {
  pdf(file="MA-plot.pdf", width=w/72, height=h/72)
  par(mfrow=c(ceiling(sqrt(ncol(dat2))), ceiling(sqrt(ncol(dat2)))))
  ylim<-c(min(na.omit(dat2)), max(na.omit(dat2)))
  for(i in 1:ncol(dat2)) {
    par(mar=c(0, 1, 1, 0)+0.1)
    plot(A[,i], dat2[,i], xaxt="n", xlab=NULL, ylab=NULL, main=paste("Chip", i, sep=" "), cex.main=0.75, cex.axis=0.7, cex.lab=0.75, tck=-0.01, mgp=c(3,0.2,0), ylim=ylim)
    d<-na.omit(data.frame(A=A[,i], dat=dat2[,i]))
    lines(lowess(d$A, d$dat), col="red")
  }
  dev.off()
}

# EOF
