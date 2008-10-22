NAMI-projektin R-skriptien versio 0.5.8
Jarno Tuimala, 3.10.2008

Sis�llys:
- Teht�v��
- Versioon kuuluvat skriptit
- Nykyiset ominaisuudet
- Muutokset edellisest� versiosta
- R:n asennus Coronalle (tarvittavat paketit)

---------------------------------------------------------------------------------------------------------------------------

Teht�v��:
- Bugeja:
   - yeast2-siruilla ei toimi hypergeometrinen testi, koska ENTREZID:t� ei l�ydy annotaatiokirjastoista.
      - use systematic names
   - v�rit� PCA/NMDS-kuvat group-sarakkeen perusteella
   - volcanoplot skriptin pit�isi palauttaa my�s data
   - Tee annotaatiokirjastot:
      - Illumina mouse V2
      - Illumina human V3
      - Agilentin miRNA-sirut
   - norm-cdna ja norm-agilent eiv�t anna flagej�, koska niiden rivim��r� eroaa datan rivim��r�st�
     (koska datasta on laskettu replikaattispottien suhteen keskiarvo, mutta flageist� ei)
   - Kuvien antamat tulokset my�s tekstin�
	- Kaikki tool-sivut ajan tasalle
         - Supported-chiptype -sivulle my�s sirujen nimet systeemiss� (R:ss� esim hgu133a)
         - artikkeliviitteet
	- Getting started -sivu
- Kehitett�v��:
   - skriptit
      - Fold changen klusterointi
         - klusterointi-skripteihin valinta [chip., FC.] tms, jossa voi valita
           haluaako klusteroida chip. vai FC. sarakkeet.
      - Pathway analysis: show which genes contributes to the results
      - Ability to tell how big GO classes are taken into account in the analysis
      - Gene set test: show y-axis values
      - Arabidopsis promoter analysis
      - Many probes for the same gene: combine the expression values to one entry
      - kuvaavammat parametrinimet linear modellingissa (esim. main.effect1, technical.replicate, pairing, main.effect1.is.factor) 
      - storeyn Q-arvokorjaus yhdeksi FDR-vaihtoehdoksi
      - sort data 
      - extract rows
      - merge a table and a genelist
      - laajenna search-by-genename toiminnallisuutta
      - Bring in own annotations
         - New tools for running analyses using these
      - general normalization
          - mahdollisuus m��ritell� pudotusvalikoilla k�ytett�v�t sarakkeet
          - Agilentille oma skripti siten, ett� tab-delimited fileet vain tuodaan sis��n,
            ja skripti hoitaa kaiken muun?
      - HGU95A-sarjan siruja custom.chiptype:ksi norm-affy:yyn
      - Rikastuneiden GO-luokkien analyysin pit�isi palauttaa geenitkin (miten?)
      - MA-plot for Illumina
      - direct data import form GeneSpring
	- survival analysis -skripti
      - extract genes using a category (GO, KEGG) term
      - search by gene ontology class
      - Normalisointi positiivisten kontrollien (spiked controls) perusteella
      - normalisointi tiettyihin geeneihin
      - plot residuals from RLE/NUSE
      - permutation for multiple testing correction
      - Help in selecting a correct p-value cutoff (Qvalue package)
      - connectivity map?
      - other ontologies (cMAP, BioCarta, reactome, panther)
	   - using BioPAX through Rredland
      - Fix export to soft to include all information from phenodata
   - more wizards
      - GO-classification based on min 3-fold change
      - extract the significant pathways
      - QC
   - ISOT JUTUT
      - Gene arrays
      - Tiling arrays
      - ChIP-on-chip
      - CGH
      - SNP analysis
         - add support for Illumina chips
         - link to Plink
         - Sampsa Hautaniemen ryhm�n tuotokset
            - CohortComparator
               - Tsekkaa fortran-koodin k��nt�minen R:�� varten
	- fylogenetiikka
	- geenikartoitus
- R-kirjasto-ongelmia:
   - Seuraavat kirjastot eiv�t viel� asentuneet:
	- Rgraphviz
   - Skriptit, jotka eiv�t siksi toimi:
	- pathway-bayesian.R

---------------------------------------------------------------------------------------------------------------------------

T�h�n versioon kuuluvat skriptit:



---------------------------------------------------------------------------------------------------------------------------

Nykyiset ominaisuudet:



---------------------------------------------------------------------------------------------------------------------------

Muutokset edellisest� versiosta (0.5.7)
- Calculate-fold-change toimii nyt my�s vain kahdella sarakkeella. Aiemmin skripti kaatui siihen, ett� kun
  kahden sarakkeen data framesta poimitaan yksi sarake, se konvertoidaan automaattisesti vektoriksi, jolle
  ncol()-komento antaa dimensioiksi NULL. Homma korjattu siten, ett� vektori muutetaan aina ensin data frameksi
  ennen laskentaa.
- Hierarkkisen klusteroinnin palauttama tulostiedosto muutettu hc.txt:st� hc.tre:ksi yhteensopivuusongelmien
  v�ltt�miseksi.
- change-interpretation -skriptiss� ollut erhe, joka j�tti flag-sarakkeet dataan (ja kaatoi siis skriptin) on 
  korjattu.
- plot-dendrogram-skriptin oletusarvoksi muutettu chips genes sijaan.
- Uusi skripti: Laura Elo-Uhlgrenin ROTS
- Modifioitu normalisointi-tyokaluja siten, ett� normalisoituun dataan kirjoitetaan SYMBOL- ja DESCRIPTION-sarakkeet.
- calculate-fold-change -skripti antaa nyt my�s datan FC:n lis�ksi. FD muutettu tulosteessa muotoon FC.
- stat-two-groups: FD muutettu muotoon FC.
- stat-timeseries ei n�ytt�nyt parametria p.value.threshold. T�m� johtui siit�, ett� edellisen parametrin per�st�
  puuttuivat sulut. Sulut lis�tty.
- norm-agilent- ja norm-cdna-skripteihin lis�tty between arrays -normalisointioptioksi Aquantile.
- norm-affy -skriptin pit�isi nyt menn� l�pi, vaikkei annotaatiokirjastoa l�ytyisi, jolloin SYMBOL ja DESCRIPTION-
  sarakkeita ei dataan kirjoiteta. 


Muutokset edellisest� versiosta (0.5.6)
- Korjattu SAM-tyokalussa ollut virhe (samout generoitiin vasta sit� testiss� k�ytt�v�n if-lauseen j�lkeen).


Muutokset edellisest� versiosta (0.5.5)
- Versiomuutos johtuu Linux-Windows konversion ongelmista. Skripteiss� ei muutoksia.


Muutokset edellisest� versiosta (0.5.4)
- norm-affy -skriptiss� vsn-normalisointi antoi ulos expressionSetin eik� matriisia. Korjattu skripti vastaamaan
  t�t� muutosta R:ss�.
- stat-geneset -skripti ei toiminut current-asetuksella, koska yksi output-tiedosto puuttui. Korjattu.
- stat-linear-model kaatui, jos p-arvojen adjustoinnin k��nsi pois p��lt�. T�m� johtui siit�, ett� toptable:sta
  yritettiin ottaa sarake P.Values eik� P.Value, joka olisi ollut oikea. Korjattu.
- norm-affy -skriptiss� custom.chiptype ei toiminut esimerkiksi RMA-normalisoinnin kanssa. Syyn� oli, ett�
  funktion rma kutsu yritettiin ajaa objektille dat2, jota ei ollut olemassa, kun se olisi pit�nyt ajaa
  raakadatan sis�lt�v�lle dat-objektille. Korjattu.
- import-soft2 -skriptiin tehty pari p�ivityst�. Nyt voi valita haluaako datan muuntaa log2-muotoon vain ei.
  Lis�ksi korjattu virhe datan kirjoittamisen kanssa. Nyt ekspressioarvot otetaan eset-objektista ulos
  komennolla exprs(eset) eik� suoraan tuota slottia osoittamalla.
- uusi skripti: cluster-kmean-testK, joka testaa klusterointia eri klusterim��rill�, ja antaa tulokseksi
  kuvan, jonka perusteella optimaalista m��r�� voi arvioida.
- skripteihin norm-agilent, norm-agilent-one-color ja norm-cdna on lis�tty normalize.genes -asetukseen my�s
  mahdollisuus k�ytt�� VSN-normalisointia.
