# TOOL dea-cufflinks.R: "Differential expression using Cufflinks v1.0.3"  (This tool performs differential expression analysis of known genes and isoforms using the older Cufflinks algorithm, v1.0.3. Note that only one filtering criteria should be applied for a given analysis run. When left at default settings, Cufflinks filters out unsuccessfully tested loci, as well as those with a q-value less than 0.05. The tool assumes that all samples belonging to each experiment condition have been merged into one single BAM file.)
# INPUT treatment.bam: "BAM data file for the treatment sample" TYPE GENERIC
# INPUT control.bam: "BAM data file for the control sample" TYPE GENERIC
# OUTPUT cufflinks-log.txt
# OUTPUT de-genes-cufflinks.tsv
# OUTPUT de-isoforms-cufflinks.tsv
# OUTPUT OPTIONAL de-genes-cufflinks.bed
# OUTPUT OPTIONAL de-isoforms-cufflinks.bed
# PARAMETER genome: "Genome" TYPE [hg19: "Human (hg19\)", mm10: "Mouse (mm10\)", mm9: "Mouse (mm9\)", rn4: "Rat (rn4\)"] DEFAULT hg19 (Genome that your reads were aligned against.)
# PARAMETER OPTIONAL p.value.threshold: "P-value cutoff" TYPE DECIMAL FROM 0 TO 1 DEFAULT 1 (The cutoff for statistical significance. Since the p-values are not adjusted to account for multiple testing correction, the cutoff needs to be substantially more conservative than what is usually applied.)
# PARAMETER OPTIONAL q.value.threshold: "Q-value cutoff" TYPE DECIMAL FROM 0 TO 1 DEFAULT 1 (The cutoff for statistical significance. Note that q-values are adjusted to account for multiple testing correction.)                                                
                                                         
# MG, 21.6.2011                                            
# EK, 11.5.2012
# EK, 2.6.2012
# EK, 24.9.2012 updated GTFs, added mm10

# Output that is yet to be supported
# OUTPUT de-cds.tsv
# OUTPUT de-tss.tsv
# OUTPUT de-splicing.tsv
# OUTPUT de-promoters.tsv

# Cufflinks tools setup
source(file.path(chipster.common.path, "bed-utils.R")) # bed sort
cufflinks.binary <- c(file.path(chipster.tools.path, "cufflinks", "cuffdiff"))

# Annotation file setup
if (genome == "hg19") {
	annotation.file <- "Homo_sapiens.GRCh37.68.chr.gtf"
}
if (genome == "mm10") {
	annotation.file <- "Mus_musculus.GRCm38.68.chr.gtf"
}
if (genome == "mm9") {
	annotation.file <- "Mus_musculus.NCBIM37.62.chr.gtf"
}
if (genome == "rn4") {
	annotation.file <- "Rattus_norvegicus.RGSC3.4.68.chr.gtf"
}
annotation.file <- c(file.path(chipster.tools.path, "genomes", "gtf", annotation.file))

# Run differential expression analysis for known genes and transcript isoforms
cufflinks.input.treatment <- "treatment.bam"
cufflinks.input.control <-"control.bam" 
cufflinks.command <- paste(cufflinks.binary, annotation.file, "treatment.bam", "control.bam")
system(cufflinks.command)

# Rename output files for Chipster
system ("mv gene_exp.diff de-genes.tsv")
system ("mv isoform_exp.diff de-isoforms.tsv")
# system ("mv cds_exp.diff de-cds.tsv")
# system ("mv promoters.diff de-promoters.tsv")
# system ("mv splicing.diff de-splicing.tsv")
# system ("mv tss_group_exp.diff de-tss.tsv")

# DE genes
# Extract chromosome locations and add in the first three table columns
dat <- read.table(file="de-genes.tsv", header=T, sep="\t")
regions_list <- as.character(dat$locus)
chr_list <- character(length(regions_list))
start_list <- numeric(length(regions_list))
end_list <- numeric(length(regions_list))
for (count in 1:length(regions_list)) {
	chr_list[count] <- unlist(strsplit (regions_list[count], split=":")) [1]
	start_list[count] <- unlist(strsplit(unlist(strsplit(regions_list[count], split=":"))[2], split="-")) [1]
	end_list[count] <- unlist(strsplit(unlist(strsplit(regions_list[count], split=":"))[2], split="-")) [2]	
}
dat2 <- data.frame(chr=chr_list, start=start_list, end=end_list, dat)

# Rename gene to symbol for compatibility with venn diagram
colnames (dat2) [5] <- "ensembl_id"
colnames (dat2) [6] <- "symbol"
colnames (dat2) [13] <- "ln_FC"

