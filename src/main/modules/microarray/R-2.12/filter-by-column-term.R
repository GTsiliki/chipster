# TOOL filter-by-column-term.R: "Filter using a column term" (Allows the user to filter the genes on the basis of one column with textual terms, such as gene symbol, gene description or GO term.)
# INPUT normalized.tsv: normalized.tsv TYPE GENERIC 
# OUTPUT column-term-filter.tsv: column-term-filter.tsv 
# PARAMETER column: column TYPE COLUMN_SEL (Data column to filter by)
# PARAMETER match.term: match.term TYPE STRING DEFAULT empty (String to search for)
# PARAMETER exact.match: exact.match TYPE [yes: yes, no: no] DEFAULT yes (Should only exact matches be retained?)

# Tool that allows the data to be filtered according to text terms in a column
# MG 9.3.2010
# IS 1.11.2010, small fix to column matching
# JTT 30.3.2010, added a parameter to control matching

# Loads the normalized data
dat <- read.table('normalized.tsv', header=TRUE, sep='\t', quote='', check.names=FALSE)

# Only exact matches?
if(exact.match=="yes") {
	match.term<-paste("^", match.term, "$", sep="")
}

# grep the desired rows
dat2 <- dat[grep(match.term, dat[,column]),]

# write output
write.table(dat2, 'column-term-filter.tsv', sep='\t', quote=FALSE)
# EOF