- NUSE-plottiin lis�� tilaa alamarginaaliin, sama heatmap:iin. Lis�tty.
- K�yt� sample-funktiota HC-kuvan tekemisess�, jos objektien m��r� on kovin suuri. Muutettu skripti� vastaavasti.
- Annotations as a tsv-table. Annotointiskripti palauttaa nyt my�s tekstitiedoston, jossa annotaatiot ovat.
- uusi skripti: stat-hyperG-safe, joka tekee GSEA-tyyppisen analyysin KEGG/GO-kategorioille.
- Lis�tty Agilent drosophila -annotaatiot normalisointiskriptien valintoihin.
- Jos filtter�intiskripti ajettiin tilastollisen testin j�lkeen, eiv�t p-arvot s�ilyneet tulostiedostossa.
  Nyt tilanteen pit�isi olla parempi sd/IQR/cv-filtterien osalta, joita ongelma koski.
- Tilastollisiin testeihin lis�tty enemm�n p-arvojen adjustointiin sopivia menetelmi� (stat-linear-modeling, 
  stat-one-group, stat-several-groups, stat-two-groups). Korjattu my�s t�h�n muutokseen liittyneet virheet
  (tolower muutti kaikki pienille kirjaimille, mik� ei en�� toiminut - korjattu if-lauseessa).
- plot-heatmap -skriptiss� punainen tarkoitti aliekspressoitunutta ja vihre� yliekspressoitunutta. Vaihdettu 
  toisin p�in, mik� on normaalik�yt�nn�n mukainen esitystapa.


Muutokset edellisest� versiosta (0.5.3)
- stat-hypergeo -skriptiin kovakoodattu, ett� KEGG- ja PFAM-paketit ladataan ennen analyysi�. Ennen t�m�
  tehtiin automaattisesti, jos analyysi� oltiin tekem�ss�, mutta Bioconductor-funktio lienee silt� osin
  muuttunut.
- Promoottorianalyysiskriptien polut muutettu Murskan polkuja vastaaviksi.
- Sekvenssianalyysiskriptein polut muutettu Murskan polkuja vastaaviksi.
- stat-snp-chisq -skriptist� puuttui testattavan sarakkeen valintamahdollisuus. Lis�tty.
- uusi skripti: promoter-tfbs-cosmo, joka toteuttaa TFBS-haun Bioconductor-projektin Cosmo-pakettia k�ytt�en.
- Skriptiin seqanal-msa on lis�tty mahdollisuus k�ytt�� Clustalia analyysin tekoon, jos dna-paketti on
  asennettu R:��n.
- uusi skripti: norm-illumina-lumi, joka tekee kaikki vaiheet lumi-pakettia k�ytt�en.
- Korjattu polkum��reet oikein (yksi kauttaviiva puuttui) promoottoriskripteihin.
- Korjattu seqanal-mafft -skriptiin mafft toimimaan (sh-tulkki UNIX:ssa ei tunnista module load komentoa).
- Korjattu seqanal-phylogenetics -skriptiin polkum��re clustaliin oikein (paste-komennosta puuttui pilkku).
- Korjailtu noita seqanal-skriptej� lis��. Nyt pit�isi toimia my�s varapalvelimella:lla.
- Korjattu change-interpretation-skriptiin oletusarvojoukkoon log2-linear.
- uusi skripti: delete-columns, joka deletoi datasta valitut sarakkeet. 


Muutokset edellisest� versiosta (0.5.2)
- norm-affy-exon-iptiin lis�tty mahdollisuus tuottaa joko geenikohtaiset tai eksonikohtaiset ekspressioarvot.
- norm-affy -skriptiin p�ivitetty uudet alt CDF:t, joille l�ytyy suoraan my�s annotaatiot osoitteesta:
  http://nugo-r.bioinformatics.nl/NuGO_R.html
- stat-two-group -skriptiin lis�tty mahdollisuus verrata kahden populaation variansseja (F-testi)
- stat-linear-model -skriptiin lis�tty mahdollisuus olla k�ytt�m�tt� korjattuja p-arvoja
- simpleaffy-kirjaston kontrolliprobien kuvaukseen on lis�tty drosophila2cdf:�� vastaava kuvaus
  (t�m� pit�nee muuttaa joka kerta kun R p�ivitet��n). Kuvaus l�ytyy tiedostosta $R_HOME/library/simpleaffy/extdata.
- Weeder-analyysin k�ytt��n lis�tty Drosophila
- promoter-tfbs -skriptiin korjattu virhe (promoottorin pituus: parametreiss� short, skriptiss� small)
- norm-agilent -skripti kaatuu, jos ajaa sen vain yhdellesirulle. Korjattu (A:sta ja M:st� lopuksi data frame).
   - Sama korjaus tehty skripteihin norm-agilent-one-color ja norm-cdna.
- norm-agilent (ja norm-cdna) -skripteiss� spottireplikaattien keskiarvoistaminen aiheutti sen, ett� rivinimet
  eiv�t en�� tulostuneet, jos normalisoitiin vain yksi siru. Ongelma on nyt korjattu.
- Promoottority�kaluihin lis�tty mahdollisuus tehd� analyysi my�s Drosophilalle.



Muutokset edellisest� versiosta (0.5.1)
- export-soft -skriptiin korjattu virhe, joka kaatoi export-ty�kalun, jos datassa ei ollut flagej�.
- norm-affy -skriptiin korjattu pari virhett�. Ensinn�kin MAS%- ja Plier-normalisoinnin j�lkeen ei
  palautettukaan log2-muunnettuja arvoja. Toiseksi Plier normalisointi pys�htyi aina virheilmoitukseen,
  sill� custom.chiptype-testiss� oli == sen sijaan ett� siin� olisi oikein lukunut !=.
- norm-agilent-one-color ei toiminut vaan kaatui. Korjattu useita virheit�, jotka johtuivat annotaatioiksi
  merkityn ControlType-sarakkeen v��r�st� k�sittelyst�.
   - Samat korjaukset tehty my�s norm-agilent-skriptiin.
- norm-agilent-skriptin kontrolliprobien poisto aiheutti sen, ett� gene set test ei toiminut. Nyt kontrolli-
  probien poisto on vapaaehtoista (default: no), mink� pit�isi korjata tilanne. Replikaattiprobeista otetaan
  silti edelleen keskiarvo.
- uusi skripti: norm-affy-snp, joka ottaa SNP-sirujen CEL-fileet ja laskee niist� genotyypit
- uusi skripti: stat-chisq-snp, joka ottaa normalisoidu SNP-chip datan ja ajaa sille assosiaatioanalyysit
  k�ytt�en Khiin neli�testi�. Ei ota huomioon perherakenteita.
- filter-by-column -skriptiss� on nyt mahdollista testata my�s yht�suuruus (ennen vain suurempi / pienempi kuin).
- uusi skripti: change-interpretation, joka joko muuntaa log2-arvot alkuper�isiksi tai muuntaa alkuper�iset arvot
  log2-muunnetuiksi.


Muutokset edellisest� versiosta (0.5.0)
- Kaikki t�ss� versiossa ovat skriptit on tarkoitettu R:n versioon 2.6.1, ja muutokset koskevat vain n�it�
  skriptej�!
- Stilisoitu skriptien otsikkohelppej� ja koodien kommentteja.
- Modifioitu seqanal- ja promoter-skriptien polkum��rityksi� siten, ett� oikea polku tarvitsee osoittaa
  skriptiss� vain yhdess� paikassa, joten muuttaminen on helppoa.
- norm-affy -skripti� korjattu kovalla k�dell�. Useimmat normalisoinnit uudelleen annotoiduilla siruilla eiv�t
  toimineet. Nyt pit�isi toimia.
- plot-venn -skriptiss� valittujen tiedostojen nimet eiv�t tulleet n�kyviin. Asia on nyt korjattu ja
  diagrammin eri osat on nimetty tiedostonnimien perusteella.
- calculate-descriptive-statistics -skriptiin lis�tty mahdollisuus laskea ne joko geeneille tai siruille.
- norm-illumina -skriptiss� oli ongelma: flagej� ei saatu konvertoitua kirjaimiksi. Korjattu ja nyt toimii.
- sort-samples skriptiss� oli virhe, eik� se toiminut, jos datassa ei ollut flagej�. Korjattu.
- norm-illumina -skriptiin korjattu Mouse V1-1 paketin nimi oikein.