# Filter the gene output based on user defined cutoffs
dat2 <- dat2[dat2$status=="OK",]
results_list <- dat2
if (p.value.threshold < 1 || q.value.threshold < 1) {
	if (p.value.threshold < 1) {
		results_list <- dat2 [dat2$p_value <= p.value.threshold,]
	}
	if (q.value.threshold < 1) {
		results_list <- dat2 [dat2$q_value <= q.value.threshold,]
	}
} else {
	results_list <- results_list[results_list$significant=="yes",]
}
# order according to increasing q-value
results_list <- results_list[order(results_list$q_value, decreasing=FALSE),]
number_genes <- dim (results_list) [1]
row_names <- 1:number_genes
rownames(results_list) <- row_names
write.table(results_list, file="de-genes-cufflinks.tsv", sep="\t", row.names=TRUE, col.names=T, quote=F)

# Also output a BED file for visualization and region matching tools
if (dim(results_list)[1] > 0) {
	bed_output <- results_list[,c("chr","start","end","symbol","ln_FC")]
	# sort according to chromosome location
	bed_output <- sort.bed(bed_output)
	write.table(bed_output, file="de-genes-cufflinks.bed", sep="\t", row.names=F, col.names=F, quote=F)
}

# Report numbers to the log file
if (dim(results_list)[1] > 0) {
	sink(file="cufflinks-log.txt")
	number_genes_tested <- dim(dat)[1]
	number_filtered <- number_genes_tested-dim(results_list)[1]
	number_significant <- dim(results_list)[1]
	cat("GENE TEST SUMMARY\n")
	cat("In total,", number_genes_tested, "genes were tested for differential expression.\n")
	cat("Of these,", number_filtered, "didn't fulfill the technical criteria for testing or the significance cut-off specified.\n")
	cat(number_significant, "genes were found to be statistically significantly differentially expressed.")	
} else {
	cat("GENE TEST SUMMARY\n")
	cat("Out of the", number_genes_tested, "genes tested, there were no statistically significantly differentially expressed ones found.")
}

# DE isoforms
# Extract chromosome locations and add in the first three table columns
dat <- read.table(file="de-isoforms.tsv", header=T, sep="\t")
regions_list <- as.character(dat$locus)
chr_list <- character(length(regions_list))
start_list <- numeric(length(regions_list))
end_list <- numeric(length(regions_list))
for (count in 1:length(regions_list)) {
	chr_list[count] <- unlist(strsplit (regions_list[count], split=":")) [1]
	start_list[count] <- unlist(strsplit(unlist(strsplit(regions_list[count], split=":"))[2], split="-")) [1]
	end_list[count] <- unlist(strsplit(unlist(strsplit(regions_list[count], split=":"))[2], split="-")) [2]	
}
dat2 <- data.frame(chr=chr_list, start=start_list, end=end_list, dat)

# Rename gene to symbol for compability with venn diagram
colnames (dat2) [5] <- "ensembl_id"
colnames (dat2) [6] <- "symbol"
colnames (dat2) [13] <- "ln_FC"

# Filter the isoforms output based on user defined cutoffs
dat2 <- dat2[dat2$status=="OK",]
results_list <- dat2
if (p.value.threshold < 1 || q.value.threshold < 1) {
	if (p.value.threshold < 1) {
		results_list <- dat2 [dat2$p_value <= p.value.threshold,]
	}
	if (q.value.threshold < 1) {
		results_list <- dat2 [dat2$q_value <= q.value.threshold,]
	}
} else {
	results_list <- results_list[results_list$significant=="yes",]
}
# order according to increasing q-value
results_list <- results_list[order(results_list$q_value, decreasing=FALSE),]
number_genes <- dim (results_list) [1]
row_names <- 1:number_genes
rownames(results_list) <- row_names
write.table(results_list, file="de-isoforms-cufflinks.tsv", sep="\t", row.names=TRUE, col.names=T, quote=F)

# Also output a BED file for visualization and region matching tools
if (dim(results_list)[1] > 0) {
	bed_output <- results_list[,c("chr","start","end","symbol","ln_FC")]
	# sort according to chromosome location
	bed_output <- sort.bed(bed_output)
	write.table(bed_output, file="de-isoforms-cufflinks.bed", sep="\t", row.names=F, col.names=F, quote=F)
}

# Report numbers to the log file
if (dim(results_list)[1] > 0) {
	number_genes_tested <- dim(dat)[1]
	number_filtered <- number_genes_tested-dim(results_list)[1]
	number_significant <- dim(results_list)[1]
	cat("\n\nTRANSCRIPT ISOFORMS TEST SUMMARY\n")
	cat("In total,", number_genes_tested, "transcript isoforms were tested for differential expression.\n")
	cat("Of these,", number_filtered, "didn't fulfill the technical criteria for testing or the significance cut-off specified.\n")
	cat(number_significant, "transcripts were found to be statistically significantly differentially expressed.")	
} else {
	cat("\n\nTRANSCRIPT ISOFORMS TEST SUMMARY\n")
	cat("Out of the", number_genes_tested, "transcripts tested, there were no statistically significantly differentially expressed ones found.")
}
sink()

# EOF

