# TOOL MEDIPS_two_samples_V1.R: "MEDIPS - methylation analysis, treatment vs. control" (Analysis of MeDIP-seq data for two conditions. This tool is based on the MEDIPS package developed by Chavez et al.)
# INPUT control.tsv: "Converted BAM data file for the control" TYPE GENERIC
# INPUT treatment.tsv: "Converted BAM data file for the treatment" TYPE GENERIC
# OUTPUT OPTIONAL saturationplot.png: "Saturation plot"
# OUTPUT OPTIONAL coverageplot.png: "Coverage plot"
# OUTPUT OPTIONAL calibrationplot.png: "Calibration plot"
# OUTPUT OPTIONAL CpGdensities.png: "CpG density plot"
# OUTPUT OPTIONAL RPMsignal.png: "RPM plot"
# OUTPUT OPTIONAL AMSsignal.png: "AMS plot"
# OUTPUT OPTIONAL rois.tsv: "Promoter regions"
# OUTPUT OPTIONAL output.bed: "BED file"
# OUTPUT output.tsv: "Enrichment data"
# PARAMETER species TYPE [human] DEFAULT human (Select the species)
# PARAMETER promoters.only TYPE [yes, no] DEFAULT no (Should the analyses be restricted to promoter regions only)
# PARAMETER coverage.resolution TYPE [25,50,100,200] DEFAULT  50 (Targeted data resolution, in base pairs, when the genome-wide coverage is calculated)
# PARAMETER smoothing.extension TYPE [200,400,800,1200] DEFAULT 400 (The amount of data smoothing, in base pairs, by extending the reads)
# PARAMETER fragment.length TYPE [400,800,1600,2400] DEFAULT 800 (Length of the fragment for calculation of local CpGs)
# PARAMETER save.wiggle TYPE [yes, no] DEFAULT yes (Should the normalized data, as rpm, be saved as a wiggle file)
# PARAMETER promoter.upstream TYPE [1000,2000,5000] DEFAULT 1000 (How much upstream, in base pairs, from the transcription start site does the promoter extend)
# PARAMETER promoter.downstream TYPE [250,500,750,1000] DEFAULT 500 (How much downstream, in base pairs, from the transcription start site does the promoter extend)
# PARAMETER image.width TYPE INTEGER FROM 200 TO 3200 DEFAULT 600 (Width of the plotted network image)
# PARAMETER image.height TYPE INTEGER FROM 200 TO 3200 DEFAULT 600 (Height of the plotted network image)


# Parameters for testing purposes only
#species<-"human"
#promoters.only<-"no"
#coverage.resolution<-"100"
#smoothing.extension<-"1200"
#fragment.length<-2400
#save.wiggle<-"yes"
#promoter.upstream<-"1000"
#promoter.downstream<-"500"
#image.width<-600
#image.height<-600

# Processing of the parameters
if(species=="human") {
   genome<-c("BSgenome.Hsapiens.UCSC.hg19")
   pgenome<-"hg19"  
   library(BSgenome.Hsapiens.UCSC.hg19)
}
w<-image.width
h<-image.height




library(MEDIPS)

# Assign the files to control and treatment
cfile<-c("control.tsv")
tfile<-c("treatment.tsv")

# Reads the data
control<-MEDIPS.readAlignedSequences(BSgenome=genome, file=cfile)
treatment<-MEDIPS.readAlignedSequences(BSgenome=genome, file=tfile)

# Creating the genome vector
control<-MEDIPS.genomeVector(data=control, bin_size=as.numeric(coverage.resolution), extend=as.numeric(smoothing.extension))
treatment<-MEDIPS.genomeVector(data=treatment, bin_size=as.numeric(coverage.resolution), extend=as.numeric(smoothing.extension))

# Pattern Positions
control<-MEDIPS.getPositions(data=control, pattern="CG")
treatment<-MEDIPS.getPositions(data=treatment, pattern="CG")

# Coupling vector
control<-MEDIPS.couplingVector(data=control, fragmentLength=as.numeric(fragment.length), func="count")
treatment<-MEDIPS.couplingVector(data=treatment, fragmentLength=as.numeric(fragment.length), func="count")

# Calibration curve
control<-MEDIPS.calibrationCurve(data=control)
treatment<-MEDIPS.calibrationCurve(data=treatment)

# Normalization
control<-MEDIPS.normalize(data=control)
treatment<-MEDIPS.normalize(data=treatment)

# Comparing experiments
if(promoters.only=="yes") {
   library(rtracklayer)
   session <- browserSession()
   genome(session) <- pgenome
   query <- ucscTableQuery(session, "refGene")
   refGene<-getTable(query)
   sta<-refGene$txStart
   sta[refGene$strand=="+"]<-sta[refGene$strand=="+"]-1000
   sta[refGene$strand=="-"]<-sta[refGene$strand=="-"]-500
   sto<-refGene$txStart
   sto[refGene$strand=="+"]<-sto[refGene$strand=="+"]+500
   sto[refGene$strand=="-"]<-sto[refGene$strand=="-"]+1000
   rois<-data.frame(refGene$chrom, sta, sto, make.unique(as.character(refGene$name)))
   rois<-rois[rois[,1] %in% unique(c(control@genome_chr), treatment@genome_chr),]
   write.table(rois, "rois.tsv", sep="\t", quote=F, col.names=F, row.names=F)
   diff.meth<-MEDIPS.methylProfiling(data1=control, data2=treatment, select=2, frame_size=400, ROI_file="rois.tsv")
   write.table(diff.meth, "output.tsv", sep="\t", quote=F, col.names=T, row.names=F)
}

if(promoters.only=="no") {
   diff.meth<-MEDIPS.methylProfiling(data1=control, data2=treatment, select=2, frame_size=400)
   write.table(diff.meth, file="output.tsv", sep="\t", quote=F, col.names=T, row.names=F)
}