Muutokset edellisest� versiosta (0.4.9)
- uusi skripti: seqanal-msa, joka tekee usean sekvenssin rinnastuksen FastA-muotoisille sekvensseille.
- uusi skripti: seqanal-phylogenetics, joka tekee fylogeneettisen analyysin Clustal-muotoiselle rinnastukselle.
- Luotu uusi kategoria quality control, johon QC-skriptit on siirretty.
- Fiksattu bugi norm-lme-skriptiin: sanity check oli ennen datan eristyst�, ei hyv�. 
- norm-nmds -skriptiss� oli kovakoodattu kuvan koko. Muutettu.
- plot-volcano -skriptiss� SE-kuvan y-akselin otsikko muutettu muotoon -log(p) eli oikein (oli SE).
- stat-sam -skripti kaatui, jos valitulla Delta-tasolla ei l�ytynyt yht��n merkitsev�� geeni�.
- stat-timeseries -skriptiin tehty muutamia bugikorjauksia (yksi } poistettu, p->p.cut, jne.).
- skriptit, joiden toimivuus R 2.6.1:ll� on testattu, ovat tools-R261 kansiossa. Plussalla 
  merkittyihin skripteihin tyhty muutoksia.
   - annotate-genelist
   - average-replicates
   - calculate-descriptive-statistics
   - calculate-fold-change
   - classification-knn
   + cluster-hierarchical
   - cluster-kmeans
   + cluster-qt
   + cluster-som
   - estimate-samplesize
   - export-soft
   - export-tab2mage
   - extract-genes-from-clustering
   - extract-genes-from-stattest
   - extract-samples-from-dataset
   - filter-by-column
   - filter-cv
   - filter-expression
   - filter-flags
   - filter-iqr
   - filter-sd
   - generate-phenodata
   - import-soft2
   - impute
   - merge-tables
   - na-omitted
   + norm-affy
   + norm-affy-exon
   - norm-agilent
   - norm-agilent-one-color
   - norm-cdna
   - norm-illumina
   - norm-lme
   - ordination-nmds
   - ordination-pca
   - (pathway-bayesian)
   - (pathway-boolean-bestim)
   - plot-boxplot
   - plot-chrom-pos
   - plot-corrgram
   - plot-dendrogram
   - plot-heatmap
   - plot-histogram
   - plot-idiogram
   - plot-venn-diagram
   - plot-volcano
   - plot-volcano-data-exists
   - promoter-accessory
   - promoter-cbust
   - promoter-retrprom
   - promoter-tfbs
   + qc-affy
   - qc-affy-rle-nuse
   - qc-agilent
   - qc-agilent-one-color
   - qc-cdna
   - qc-illumina
   - search-correlation
   - search-queryword
   - seqanal-msa
   - seqanal-phylogenetics
   - sort-samples
   - stat-correlate-with-phenodata
   - stat-geneset
   - stat-hyperG-GO
   - stat-hyperG-KEGG-PFAM
   + stat-linear-modelling
   - stat-one-group
   - stat-sam
   - stat-several-groups
   - stat-singleslide
   - stat-timeseries
   + stat-two-groups


Muutokset edellisest� versiosta (0.4.8)
- annotate-genelist -skriptiin oli j��nyt muuttamatta ekspressio- ja p-arvo-sarakkeiden default-arvot
  isoilla kirjaimilla. T�m� korjattu, ja skripti toimii taas.
- affy-norm-exon -skriptill� ei voinut k�ytt�� kuin rma-normalisointia, joten kaikki muut optiot on
  poistettu.


Muutokset edellisest� versiosta (0.4.7)
- uusi skripti: calculate-descriptive-statistics, joka laskee kullekin geenille perustilastoarvot
- uusi skripti: filter-by-column, joka filtter�i datan yhden valitun datan sarakkeen perusteella
- uusi skripti: norm-affy-exon, joka normalisoi Affyn exon-siruja
- norm-agilent-, norm-agilent-two-color- ja norm-cdna -skripteihin on lis�tty mahdollisuus k�ytt��
  taustan korjaamiseen normexp-menetelm��, joka on todettu (Bioinformatics 2007) parhaaksi menetelm�ksi,
  kun siihen liitet��n pieni offset (50), mik� on my�skin lis�tty optioksi skripteihin.
- norm-agilent- ja norm-agilent-two-color -skripteihin lis�tty zebrafish-sirun k�ytt�mahdollisuus.
- plot-chromloc -skriptin tuottaman kuvan taustav�riksi vaihdettu valkoinen ja puuttuvien havaintojen 
  v�riksi vaalean harmaa, joka tosin rasteroituu ik�v�sti PNG-kuvassa. Tulos on kuitenkin oletusarvoja
  parempi.
- uusi skripti: sort-samples, joka j�rjest�� n�ytteet ja fenodatan jonkin fenodatan sarakkeen mukaan
- norm-agilent-skripti� muutettu siten, ett� se ei en�� kaadu, vaikkei ControlType-saraketta olisikaan
  tuotu sis��n.
    - sama muutos tehty my�s norm-agilent-one-color -skriptiin 


Muutokset edellisest� versiosta (0.4.6)
- geneset test: tulosta tulokset my�s listana, jotta n�kee kaikkien kategorioiden tulokset. Korjattu.
- norm-agilent (1- ja 2-v�ri):
   - Replikaattispoteista lasketaan nyt oletusarvoisesti (eik� sit� voi k��nt�� pois p��lt�) keskiarvo,
     jos spotit ovat samalla sirulla.
   - Poistetaan kontrollispotit datasta automaattisesti
- norm-cdna:
   - Replikaattispoteista lasketaan nyt oletusarvoisesti (eik� sit� voi k��nt�� pois p��lt�) keskiarvo,
     jos spotit ovat samalla sirulla.
- PCA - add a possibility to scale the value before analysis. Lis�tty.
- uusi skripti: ordination-nmds, joka tekee siruille NMDS-analyysin, ja piirt�� siit� kuvan
- uusi skripti: filter-cv, joka filtter�i geenit niiden CV:n perusteella
- QT- ja K-means -clustering: R:n tuottamissa kuvissa eiv�t y-akselit ole samassa skaalassa. Korjattu.
- uusi skripti: qc-agilent, joka tekee QC:n Agilentin 2-v�ridatalle
- uusi skripti: qc-agilent-one-color, joka tekee QC:n Agilentin 1-v�ridatalle
- korjattu bugi stat-group-testist�. Empirical Bayes -testist� puuttui mahdollisuus bonferroni-korjauksen
  k�ytt��n (sit� ei laskettu skriptiss� ollenkaan, vaikka se oli valittu optioksi).
- norm-agilent -skriptiss� oli bugi sirutyyppi mgug4112a:n kohdalla: a puuttui lopusta. Korjattu.
- calculate-fold-change -skriptiss� ollut fold change laskenta korjattu oikein (FD<-dat3[,2]/dat3[,1]
  muutettu muotoon FD<-dat3[,2]-dat3[,1]).


Muutokset edellisest� versiosta (0.4.5)
- empirical bayes -testien pit�isi tulostaa my�s fold change (ei pelk�st��n lineaarisen mallin)
  Korjattu kahden ryhm�n testin osalta. Usean ryhm�n tapauksessa pit�isi k�ytt�� linear modelling-
  ty�kalua.
- limma-analyysi ei tulosta rivinimi� -> korjattava ASAP! Korjattu. Lis�ksi muutettu design-matriisin
  kirjoitusta siten, ett� levylle kirjoitettava tiedosto tulkitaan pelkk�n� tekstin�.
- limma-analyysi laittoi geenit v��r��n j�rjestykseen (tulokset on sortattu suurimmasta pieneimp��n,
  mit� ei ollut otettu huomioon tuloksia levylle kirjoitettaessa). Korjattu, toivottavasti oikein.
- annotate-skripti� p�ivitetty siten, ett� jos datalle on tehty tilastollisia testej� siten, ett� p-arvo
  ja fold change ovat saatavissa, niin k�ytt�j� voi valita lis��v�ns� ne annotoituun geenilistaan
  lis�informaatioksi.


Muutokset edellisest� versiosta (0.4.4)
- Illumina normalisointi ei toiminut, koska sarakkeiden nimet datassa ja rivien nimet fenodatassa
  eiv�t sis�lt�neet tiedostop��tett�. Nyt asian pit�isi olla kunnossa, ja homman toimia.
- stat-two-groups kaatui, koska sanity check-vaihe oli ennen muuttujien luomista, joiden suhteen
  nuo checkit ajettiin. Korjattu.
- Extract-samples-from-dataset kaatui, jso datasetti ei sis�lt�nyt call-arvoja. Nyt pit�isi toimia
  my�s datalle, jossa call-arvoja ei ole.
- stat-several-groups kaatui Kruskall-Wallisin testiin, sill� muuttujaa p ei ollut alustettu ennen
  silmukkaa. Korjattu.
