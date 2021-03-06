# TOOL dea-deseq.R: "Differential expression using DESeq" (Differential expression analysis using the DESeq Bioconductor package. You can create the input count table and phenodata file by the tool Utilities - Define NGS experiment.)
# INPUT data.tsv TYPE GENERIC
# INPUT phenodata.tsv TYPE GENERIC
# OUTPUT OPTIONAL de-list-deseq.tsv
# OUTPUT OPTIONAL de-list-deseq.bed
# OUTPUT OPTIONAL ma-plot-significant-deseq.pdf
# OUTPUT OPTIONAL dispersion-plot-deseq.pdf
# OUTPUT OPTIONAL p-value-plot-deseq.pdf
# PARAMETER column: "Column describing groups" TYPE METACOLUMN_SEL DEFAULT group (Phenodata column describing the groups to test.)
# PARAMETER normalization: "Apply normalization" TYPE [yes, no] DEFAULT yes (Should effective library size be estimated. This corrects for RNA composition bias. Note that if you have supplied library size in phenodata, size factors are calculated based on the library size total, and composition bias is not corrected.)
# PARAMETER replicates: "Disregard replicates" TYPE [yes, no] DEFAULT no (You need to have biological replicates of each experiment condition in order to estimate the biological and experimental variability. If biological replicates are available for only one condition, DESeq will estimate variability using the replicates of that single condition. However, this is only an approximation and reduces the reliability of the results. If there are no replicates at all, the variance is estimated using the samples from the different conditions as replicates. This approximation is even less reliable and affects results accordingly.)
# PARAMETER fitting_method: "Use fitted dispersion values" TYPE [maximum: "when higher than original values", fit-only: "always"] DEFAULT maximum (Should the dispersion of counts for a gene be replaced with the fitted value from the dispersion model always, or only when the fitted value is larger? The latter option is more conservative and minimizes false positives. Replacing always optimises the balance between false positives and false negatives.)
# PARAMETER dispersion_estimate:"Dispersion estimate" TYPE [parametric: "parametric", local: "local"] DEFAULT local (The dispersion can be estimated either using a local fit, which is suitable in most cases - including when there are no biological independent replicate samples - or using a two-coefficient parametric model, which may be preferable under certain circumstances.)
# PARAMETER p.value.adjustment.method: "Multiple testing correction" TYPE [none, bonferroni: "Bonferroni", holm: "Holm", hochberg: "Hochberg", BH: "BH", BY: "BY"] DEFAULT BH (Multiple testing correction method.)
# PARAMETER p.value.cutoff: "P-value cutoff" TYPE DECIMAL FROM 0 TO 1 DEFAULT 0.05 (The cutoff for statistical significance.)
# PARAMETER image_width: "Plot width" TYPE INTEGER FROM 200 TO 3200 DEFAULT 600 (Width of the plotted network image.)
# PARAMETER image_height: "Plot height" TYPE INTEGER FROM 200 TO 3200 DEFAULT 600 (Height of the plotted network image.)


############################################################
#                                                          
# Analysis workflow using DESeq for normalization and     
# statistical testing for finding differentially expressed genes                                           
#                                                          
# MG, 7.2.2012                                             
# EK, 6.5.2012, clarified texts
# EK, 12.5.2013, fixed the fitting method parameter
############################################################

# Loads the libraries
library(DESeq)

# Set parameters for testing
# column <- "group"
# replicates <- "yes"
# normalization <- "yes"
# fitting_method <- "maximum"
# dispersion_estimate <- "parametric"
# p.value.adjustment.method <- "BH"
# p.value.cutoff <- 0.1
# image_height <- 600
# image_width <- 600

# Loads the counts data
file <- c("data.tsv")
dat <- read.table(file, header=T, sep="\t", row.names=1)

# Separates expression values and flags
annotations <- dat[,-grep("chip", names(dat))]
dat2 <- dat[,grep("chip", names(dat))]

# Test needs a parameter "groups" that specifies the grouping of the samples
phenodata <- read.table("phenodata.tsv", header=T, sep="\t")
groups <- as.character (phenodata[,pmatch(column,colnames(phenodata))])
group_levels <- levels(as.factor(groups))
number_samples <- length(groups)

# If the library_size column of phenodata contains data, then use that to estimate size factors
lib_size <- as.numeric(phenodata$library_size)
if (is.na(lib_size[1])) estimate_lib_size <- "TRUE" else estimate_lib_size <- "FALSE"
lib_size <- lib_size/mean(lib_size)


