# TOOL tophat2-single-end.R: "TopHat2 for single end reads" (Aligns Illumina RNA-Seq reads to a genome in order to identify exon-exon splice junctions. TopHat2 first identifies potential exons by mapping the reads to the genome. Using this initial mapping, TopHat2 builds a database of possible splice junctions, and then maps the reads against these junctions to confirm them. The alignment results are given in a BAM file, which is automatically indexed and hence ready to be viewed in Chipster genome browser.)
# INPUT reads1.fq: "Reads to align" TYPE GENERIC
# INPUT OPTIONAL genes.gtf: "Optional GTF file" TYPE GENERIC
# OUTPUT tophat.bam
# OUTPUT tophat.bam.bai
# OUTPUT junctions.bed
# OUTPUT insertions.bed
# OUTPUT deletions.bed
# PARAMETER genome: "Genome" TYPE [hg19: "Human (hg19\)", mm10: "Mouse (mm10\)", mm9: "Mouse (mm9\)", rn4: "Rat (rn4\)", Phytophthora_infestans1_1.12: "Phytophthora infestans 1.1.12", saprolegnia_parasitica_cbs_223.65_2_contigs: "Saprolegnia parasitica cbs.223.65.2 contigs", Populus_trichocarpa.JGI2.0.12: "Populus trichocarpa JGI2.0.12", athaliana.TAIR10: "A. thaliana genome (TAIR10\)", Gasterosteus_aculeatus.BROADS1.67: "Gasterosteus aculeatus (BROADS1.67\)", Halorubrum_lacusprofundi_ATCC_49239: "Halorubrum lacusprofundi (ATCC 49239\)" ] DEFAULT hg19 (Genome that you would like to align your reads against.)
# PARAMETER OPTIONAL min.anchor.length: "Minimum anchor length" TYPE INTEGER FROM 3 TO 1000 DEFAULT 8 (TopHat will report junctions spanned by reads with at least this many bases on each side of the junction. Note that individual spliced alignments may span a junction with fewer than this many bases on one side. However, every junction involved in spliced alignments is supported by at least one read with this many bases on each side.)
# PARAMETER OPTIONAL splice.mismatches: "Maximum number of mismatches allowed in the anchor" TYPE INTEGER FROM 0 TO 2 DEFAULT 0 (The maximum number of mismatches that may appear in the anchor region of a spliced alignment.)
# PARAMETER OPTIONAL min.intron.length: "Minimum intron length" TYPE INTEGER FROM 10 TO 1000 DEFAULT 70 (TopHat will ignore donor-acceptor pairs closer than this many bases apart.)
# PARAMETER OPTIONAL max.intron.length: "Maximum intron length" TYPE INTEGER FROM 1000 TO 1000000 DEFAULT 500000 (TopHat will ignore donor-acceptor pairs farther than this many bases apart, except when such a pair is supported by a split segment alignment of a long read.)
# PARAMETER OPTIONAL max.multihits: "How many hits is a read allowed to have" TYPE INTEGER FROM 1 TO 1000000 DEFAULT 20 (Instructs TopHat to allow up to this many alignments to the reference for a given read, and suppresses all alignments for reads with more than this many alignments.)
# PARAMETER OPTIONAL no.novel.juncs: "When GTF file is supplied, ignore novel junctions" TYPE [yes, no] DEFAULT yes (If you supply an optional GTF file, TopHat will use the exon records in this file to build a set of known splice site junctions for each gene, and it will attempt to align reads to these junctions even if they would not normally be covered by the initial mapping. This parameter controls if TopHat should look for reads accross the known junctions only.)

# EK 17.4.2012 added -G and -g options
# MG 24.4.2012 added ability to use gtf files from Chipster server
# AMS 19.6.2012 Added unzipping

# check out if the file is compressed and if so unzip it
source(file.path(chipster.common.path, "zip-utils.R"))
unzipIfGZipFile("reads1.fq")

options(scipen = 10)
# max.intron.length <- formatC(max.intron.length, "f", digits = 0)

# setting up TopHat
tophat.binary <- c(file.path(chipster.tools.path, "tophat2", "tophat2"))
path.bowtie <- c(file.path(chipster.tools.path, "bowtie2"))
path.samtools <- c(file.path(chipster.tools.path, "samtools"))
set.path <-paste(sep="", "PATH=", path.bowtie, ":", path.samtools, ":$PATH")
path.bowtie.index <- c(file.path(path.bowtie, "indexes", genome))


# command start
command.start <- paste("bash -c '", set.path, tophat.binary)

# parameters
command.parameters <- paste("-a", min.anchor.length, "-m", splice.mismatches, "-i", min.intron.length, "-I", max.intron.length, "-g", max.multihits, "--library-type fr-unstranded")

# optional GTF command
command.gtf <- ""
input_files <- dir()
is_gtf <- (length(grep("genes.gtf", input_files))>0)
if (is_gtf) {
	if (no.novel.juncs == "yes") {
	command.gtf <- paste("-G", "genes.gtf", "--no-novel-juncs")
	} else {
		command.gtf <- paste("-G", "genes.gtf")
	}
}

# optional GTF command, if a GTF file has NOT been provided by user
# BUT is avaliable from Chipster server
genome_available <- FALSE
if (genome == "hg19" ||	genome == "mm9" || genome == "mm10" || genome == "rn4") genome_available <- TRUE
if (!is_gtf && genome_available) {
   # annotation file setup
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
	  annotation.file <- "Rattus_norvegicus.RGSC3.4.62.chr.gtf"
   }
   annotation.file <- c(file.path(chipster.tools.path, "genomes", "gtf", annotation.file))
	
	if (no.novel.juncs == "yes") {
		command.gtf <- paste("-G", annotation.file, "--no-novel-juncs")
	} else {
		command.gtf <- paste("-G", annotation.file)
	}
}

# command ending
command.end <- paste(path.bowtie.index, "reads1.fq'")

# run tophat
command <- paste(command.start, command.parameters, command.gtf, command.end)
system(command)

# samtools binary
samtools.binary <- c(file.path(chipster.tools.path, "samtools", "samtools"))

# sort bam
system(paste(samtools.binary, "sort tophat_out/accepted_hits.bam tophat"))

# index bam
system(paste(samtools.binary, "index tophat.bam"))

system("mv tophat_out/junctions.bed junctions.bed")
system("mv tophat_out/insertions.bed insertions.bed")
system("mv tophat_out/deletions.bed deletions.bed")