- QT-clustering kuva R:st� saatuna n�ytt�� pahalta (x-akseli on liian pitk�, fiksaa kuten K-means:ssa).
  Korjattu.
- correlate-with-phenodata kaatuu, jos datasta on ensin laskettu ryhm�keskiarvot
   - tsekkaa, ett� datan ja fenodatan dimensiot ovat samat - jolleivat, anna virheilmoitus.
   - Korjattu.
- cluster-som kaatui siihen, ett� dists-objektista otettiin ensimm�inen sarake, vaikka objekti oli 
  oikeasti vektori. Korjattu.


Muutokset edellisest� versiosta (0.4.3)
- calculate-fold-change -skripti� muutettu hieman. Nyt se ei laskenut fold changea edes kahdelle ryhm�lle.
  Lis�ksi ulostulotiedoston nimeksi muutettu VVSADL:n vaatima fold-change. Lis�ksi fold change talletetaan
  sarakkeeseen chip.FD entisen FD:n sijaan.
- plot-heatmap -skripti� korjattu siten, ett� se odottaa tulostiedostoksi heatmap.png eik� esim.
  dendrogram-color.png, kuten aiemmin.
- plot-dendrogram -skriptiin lis�tty phenodata, jota ilman skripti ei tuottanut dendrogrammia.
- Muutettu tilastoskriptien oletusarvoiseksi metadata-sarakkeeksi group.
- stat-correlate-with-phenodata -skriptist� korjattu virhe: yritettiin py�rist�� my�s flagej�
  korrelaatioarvojen lis�ksi. Skripti kaatui virheilmoitukseen, ett� merkkidataa ei voi py�rist��.


Muutokset edellisest� versiosta (0.4.2)
- Kaikki merge-funktiot poistettu yht� geneerist� lukuunottamatta.
- norm-illumina -skripti muutettu toimimaan siten, ett� data on tuotu
  sy�tt�ty�kalulla, ja se on kirjoitettu levylle yksitt�isiksi tiedostoiksi.
- stat-two-groups- ja stat-several-groups -skripteist� poistettu viittaus SAM-
  analyysiin. 
- uusi skripti: stat-sam, joka laskee SAM-analyysi yhdelle, kahdelle tai useammalle
  ryhm�lle.
- KNN ei toimi, sill� group-saraketta ei voi j�tt�� tyhj�ksi test datalle.
   - virhe johtuu siit�, ett� predict-funktiolle sy�tet��n train-setti, joka
     on oikean mittainen, mutta cl-muuttuja puolestaan on koko vektorin mittainen
     (=ei hjuva).
   - Korjattu.
- filter-IQR ei tuota tulostiedostoa (library-komento puuttuu). Korjattu.
- K-means kuva, joka tuotettu R:ss� n�ytt�� oudolta. Nyt n�ytt�� paremmalta. Koodi
  oli skriptiss� hieman v��r�ss� j�rjestyksess�, mutta nyt pit�isi olla oikein (
  kuvan piirto ennen taulukon kirjoitusta).
- calculate-fold-change kaatuu. Ongelmana ainakin vaadittava ryhmien m��r�. Nyt 
  pit�isi toimia kunnolla.
- plot-dendrogram kaatui, koska toisen ulostulotiedoston nimi oli v��rin.
- plot-corrgram kaatui, koska corrgram-kirjastoa ei ollut ladattu ennen analyysi�
- plot-heatmap kaatui, koska heatmap() vaati sy�tteksi matriisin, ja sille annettiin
  data frame. Korjattu.
- Jos niiss� skripteiss�, joissa pit�� valita jokin fenodatan sarake ei ole valittu
  mit��n saraketta, niin ajetaan skripti group-sarakkeella, joka pit�isi aina olla
  t�ytettyn�. Muutokset tehty skripteihin:
     - average-replicates
     - calculate-fold-change
     - extract-samples-from-dataset
     - extract-genes-from-stattest
     - norm-lme
     - plot-dendrogram
     - plot-volcano-data-exists
     - stat-correlate-with-phenodata
     - stat-geneset
     - stat-linear-modelling
     - stat-sam
     - stat-several-groups
     - stat-timeseries
     - stat-two-groups
     

Muutokset edellisest� versiosta (0.4.1)
- stat-linear-modelling -skripti kirjoittaa nyt ulos my�s kunkin parametrin suhteen
  lasketun fold changen.
- uusi skripti: extract-genes-from-stattest, joka erottaa tilastollisten testien
  antamista tuloksista ne, joiden p-arvo on alle valitun raja-arvon.
- uusi skripti: volcano-plot-data-exists, joka plottaa tehdyn tilastollisen testin
  (linear model tai empirical bayes) tulokset Volcano plotin muotoon.
- Muutettu muutamiin plottiskripteihin kuvan koon asetukset oikein (600/72 -> w/72 ym.).
- norm-illumina -skriptiin lis�tty mahdollisuus k�ytt�� my�s ProbeID-annotaatioita.
- norm-illumina -skriptist� korjattu flagging-muuttujan k�sittelyyn liittyvi� virheit�.


Muutokset edellisest� versiosta (0.4.0)
- uusi skripti: stat-linear-modelling, jossa on toteutettu lineaaristen mallien k�ytt� data-
  analyysiin, kuten se on limma-paketissa toteutettu.
- norm-illumina-skriptiin lis�tty mahdollisuus valita sarake-erotin (, tai ; tai tab). Jos
  erotin on ;, on pilkun merkkin� , eli k�ytet��n "suomalaisia asetuksia".
- uusi skripti: norm-agilent-average-replicates, joka laskee normalisoinnin j�lkeen keskiarvon
  sirulla olevista kahdesta t�pl�replikaatista. T�m� toimii vain, jos replikaatteja on tismalleen
  kaksi.
- stat-skripteihin lis�tty parametri, jolla k�ytt�j� voi valita, mink� fenodatan sarakkeen
  suhteen testi halutaan tehd�.
- Hypergeometrinen testi ei palauttanut ikin� yht��n merkitsev�� tulosta, koska skriptille
  annetun datan sijaan testeissa k�ytettiin sirun kaikkien geenien listaa -> kaikilla geeneill�
  p-arvo 1. Korjattu.


Muutokset edellisest� versiosta (0.3.9)
- norm-lme-skripti muutettu siten, ett� k�ytt�j� voi valita random-efekti sarakkeen.
- cluster-som -skripti tuottaa nyt tuloksista my�s R:ss� generoidun kuvan.
- cluster-kmeans -skripti tuottaa nyt my�s kuvan klustereista.
- cluster-qt -skripti tuottaa nyt my�s kuvan klustereista.
- uusi skripti: extract-genes-from-clustering, joka tallentaa uudeksi datatiedostoksi
  tietyn klusterin geenit.
- uusi skripti: norm-agilent-one-color, joka normalisoi Agilentin 1-v�ridatan.
  (elina.palonen@btk.fi).
- uusi skripti: norm-illumina-separate-files, joka normalisoi Illumina sirut, jos ne
  on tuotu sis��n import tool:n kautta.
- uusi skripti: plot-corrgram, joka visualisoi n�ytteiden v�liset korrelaatiot hauskana
  kuvana. 
- uusi-skripti: extract-samples-from-dataset, joka antaa k�ytt�j�n poistaa n�ytteit� ja
  fenodatan rivej� tietyn fenodatan sarakkeen perusteella.
- normalisoimaton Illumina-data tuli ulos raaka-arvoina. Muutettu tuottamaan log2-muunnettuja
  arvoja.
- uusi skripti: merge-tables-exp-with-annot, joka yhdist�� tabulaarisen ekspressiodatan
  tabulaarisen annotaatiodatan kanssa.
- norm-illumina -skripti� p�ivitetty siten, ett� ainoastaan jos flagit halutaan tuottaa,
  oletetaan ett� datassa on olemassa Detection-sarakkeet. Lis�ksi, jos Detection sarakkeita ei
  ole, ei skripti edes yrit� tuottaa flagej�. Pit�isi olla vikasietoisempi nyt.
- uusi skripti: filter-iqr, joka filtter�i geenit niiden kvartiiliv�lin pituuden perusteella.


Muutokset edellisest� versiosta (0.3.8)
- stat-hyperG -skripti jaettu kahtia siten, ett� GO-analyysi on omassa skriptiss��n, ja
  KEGG/PFAM omassaan.
- norm-agilent -skripti ei kirjoittanut flag-arvoja, vaikka ne oli dataa tuotaessa m��ritelty.
  T�m� muutettu toimimaan samalla periaatteella kuin cDNA-normalisoinnissa.