# Sanity checks
# only 2 group comparison is supported
if (length(unique(groups))==1 | length(unique(groups))>=3) {
	stop("CHIPSTER-NOTE: You need to have exactly two groups to run this analysis")
}
# if no biological replicates, force blind mode in dispersion estimation
if (number_samples == 2 && replicates == "no")  {
	stop("CHIPSTER-NOTE: You need to have independent biological replicates for at least one of the experiment conditions in order to reliably estimate dispersion. Alternatively, run the analysis with the disregard replicates parameter set to yes, but note that statistical power may be significantly reduced and the false positive rate may increase.")
}
if (number_samples == 2 && replicates == "yes")  {
	blind_dispersion <- TRUE
} else {
	blind_dispersion <- FALSE
}

# Create a counts data object
counts_data <- newCountDataSet(dat2, groups)

# Calculate scaling factors based on estimated library size, unless it is given in phenodata
# If normalization is turned off, set the size factors to 1 for all samples
if (normalization == "yes") {
	if (estimate_lib_size) {
		counts_data <- estimateSizeFactors(counts_data)
	} else {
		counts_data <- estimateSizeFactors(counts_data)
		sizeFactors(counts_data) <- lib_size
	}
} else {
	sizeFactors(counts_data) <- 1
}

# Estimate dispersion values for each gene and replace with fitted values
# Use sharingMode parameter to control how conservative the replacement will be
# Use fitType to control for parametric or local fit
if (blind_dispersion) {
	counts_data <- estimateDispersions(counts_data, method="blind", sharingMode="fit-only",
			fitType=dispersion_estimate)
} else {
	counts_data <- estimateDispersions(counts_data, method="pooled", sharingMode=fitting_method,
			fitType=dispersion_estimate)
}


# Function that produces a qc plot to check dispersion estimates
plotDispEsts <- function(cds) {
	plot(rowMeans( counts(cds, normalized=TRUE)), fitInfo(cds)$perGeneDispEsts,pch = '.', 
			log="xy", main="Dispersion plot", xlab="normalized counts", ylab="dispersion")
	xg <- 10^seq( -.5, 5, length.out=300)
	lines(xg, fitInfo(cds)$dispFun(xg), col="red")
	legend(x="topright", legend="fitted dispersion", col="red", cex=1, pch="-")
}

# Make dispersion plot
pdf(file="dispersion-plot-deseq.pdf")
plotDispEsts(counts_data)
dev.off()

# Calculate statistic for differential expression
results_table <- nbinomTest(counts_data, group_levels[2], group_levels[1] )

# Merge with original data table
output_table <- cbind (dat, results_table[,-1])

# Adjust p-values
output_table$padj <- p.adjust(output_table$pval, method=p.value.adjustment.method)

# Filter out the significant ones
significant_table <- output_table[ (output_table$padj <  p.value.cutoff),]

# Remove rows with NA adjusted p-values
significant_table <- significant_table[! (is.na(significant_table$padj)),]

# Order results based on raw p-values
significant_table <- significant_table[ order(significant_table$pval), ] 

# Output the table
if (dim(significant_table)[1] > 0) {
	write.table(significant_table, file="de-list-deseq.tsv", sep="\t", row.names=T, col.names=T, quote=F)
}

# Also output a BED file for visualization and region matching tools
if (dim(significant_table)[1] > 0) {
	empty_column <- character(length(significant_table[1]))
	bed_output <- significant_table [,c("chr","start","end")]
	bed_output <- cbind(bed_output,empty_column)
	bed_output <- cbind(bed_output, significant_table[,"log2FoldChange"])
	write.table(bed_output, file="de-list-deseq.bed", sep="\t", row.names=F, col.names=F, quote=F)
}

# Make histogram of p-values with overlaid significance cutoff and uniform distribution
pdf (file="p-value-plot-deseq.pdf")
hist(output_table$pval, breaks=100, col="blue",
		border="slateblue", freq=FALSE,
		main="P-value distribution", xlab="p-value", ylab="proportion (%)")
hist(output_table$padj, breaks=100, col="red",
		border="slateblue", add=TRUE, freq=FALSE)
abline(h=1, lwd=2, lty=2, col="black")
abline(v=p.value.cutoff, lwd=2, lty=2, col="green")
legend (x="topright", legend=c("p-values","adjusted p-values", "uniform distribution", "significance cutoff"), col=c("blue","red","black","green"),
		cex=1, pch=15)
dev.off()

# Define function for making MA-plot of significant findings
plotDE <- function(res)
	plot(res$baseMean, res$log2FoldChange,
			log="x", pch=20, cex=.25, col = ifelse( res$padj < p.value.cutoff, "red", "black"),
			main="MA plot for significantly\ndifferentially expressed genes", xlab="mean counts", ylab="log2(fold change)") 

# Make MA-plot
pdf(file="ma-plot-significant-deseq.pdf")
plotDE(results_table)
legend (x="topleft", legend=c("significant","not significant"), col=c("red","black"),
		cex=1, pch=19)
abline(h = c(-1, 0, 1), col = c("dodgerblue", "darkgreen", "dodgerblue"), lwd = 2)
dev.off()

# EOF