- filter-sd ei toimi yhdell� sirulla, eik� toimi filter-flag:k��n. T�ll�in yritet��n
  valita subscriptill� tyyliin [1,] vektorista tai tyyliin [,1] vektorista. Paha, paha juttu.
   - Nyt t�m� on korjattu, ja skriptien pit�isi toimia. Lis�ksi nyt voi SD-filtteriss� k�ytt��
     vain geenien suhteen filtter�inti�, joten siruja on oltava v�hint��n 2.
- uusi skripti: na-omit.R. Poistaa datasta kaikki rivit, joilla on puuttuvia havaintoja.
- cDNA- ja Agilent-normalisointi ei nyt en�� kaadu flag-sarakkeen puuttumiseen, mutta 
  kyll�kin, jos jokin muista vaadituista sarakkeista puuttuu.
- K�ytt�liittym� kaatuu, jos stat-hyperG-GO ajetaan, eik� yht��n merkitsev�� geeni� l�ytynyt. 
  Skripti� muutettu hieman toisenlaiseksi, jolloin HTML-file luodaan suoraan kirjoittamalla
  tagit tiedostoon eik� R:n helpperi-funktiolla.
- SD-filtteriss� oli edelleen maininta muuttujasta marg. Se on nyt poistettu if-lauseesta,
  ja nyt skriptin pit�isi toimia.
- uusi skripti: plot-venn-diagram piirt�� Vennin diagrammin kolmelle datasetille.
- uusi skripti: calculate-fold-change.R laskee kahden ryhm�n keskiarvojen perusteella fold changen
- PCA-skriptiss� ollut virhe korjattu (samples->chips). Nyt PCA toimii my�s n�ytteille.
- uusi skripti: merge-illumina-files, joka yhdist�� kaksi systeemiin ladattua bead summary data 
  -tiedostoa. Yhdist�misen j�lkeen tiedoston voi normalisoida norm-illumina -skriptill�.
- uusi skripti: generate-phenodata, joka kirjoittaa tyhj�n phenodatan, jos skripti ajetaan
  esinormalisoidulle datalle.
- uusi skripti: plot-boxplot, joka piirt�� boxplotin normalisoidulle datalle.
- stat-geneset -skriptiss� viitattiin viel� hgu133aSYMBOL-ymp�rist��n, joka korvattiin siten,
  ett� skripti toimii kaikilla annotaatiopaketeilla.
- search-queryword-skriptiss� viitattiin edelleen hgu133a-ymp�rist��n, mutta t�m� muutettiin k�ytt�m��n
  oikeaa ymp�rist�� kaikille siruille, joista annotaatioita on.
- Kaikkin plot-funktioihin lis�tty mahdollisuus s��t�� kuvan kokoa.
- norm-cdna- ja norm-agilent -skripteihin tehty pieni muutos flagien k�sittelyyn, jotta ne saadaan mukaan
  aina, olipa sarakkeet m��ritelty miten vain. 
- uusi skripti: plot-dendrogram, joka piirt�� kaksi erilaista dendrogrammia.
- uusi skripti: plot-heatmap, joka piirt�� heatmapin.
- uusi skripti: plot-histogram, joka piirt�� kullekin sirulle histogrammin


Muutokset edellisest� versiosta (0.3.7)
- Manuaalia laajennettu ja parannettu huomattavasti.
- Illumina RatRef-12 lis�tty annotaatiokirjastoihin, ja normalisointiskripti�
  korjattu vastaavasti.
- Agilent-sirujen (ihminen, hiiri ja rotta) annotaatiot lis�tty, ja normalisointiskripti�
  muutettu vastaavasti. Nyt Agilent-normalisoinnin yhteydess� pit�� valita sirutyyppi.
- cDNA-QC-skriptin kaikkien MA-plottien Y-akselien pit�isi olla samalla skaalalla. OK.
- K��nnetty qc-affy-rle-nuse -skriptin sirujen nimet pystysuoraan, jotta ne ovat luettavissa
  kaikille siruille.
- ClusterBuster-skripti palauttaa nyt tuloksessa transkriptiofaktoreitten nimet, ei niiden
  matriisiluokkia kuten aiemmin.
- Muuta fenodata tuottamaan sarakkeet j�rjestyksess�: sample, chiptype, group, training. OK.
- hyperG-skriptin silmukoissa oli pient� s��t�mist�, koska if..else-lauseet oli muotoiltu v��rin.
  Nyt ne on korjattu, ja skriptin pit�isi toimia oikein.
- Volcano plot implementoitu.
- Agilent-normalisointia korjattu - sirutyypin valinta ei onnistunut. Nyt pit�isi toimia.


Muutokset edellisest� versiosta (0.3.6)
- Lis�tty skriptiin stat-several-groups parametrin use.simple.analysis, jota AffyWizard
  k�ytt�� asetuksella yes. Oletusarvoisesti se on pois p��lt�.
    - stat-several-groups-foAffyWizard skripti on poistettu.
- importGDS-skripti lis�� nyt sarakkeiden nimien eteen "chip.", jotta analyysitkin toimivat.
- stat-geneset -skriptist� korjattu pieni bugi, joka esti kuvan otsikoiden tulostumisen oikein
  oli (names(substr(...)) kun pit�isi olla subtr(names(...)).
- stat-hyperG -skriptist� poistettu yksi ylim��r�inen } yhden if-lauseen lopusta.
- search-correlation -skriptist� poistettu debuggaus-rivit.
- P�ivitetty hc-skripti�: write.tree() -> format="Newick" poistettu.
  - Muutettu takaisin, sill� muutoin skripti antaa jotain ihan h�p��.
- P�ivitetty skripti� stat-hyperg siten, ett� sen pit�isi toimia, vaikkei merkitsevi�
  tuloksia olekaan saatu.
- Lis�tty norm-cdna -skriptin generoiman fenodatan sample-sarakkeen nimien per��n ".tsv". Nyt
  normalisoinnin pit�isi toimia.
- suurenna hc-bootstrapping fonttikokoa - OK, nyt 0.75 (ennen 0.5)
- PCA kaatuu (Error in if (no < 3) { : missing value where TRUE/FALSE needed)
    - Ongelmana oli parametri, joka pyydettiin prosentteina (0-100) eik� desimaalilukuna
      (0-1). Ongelma korjattu jakamalla prosenttiluku sadalla.
- average-replicates -skriptin pit�� palauttaa sarakeotsikot tyyliin "chip.group1" ei
  "group1" - OK.
- norm-cdna -skripti ei kirjoita flagej�, vaikka ne olisi m��ritettykin - OK
   - Flagit luettiin v��r�st� objektista (dat3), mutta nyt sen pit�isi toimia (luetaan
     objektista dat)
- impute-skriptiin lis�tty tarkistus, ett� onko A (average) -matriisin sarakeluku yli 0.
  Jos on, niin kirjotettavaan tiedostoon liitet��n my�s average-sarakkeet, joten imputen
  pit�isi toimia nyt my�s cDNA-datalle.
- qc-cdna kaatuu (mihin?)
   - Useita juttuja meni pieleen, erityisesti puuttuvien havaintojen tapauksessa.
     Nyt pit�isi toimia.
- filter-expression: korjattu pieni bugi, joka esti funktion toimimisen yhdell� sirulla
  (t�ll�in palautettiin scaled.dat2-vektori, jolle apply-funktiota ei voinut k�ytt��, nyt
  muutetaan tuo vektori ensin dataframeksi).
   - sama ongelma vaivasi filter-sd -skripti�, mutta sekin on nyt korjattu.
- filter-flags antoi virheilmoituksen yhdelle sirulle, koska vektorin sarakkeiden lukum��r�
  oli looginen nolla. Nyt muutettu flagit ensin datakehikoksi, ja sitten siit� lasketaan
  sarakkeiden lukum��r�.


Muutokset edellisest� versiosta (0.3.5)
- Lis�tty stat-several-groups- skriptiin empirical Bayes -menetelm� (limma).
  Affy-wizard k�ytt�� t�t�, ja se toimii todella nopeasti.
- stat-two-groups -skriptiin lis�tty empirical Bayes -menetelm� (limma)
- norm-cdna-skripti luki rivien nimet annotaatioksi. Muutettu rivien nimet geenien nimiksi
  ja annotation-sarake annotaatioksi.
- Utility-kategoriaan v��rin sijoitetut skriptit on nyt siirretty utilities-kategoriaan.
- ClusterBuster palautti tyhj�n tiedoston, koska jasparin matriisitietokantaa ei l�ytynyt. 
  Ongelma on nyt korjattu (jaspar_cor.tsv muutettu ohjelmakutsussa oikein muotoon 
  jaspar_core.txt).
- export-funktiot kaatuivat heti alkuun, koska luettavan fileen nimi oli normalized. Oikea
  file oli tietysti normalized.tsv, mik� onkin nyt korjattu.
- Lis�tty cbust-skriptiin ohjelman asetukset (-c, -m, ... ,-p)
- Lis�tty pudotusvalikko norm-illumina-skriptiin. Valikosta valitaan siru, jonka mukaan valitaan
  k�ytett�v� annotaatiokirjasto.
- search-queryword -skriptist� korjattu kaksi bugia, joiden takia haku aina ep�onnistui.
  Query oli muodoissa "chr" ja "genename". Nyt yhten�istetty.
- Affy-wizardille tehty oma versio stat-several-groups -skriptist�. Se palauttaa merkitsev�t
  geenit, jos niiden adjusted p-arvo on yli p-arvon kriittisen arvon. Muutoin palautetaan
  100 raa'alta p-arvoltaan merkitsevint� geeni�.
- Muutettu one-sample-text raportoimaan adj.p.value -sarake entisen sekasiki�nimen sijaan.
- Muutettu estimate-samplesize tuottamaan 600/72 tuuman kokoisia kuvia entisen 600 tuuman sijaan.
- clust-hc -skriptiss� ollut kirjastojen latausvirhe korjattu, ja nyt bootstrap-analyysinkin
  pit�isi toimia.
- Kommentoitu SOM-skriptist� pois gridin koon j�rkevyystestaus.
- stat-genesettest-skriptiss� poistettu parametri ylab par-asetuksesta ja siirretty se plot-komentoon,
  mihin se oikeasti kuuluisikin.


Muutokset edellisest� versiosta (0.3.4)
- Muutettu .txt -> .tsv, joka viittaa nyt vain tab-delimited text -tiedostoon. Samaten
  muutettu .text -> .txt, joka viittaa nyt geneeriseen tekstitiedostoon.
- Muutettu SOM-skriptin oletusv�ritys sini-keltaiseksi
- Muutettu hierarkkisen ryhmittelyanalyysin skripti� siten, ett� tuloksena
  on bootstrapin tapauksessa 600/72 tuuman kuva 600 tuuman kuvan sijaan.
- Lis�tty parametrien tsekkausta skripteihin:
   - average-replicates
   - classification-knn
   - cluster-hierarchical
   - cluster-kmeans
   - cluster-som
   - filter-flags
   - filter-sd
   - norm-lme
   - stat-one-group
   - stat-several-groups
   - stat-two-groups   
- export-soft -funktio (kuten tab2mage-funktiokin) tulostaa nyt vain blankon SOFT-fileen.
- fiksattu bugi impute -skriptiss�
- fiksattu bugi stat-correlate-with.phenodata -skriptiss�. Ajo kaatui, koska skriptille ei
  sy�tetty fenodataa, vaikka se tarvittiin skriptin suoritukseen.
- Hierarkkinen clusterointi palauttaa nyt tiedoston hc.txt entisen hc.tsv:n sijaan.
- norm-affy -skriptiss� valitaan nyt custom chip type pudotusvalikosta vahinkojen v�ltt�miseksi.


Muutokset edellisest� versiosta (0.3.3)
- Normalisointiskripteihin tehty muutos siten, ett� skripti kirjoittaa fenodatan 
  k�ytt�liittym�n sijaan. Samalla lis�tty parametri, jolla voidaan asettaa custom
  chiptype. Muutokset tehty skripteihin:
    - norm-affy.R
    - norm-illumina.R
    - norm-cdna.R
    - norm-agilent.R
- Normalisointiskriptit tuottavat nyt tiedoston phenodata.txt eiv�tk� yrit� en��
  lukea sit� sis��n, ja tuottaa phenodata_out.txt:t�.
- Uusi skripti: tab2mage.R, joka kirjoitta tyhj�n tab2mage-tiedoston, jonka k�ytt�j�
  voi t�ytt�� vaikka Exceliss�.
- Uusi skripti: import-SOFT.R, joka hakee GDS:n suoraan GEO:sta ko. numerolla, ja
  luo siit� normalisoidun datan ja fenodatan.
- Illumina koodia muokattu siten, ett� se pystyy lukemaan my�s BeadStudio 3:lla tuotetut 
  tiedostot, joissa header-informaatiota on 8 rivia aiempien versioiden 7 sijaan. T�m�
  vaati pieni� muutoksia datan sis��nluvussa ja flag-arvojen generoinnissa. Lis�ksi
  k�ytt�j�n on nyt itse valittava BeadStudion versionumero, jolla data on generoitu.


Muutokset edellisest� versiosta (0.3.2)
- Uusi skripti: promoter-retrprom, joka vain hakee promoottorisekvenssit tietokannasta,
  muttei tee niill� se pidemm�lle menevi� analyysej�.
- qc-affy -skripti muokattu k�ytt�m��n simpleaffy-kirjaston funktioita.
- Uusi skripti: qc-affy-rle-nuse, joka tuottaa RLE- ja NUSE- plotit Affymetrix datalle.
- Uusi skripti: stat-correlate-with-phenodata, joka korreloi geeniekspression 
  phenodatan kanssa. 
- Uusi skripti: average-replicates, joka laskee kunkin ryhm�n keskiarvon.
- Uusi skripti: stat-hyperG, joka tekee hypergeometrisen testin GO/KEGG/PFAM-luokille.


Muutokset edellisest� versiosta (0.3.1)
- qc-affy -skripti piirt�� nyt eri sirut eri v�reill�, ja tekee kuvaan kuvatekstin,
  jossa sirujen nimet ja v�rit on spesifioitu.
- clust-som -skrptiin lis�tty sarake, joka kertoo gridin koon (x*y). GUI:n piirt�m�
  kuva tehd��n t�m�n perusteella.
- Korjattu promoter-cbust -skriptiss� ollut virhe, joka esti promoottorisekvenssej�
  latautumasta oikein objektiin upstream.
- Korjattu search-correlation -skriptiss� ollut vika, joka kaatoi analyysin, jos 
  grep-komento l�ysi useampia geenej�, joissa sama string esiintyi.
- Korjattu search-queryword -skriptiss� parametrien nimiin liittyv� ristiriita.
  N�in ainakin AFfymetrixID:n perusteella toimiva etsint� saatiin toimimaan.
- promoter-cbust -skriptiss� ollut tulostiedosto clusters.txt muutettu muotoon
  clusters.text, jolloin k�ytt�liittym� osaa sen n�ytt�� (.txt viittaa aina matriisiin).


Muutokset edellisest� versiosta (0.3.0)
- norm-illum -skripti muutettu toimimaan k�ytetyn Bioconductor-version (1.9) kanssa.
- norm-illum -skriptin normalisointifunktiot (qspline ja quantile) muutettu k�ytt�m��n 
  affy-paketin normalisointia.
- lis�tty skripti Agilent-datan sis��nlukua ja normalisointia varten.
- cDNA / Agilent -skripteihin tehty muutoksia flagien k�sittelyn osalta. Jos flagej� ei ole
  ei niit� yritet� en�� kirjoittaakaan, joten skripti ei kaadu niihin.
- Korjattu cbust-skriptiss� kirjoitusvirhe, joka esti oikean annotaatiokirjaston latautumisen.
- filter-sd -skriptin kuvausta muokattu: retain->filter out.
- stat-geneset -skriptiss� muokattu kuvien marginaaleja.


Muutokset edellisest� versiosta (0.2.9)
- Hierarkkinen klusterointi tuotti dendrogram.txt:n, mutta GUI odotti hc.txt:t�. Korjattu.
- KNN-skriptiss� parametrien arvojen testauksessa oli kirjoitusvirhe (sulku puuttui). Korjattu.
- Idiogram menee ajoon, ja tekee tuloksen, mutta kuva ei ilmesty. GUI odotti tiedostoa
  idiogram.png, mutta tuotettiinkin midiogram.png, mist� ongelma johtui. Korjattu.
- Geneset test -skriptiin korjattu GO-luokkien testaus toimivaksi (muuttujan nimi oli v��rin.
- Korjattu bugi plier-kirjaston latauksessa plier-normalisoinnin yhteydess�.
- KEGG/GO-analyysin tuottaman kuvan marginaaleja pienennetty, jotta kuvasta tulee informatiivisempi.
  Samalla my�s tekstikokoa suurennettu hieman.
- qc-affy -skripti korjattu toimimaan my�s yhdell� sirulla.
- qc-affy -skripti� muutettu siten, ett� sirut ovat nyt sarakkeina ja kontrolliarvot rivein�. 
  Lis�ksi muutettu rivien ja sarakkeiden nimi� hieman.
- qc-affy-skriptiss� oletettiin tulevan .jpg-kuva, mutta tulikin .png-kuva. Korjattu.
- stat-geneset-skriptist� korjattu kirjoitusvirhe, joka esti kuvien marginaalien skaalauksen.
- SD-filtteriskriptiss� "retain" muutettu muotoon "filter.out".
- promoter-cbust -skriptiss� korjattu promoottorien kokoon liittyv�n vertailuoperaattorin
  kirjoitusvirhe.
- promoter-tfbs -skripti� muutettu siten, ett� medium-analyysi ajaa my�s small-analyysin. 
- promoter-tfbs -skriptist� poistettu mahdollisuus ajaa large- ja extra-analyysej�
- promoter-tfbs -skripti ei en�� kirjoita seqs.txt.wee tiedostoa. Tai kirjoittaa kyll�,
  mutta sit� ei en�� n�ytet� k�ytt�j�lle.


Muutokset edellisest� versiosta (0.2.8)
- KNN-skripti muutettu toimivaksi (erottelee .chip- ja muut sarakkeet toisistaan aluksi)
- Hierarkkinen klusterointi muutettu k�ytt�m��n pvclust-kirjastoa bootstrap-analyysiss�.
- Hierarkkinen klusterointi tuottaa sulkukaavion, joka voidaan parsia VISKI-projektin
  visualisointipalikan k�ytt��n.
- Search-correlation-skriptiss� korjattu virhe, joka aiheutti sen, ett� skripti toimi vain
  Affyn hgu133a-siruilla, ja l�ysi silloinkin vain tietyn geenin kanssa samalla tavalla
  ekspressoituneet geenit.
- Idiogrammi-funktiolta puuttui tieto ihmisen kromosomiraidoista. Ongelma korjattu, ja nyt
  skripti toimii.
- Fiksattu bugi one-group test ja several groups test -skripteiss�. T�m� liittyi korjattujen
  p-arvojen laskemiseen.
- Fiksattu pieni bugi (?) PCA-skriptiss�. Jostain syyst� skripti toimii hyvin R:ss�, muttei en��
  jos se suoritetaan NAMI:n kautta. Testataan toimiiko korjattukaan versio NAMI:ssa.
     - Ei toimi viel�k��n, vaikka samalla datalla skripti toimii R:ss�
- Fiksattu SOM-skriptiss� v�rien m��r� vastaamaan solujen lukum��r�� siten, ett� kukin solu,
  joka on yht� kaukana ensimm�isest� solusta v�rj�t��n samalla v�rill�.
- Chromosome position plot:ssa ei eroteltu dataa ja flagej�, joten skripti kaatui heti k�rkeen.
- Muutamien kuvien tuotossa tiedostop��tteet muutettu oikeiksi (jpg->png).
- Stat-two-group-tests -skriptiss� ladataan nyt LPE-kirjasto ennen ko. testin suorittamista.
- Stat-one-group -skriptiss� muutettu Wilcoxon-testin k�ytt�m� datakehikko vektoriksi, jolloin
  testi toimii oikein.
- Search-queryword -skriptiss� korjattu verailuoperaattoriin liittyv� virhe, joka esti skriptin 
  ajamisen.
- Muutettu search-correlation -skripti� siten, ett� korrelaatio voi olla v�lilt� 0-1 (aiemmin
  -1...1).
- Kutsut png-komentoon on muutettu muotoon bitmap("filename", width=w/72, height=h/72).
- Korjattu muutama ly�ntivirhe (dat, kun piti olla dat2) plot-chrom-pos -skriptiss�.
 

Muutokset edellisest� versiosta (0.2.7)
- Manuaalin ensimm�inen draft valmis.
- Muutettu hakemistopuuta hieman
- Muutettu normalisoinnin yhteydess� tapahtuvaa phenodatan levylle kirjoitusta: phenodata-taulukko kirjoitetaan aina
  aluksi levylle, ja siihen lis�t��n my�hemmin chiptype-sarake, jos se siit� puuttuu.


Muutokset edellisest� versiosta (0.2.6)
- Skriptien nimi� GUI:ssa muokattu hiukan (suodin-skripteiss� kerrotaan t�m� nyt jo nimess�, jne.)
- Affymetrix-normalisointeihin lis�tty PLIER-algoritmi
- MAS5- ja PLIER-normalisoidulle datalle on mahdollista tehd� VSN-normalisointi (mutta muille ei, sill� niiden
  antamat arvot on jo log2-muunnettu, mik� ajaa saman asian)
- Lis�tty skriptiin classification-knn.R tarkistuksia asetusten oikeellisuuden suhteen ja poistettu phenodata-taulukkoa
  muokkaavat ominaisuudet.
- Lis�tty skripti otoskoon ja voiman arviointiin (estimate-samplesize.R)
- Hierarkkinen klusterointi toteutettu uudelleen amap-kirjaston hcluster-funktiota k�ytt�en. T�m� mahdollistaa
  puun muodostamisen suurillekin (>20000 havaintoa) aineistoille suhteellisen lyhyess� ajassa (~30 min).
- Hierarkkisen klusteroinnin uudelleenotantafunktio on muutettu t�ysin. Nykyinen toteutus k�ytt�� pvclust-kirjastoa,
  joka tuottaa valmiin kuvan, johon bootsrapping-arvot on aseteltu. Aiempi toteutus laski bootstrapping-puun hitaasti
  yksitt�isi� funktioita k�ytt�en. Nykytoteutus on nopeampi (yli 6000X), mutta toisaalta ainoastaan pearsonin 
  korrelaatiota voidaan k�ytt�� puun muodostamiseen.
- Tunnettujen transkriptiofaktoreitten sitoutumiskohtien etsimiseen sekvensseist� k�ytet��n ClusterBuster-ohjelmaa.
  T�m� on uusi skripti TFBS-ty�kalujen joukkoon.


Muutokset edellisest� versiosta (0.2.5)
- GEO:ssa tapahtuneet batch submission -ohjeiden mukaiset muutokset toteutettu export-soft.R skriptiin.
- Idiogrammin piirt�v� skripti muokattu toimivaksi
- Geenien kromosomaaliset positiot visualisoiva skripti plottaa nyt eri v�reill� yli- ja aliekspressoituneet geenit
- Phenodataan ei en�� kirjoiteta chiptype-saraketta, jos se on jo olemassa


Muutokset edellisest� versiosta (0.2.4)
- Promoottority�kalu muokattu toimimaan UCSC:st� ladatuilla RefSeq-promoottorisekvensseill�
   - weederlauncher.out ei toiminut, joten skripti k�ytt�� nyt weederTFBS:��
- Korjattu bugi filter-expression.R skriptiss�. Bugi aiheutti sen, ettei skripti poistanut
  filtter�inniss� datasta yht��n geeni�. 


Muutokset edellisest� versiosta (0.2.3)
- LME-skripti muutettu k�ytt�m��n nlme-kirjastoa lme4:n sijaan.
- bestim.R-skripti tehty bnBestFit-ty�kalun integrointia varten.


Muutokset edellisest� versiosta (0.2.2)
- Uudet skriptit:
   - cPlot-cColor.R
   - GOFisherTest.R
   - idiogram.R
   - promoter.R
- LME-skripti korjattu k�ytt�m��n oikeita residuaaleja


Muutokset edellisest� versiosta (0.2.1):
- Skriptien parametrien nimet on pyritty muuttamaan informatiivisiksi


Muutokset edellisest� versiosta (0.2.0):
- Phenodata luetaan samasta kansiosta kuin datakin
- Bayesian network-skripti on omana kategorianaan eik� en�� timeseries-skriptien osana
- One-sample t-testi toimii noin 500-kertaa nopeammin kuin aiemmin, funktio kirjoitettu uudelleen


Muutokset edellisest� versiosta (0.1.4):
- Bootstrapping-validointi lis�tty hierarkkiseen ryhmitttelyanalyysiin
- Aikasarja-analyysi:
   - Etsi periodisesti muuttuvat geenit
   - Muodosta network
   - Independent component analysis


Muutokset edellisest� versiosta (0.1.3):
- Muutettu otsikkotietoja edelleen...


Muutokset edellisest� versiosta (0.1.2):
- Korjattu otsikkotiedot
   - Kirjoitusvirheet korjattu
   - Skriptit luokiteltu analyysiymp�rist�n luokkiin:
      - Normalisation
      - Preprocessing
      - Clustering
      - Statistics
      - Utility
   - K�ytt�m�tt�m�t luokat
      - Spot filtering
      - Expression
      - Pathways


Muutokset edellisest� versiosta (0.1.1):
- Linear Mixed Model korjattu
   - Mahdollistaa satunnaisvaikutusten (batch effect) poistamisen normalisoidusta datasta


Muutokset edellisest� versiosta (0.0.2):
- tuki cDNA- ja Illumina-siruille
   - normalisointi
   - monet analyysioptiot toimivat my�s n�ill� siruilla
- tuki phenodatalle
   - kaikki skriptit lukevat tarvittaessa parametrit tiedostosta phenodata.txt
   - phenodata.txt:n oletetaan olevan hakemistossa ../../common. Jos se puuttuu
     kirjoitetaan normalisointiskriptien yhteydess� dummy-demodata
   - Siis, tuotaessa dataa systeemiin, on tuotava my�s phenodata ennen 
     normalisointia.
   - Normalisoinnin yhteydess� phenodataan lis�t��n yksi sarake, jolla kerrotaan
     sirutyyppi, esimerkiksi Affymetrix-sirun versio.
- Laadun tarkistus (QC) sek� Affymetrix ett� cDNA/Illumina-datalle
- Filtter�inti� parannettu, mm.
   - Ekspressioarvon perusteella filtter�it�ess� voidaan m��ritt�� kuinka monella
     sirulla arvon tulee toteutua, jotta arvo l�p�isee filtterin.
- Monipuolisemmat tilastolliset ty�kalut
   - LPE, SAM, etc.
   - Yhdelle sirulle soveltuvat menetelm�t (noise-envelope ja Newton)
- Toimiva clusterointi ja useita menetelmi�
   - Et�isyyden voi valita et�isyyksien tai korrelaatioiden joukosta
   - Hierarkkisessa klusteroinnissa tuloksen luotettavuuden voi testata permutaatio-
     testill�
- P��komponenttianalyysi
   - Datasetti koodataan uudelleen halutulla m��r�ll� p��komponentteja
- KNN-menetelm��n perustuva "diskriminanttianalyysi"
- Datan kirjoitus ulos GEO:n SOFT-formaatissa
   - Edellytt��, ett� data on kuvattu hyvin phenodata.txt-tiedostossa, ks.
     http://staff.csc.fi/~jtuimala/SOFT/
- Muut toiminnallisuudet
   - Taulukoiden yhdist�minen geenin nimien perusteella
   - Geenien etsint� korrelaation, nimen, Affymetrix ID:n tai kromosomilokaation 
     perusteella


---------------------------------------------------------------------------------------------------------------------------

R Murskalla

- 64-bittinen versio
   gzip R-2.6.1.tar.gz
   tar xvf R-2.6.1.tar
   cd R-2.6.1
   ./configure
   make
   make check
- Lis�paketit
  # Basic CRAN packages
  install.packages(c("Matrix", "lme4", "amap", "ape", "flexclust", "kohonen", "e1071", "sma", "fastICA"), repos="http://cran.r-project.org", dependencies = T)
  install.packages(c("XML"), repos="http://cran.r-project.org", dependencies=T)
  install.packages(c("aplpack", "corrgram", "deal", "outliers", "pvclust", "zoo"), repos="http://cran.r-project.org", dependencies=T)
  install.packages(c("pvclust"), repos="http://cran.r-project.org", dependencies=F)

  # Basic Bioconductor packages
  source("http://www.bioconductor.org/biocLite.R")
  biocLite()
  biocLite(c("ctc", "ssize", "LPE", "Ruuid", "graph", "affyQCReport", "GlobalAncova", "impute", "idiogram", "GOstats", "beadarray", "GeneTS", "simpleaffy", "globaltest"))
  biocLite(c("geneplotter", "biomaRt", "lumi", "prada", "siggenes", "plier")) 
  biocLite("cosmo")

  # Annotation packages for Affymetrix
  # Human and yeast
  biocLite(c("hgu133a", "hgu133acdf", "hgu133aprobe", "ygs98", "ygs98cdf", "ygs98probe", "hgu133a2", "hgu133a2cdf", "hgu133a2probe", "hgu133plus2", "hgu133plus2cdf", "hgu133plus2probe"))
  biocLite(c("hs133ahsrefseq", "hs133ahsrefseqcdf", "hs133ahsrefseqprobe", "hs133av2hsrefseq", "hs133av2hsrefseqcdf", "hs133av2hsrefseqprobe", "hs133phsrefseq", "hs133phsrefseqcdf", "hs133phsrefseqprobe"))
  biocLite(c("GO", "KEGG"))
  biocLite("PFAM")

  # Mouse and Rat
  biocLite(c("mgu74a", "mgu74acdf", "mgu74aprobe", "mgu74av2", "mgu74av2cdf", "mgu74av2probe", "moe430a", "moe430acdf", "moe430aprobe",
             "mouse4302", "mouse4302cdf", "mouse4302probe", "mouse430a2", "mouse430a2cdf", "mouse430a2probe", 
             "rae230a", "rae230acdf", "rae230aprobe", "rat2302", "rat2302cdf", "rat2302probe", 
             "rgu34a", "rgu34acdf", "rgu34aprobe"))

  # Other organisms, arabidopsis, C. elegans, drosophila, xenopus
  biocLite(c("ag", "agcdf", "agprobe", "ath1121501", "ath1121501cdf", "ath1121501probe", "celegans", "celeganscdf", "celegansprobe",
             "drosgenome1", "drosgenome1cdf", "drosgenome1probe", "drosophila2", "drosophila2cdf", "drosophila2probe",
             "test3probe", "xenopuslaevis", "xenopuslaeviscdf", "xenopuslaevisprobe", "zebrafish", "zebrafishcdf", "zebrafishprobe",
             "yeast2", "yeast2cdf", "yeast2probe", "test3cdf"))
  biocLite(c("hgu95a", "hgu95acdf", "hgu95aprobe", "hgu95av2", "hgu95av2cdf", "hgu95av2probe"))

  # Alternative annotations
  biocLite(c(
  "hs133ahsensg", "hs133ahsensgcdf", "hs133ahsensgprobe", "hs133av2hsensg", "hs133av2hsensgcdf", "hs133av2hsensgprobe", 
  "hs133phsensg", "hs133phsensgcdf", "hs133phsensgprobe", "mm430mmensg", "mm430mmensgcdf", "mm430mmensgprobe",
  "mm74av1mmensg", "mm74av1mmensgcdf", "mm74av1mmensgprobe", "mm74av2mmensg", "mm74av2mmensgcdf", "mm74av2mmensgprobe",
  "rn230arnensg", "rn230arnensgcdf", "rn230arnensgprobe", "rn230rnensg", "rn230rnensgcdf", "rn230rnensgprobe",
  "rn34arnensg", "rn34arnensgcdf", "rn34arnensgprobe"
  )) 

  # Other organisms with no annotations
  biocLite(c("barley1cdf", "barley1probe", "bovinecdf", "bovineprobe", "canine2cdf", "canine2probe", "caninecdf", "canineprobe",
             "chickencdf", "chickenprobe", "citruscdf", "citrusprobe", "maizecdf", "maizeprobe", "medicagocdf", "medicagoprobe",
             "plasmodiumanophelescdf", "plasmodiumanophelesprobe", "poplarcdf", "poplarprobe", "porcinecdf", "porcineprobe",
             "rhesuscdf", "rhesusprobe", "ricecdf", "riceprobe", "soybeancdf", "soybeanprobe", "sugarcanecdf", "sugarcaneprobe",
             "tomatocdf", "tomatoprobe", "vitisviniferacdf", "vitisviniferaprobe", "wheatcdf", "wheatprobe"))

  # Annotation packages for Agilent
  biocLite(c("hgug4100a", "hgug4101a", "hgug4110b", "hgug4111a", "hgug4112a"))
  biocLite(c("rgug4105a", "rgug4130a"))
  biocLite(c("mgug4104a", "mgug4120a", "mgug4121a", "mgug4122a"))

  # Annotation packages for Illumina
  biocLite(c("illuminaHumanv1", "illuminaHumanv2", "illuminaMousev1", "lumiHumanV1", "lumiHumanV2", "lumiMouseV1", "lumiRatV1"))

  # Extra packages
  # Download: http://addictedtor.free.fr/packages/
  # Needed: fpc, A2R
  # Some annotation packages (Illumina ProbeIDs and Affymetrix exon arrays) are available on a disk.


---------------------------------------------------------------------------------------------------------------------------
