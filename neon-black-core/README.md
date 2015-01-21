#Neon Black Gazetteer

Neon Black is location detection/resolution application based loosely on Berico Technologies award winning [CLAVIN](https://github.com/Berico-Technologies/CLAVIN) project.

Neon Black 

* Uses SOLR instead of Lucene
* Detects latitudes and longitudes (decimal degrees and DMS)
* Detects MGRS (Military Grid Reference System) 
* Has an available Tika front-end
* Output as JSON or "pretty print" strings

Neon Black utilizes

* Stanford NER for location detection
* [OpenSextant](http://www.opensextant.org) for lat/lon/MGRS detection
* [Apache Tika](http://tika.apache.org) for text extraction from files
* Solr Spatial for point/polygon intersections and point "within distance of" queries
* Various Apache Commons libraries
* Gazetteer data from [GeoNames.org](http://www.geonames.org/)

#Setup

First, get the latest Geonames gazetteer file from [Geonames.org](http://download.geonames.org/export/dump/). Scroll to close to the bottom and download allCountries.zip.  When you have it
downloaded, unzip it and place the **UNZIPPED** file in the src/main/resources/ directory and rebuild the project.

Neon Black was developed against Solr 4.9.0 and the included resources are setup for the "new" Solr 4.4+ format.

We assume you have a version of Solr 4.4+ running somewhere, if you don't, we recommend the open source [Heliosearch Distribution for Solr (HDS)](http://heliosearch.com/heliosearch-distribution-for-solr/)
which is what we used to develop Neon Black.  
Neon Black performs Solr Spatial queries against polygons and multipolygons that represent country and US State shapes. In order for Solr Spatial to be able to work with polygons and 
multi-polygons you must add the Java Topology Suite (JTS) jar files to Solr. You can get the latest JTS from [Vivid Solutions](http://www.vividsolutions.com/jts/JTSHome.htm). 
If you are using Solr under TomCat or the Heliosearch package, the solr.war file has been exploded.  Place a copy of jts-x.xx.jar and jtsio-x.xx.jar in the webapps/solr/WEB_INF/lib directory of your Tomcat installation.  
If you are using an app server that **does not** explode solr.war, you have to:  

* Rename solr.war to solr.zip
* Unzip/extract solr.zip
* Place the jts jars in the webapps/solr/WEB_INF/lib directory
* Re-zip 
* Rename to solr.war
* Put your new solr.war where it belongs in your app server

Neon Black deploys two Solr cores, one called geonames which contains the gazetteer data, and one called geodata which holds the country and state polygons. Go into the src/main/resources/solr 
directory of the code distribution and grab the two files and two folders, and copy them to your SOLR_HOME directory.  If you already have a working
Solr installation, just grab the two folders and put them into your SOLR_HOME.  

* From your IDE, run /src/main/java/com/apexxs/neonblack/setup/Setup.
* The Setup utility reports status to System.out.

This is going to take a while to run, as it first loads the GeoNames core, then the country/shape polygons.  On an Intel I5 processor with 4 cores and 8 GB RAM (Win 7 64-bit, Oracle JDK1.7.0_xx), it takes about 20 minutes.  
Don't be alarmed when it seems to hang loading the polygon for Greenland, that one takes a long time.


#Configuration

Run time configuration for Neon Black is obtained from the NB.properties file in src/main/resources directory.  These properties allow quite a bit of flexibility in how well  
locations are detected and resolved.

##NB.properties
    ner.detector.stanford.model = english.all.3class.distsim.crf.ser.gz
    ner.detector.removeStopWords = true
    ner.detector.removeDemonyms = true
    ner.detector.replaceSynonyms = true
    solr.url = http://localhost:8983/solr/
    solr.search.maxHits = 100
    solr.populationSort = true
    coords.proximalDistanceKM = 10
    coords.numProximalHits = 10
    results.numAlternates = 5
    results.format = json
    
###ner.detector.stanford.model
The Stanford NER distributes with several different detection models, and some brave souls may wish to train their own model.  If you want to use a different model, specify its name here.
###ner.detector.removeStopWords
This is a pre-Solr resolution stopword removal, not to be confused with Solr index/query stopword removal.  The contents of src/main/resources/Stopwords.txt are used to remove things you  
just don't want the Stanford NER to "detect".  We found that many acronyms starting with "USA" or "AFRI" are identified by the Stanford NER as locations, and of course, most of these don't resolve  
when run against the Solr gazetteer.
###ner.detector.removeDemonyms
A demonym is the name used for the people who live in a particular county, for instance, Iranian for people who live in Iran.  The Stanford NER likes to identify demonyms as locations.  We  
"borrowed" this demonym removal list from the folks at Berico under their [Clavin NERD](https://github.com/Berico-Technologies/CLAVIN-NERD) project.  
Most of the time, demonyms will not resolve, and should be removed.  However, if for your needs, you want a resolution for Iran from Iranian, you could add entries to the Synonyms.txt file and enable ner.detector.replaceSynonyms  
as described below.
###ner.detector.replaceSynonyms
Again, this is a pre-Solr resolution synonym replacement. This synonym replacement is basically to help "transform" a location detected by the Stanford NER to something that resolves more easily in the 
Solr gazetteer. Take a look at the src/main/resources/Synonyms.txt file to see the default synonym replacements.  **NOTE** that again, this is not the Solr index/query time synonym replacement, these 
replacements happen prior to the location detections being sent to Solr, and it is the synonym that is "resolved" by the Solr query.
###solr.url
The URL of your Solr installation.
###solr.search.maxHits
The maximum number of hits returned by the Solr resolution query. 
###solr.populationSort
The Solr query results for a location are, by default, sorted by population and Solr score.  The population sort works well because according to the [smart guys who do computational linguistics](http://www.aclweb.org/anthology/P13-1144), 
the population sort is correct about 80% of the time.  For instance, if you see the text "Next summer we are going to Paris", it is highly likely that Paris is referring to Paris, France, which
happens to have the highest population of all of the "Paris" entries in the GeoNames gazetteer.  If you set this property to "false", the results will be returned sorted by Solr score only.
###coords.proximalDistanceKM
When Neon Black encounters an MGRS, or a lat/lon it will return a number of proximal locations from the gazetteer.  This is to provide context to the coordinate detection.  This is a Solr
spatial search, and represents the distance from the detected coordinate to query upon (query is by bounding box).
###coords.numProximalHits
The number of places within the proximal distance to display as results.
###results.numAlternates
If a location resolution query returns more than one possible place, this is the number of alternate places to display as results.
###results.format
Neon Black can return results either as "pretty print" strings or as json strings.  Any property value other than "json" will return the "pretty print" strings.
#Using Neon Black
The com.apexxs.neonblack.NeonBlack class can be used as the primary interface into NeonBlack.  Neon Black basically takes a string as input and produces a List<String> as output.   A file name
can be provided and run through the Apache Tika text extractor.  
###Output
Neon Black, by default produces a List<String> of json formatted text with one detection per List<String> element.  The first line which starts with "Detection" is the
highest scoring candidate, and the "Alternates" are other possible locations. 
 
     {"Detection":"Wilmington","Gazetteer name":"Wilmington","LonLat":"-75.54659 39.74595","Country":"US","Feature code":"PPLA2","Admin1":"DE","Num Occurrences":1,
     "Alternates":[{"Name":"City of Wilmington","LonLat":"-75.52844 39.73485","Country":"US","Feature code":"ADMD","Admin1":"DE"},
     {"Name":"Wilmington Manor","LonLat":"-75.58437 39.68678","Country":"US","Feature code":"PPL","Admin1":"DE"},
     {"Name":"Wilmington College","LonLat":"-75.54909 39.74039","Country":"US","Feature code":"SCH","Admin1":"DE"},
     {"Name":"Wilmington Hospital","LonLat":"-75.54964 39.75178","Country":"US","Feature code":"HSP","Admin1":"DE"},
     {"Name":"Wilmington Hundred","LonLat":"-75.58298 39.68345","Country":"US","Feature code":"ADMD","Admin1":"DE"}]}

This detection correctly resolved Wilmington, Delaware, but since the resolution was ambiguous, it returns the specified number of alternates.  

A single detection
   
     {"Detection":"New York City","Gazetteer name":"New York City","LonLat":"-74.00597 40.71427","Country":"US","Feature code":"PPL","Admin1":"NY","Num Occurrences":1}

usually indicates that Neon Black had enough context information to uniquely identify a location, so "alternates" are not returned.


A lat/lon or MGRS detection returns a string similar to this...

    {"Detection":"14TQL4893382995","Gazetteer name":"14TQL4893382995","LonLat":"-96.02400338029048 41.35999793484829","Country":"US","Feature code":null,"Admin1":"NE","Num Occurrences":1,
    "Within 10 Km":[{"Name":"Interchange 1","LonLat":"-95.93585 41.34666","Country":"US","Feature code":"RDJCT","Admin1":"IA"},
    {"Name":"Mormon Bridge","LonLat":"-95.95724 41.34555","Country":"US","Feature code":"BDG","Admin1":"IA"},
    {"Name":"Cabannes Trading Post (historical)","LonLat":"-95.95862 41.37555","Country":"US","Feature code":null,"Admin1":"NE"},
    {"Name":"Calvary Foursquare Gospel Church","LonLat":"-95.95974 41.33694","Country":"US","Feature code":"CH","Admin1":"NE"},
    {"Name":"Campus Managers Office-Instructional Building","LonLat":"-95.95835 41.31222","Country":"US","Feature code":"BLDG","Admin1":"NE"},
    {"Name":"Central Park Elementary School","LonLat":"-95.97611 41.30361","Country":"US","Feature code":"SCH","Admin1":"NE"},
    {"Name":"Christian Science Second Church","LonLat":"-96.06141 41.2775","Country":"US","Feature code":"CH","Admin1":"NE"},
    {"Name":"Church of God of Prophecy","LonLat":"-95.98863 41.2825","Country":"US","Feature code":"CH","Admin1":"NE"},
    {"Name":"Church of the Living God","LonLat":"-95.96974 41.28833","Country":"US","Feature code":"CH","Admin1":"NE"},
    {"Name":"Church of God in Christ Congregational","LonLat":"-95.98446 41.30333","Country":"US","Feature code":"CH","Admin1":"NE"}]}
    
Here, Neon Black detected an MGRS, it got the lat/lon and did a spatial query against the geodata index to get the country and state code, then it did a spatial query for 10 places within
10 kilometers of the detected point.  The number of proximal places and the proximity distance can be configured in the properties files as described above.

If json is not your desired output format, the "pretty print" string format will be returned.

    Detection: Wilmington	Gazetteer name: Wilmington	LonLat: -75.54659 39.74595	Country: US	Feature code: PPLA2	Admin1: DE	Num Occurrences: 1
    	Alternate:	 Gazetteer name: City of Wilmington	LonLat: -75.52844 39.73485	Feature code: ADMD	Country code: US	Admin1: DE	
    	Alternate:	 Gazetteer name: Wilmington Manor	LonLat: -75.58437 39.68678	Feature code: PPL	Country code: US	Admin1: DE	
    	Alternate:	 Gazetteer name: South Wilmington	LonLat: -75.54409 39.72928	Feature code: PPL	Country code: US	Admin1: DE	
    	Alternate:	 Gazetteer name: Wilmington Academy	LonLat: -75.66576 39.7515	Feature code: SCH	Country code: US	Admin1: DE	
    	Alternate:	 Gazetteer name: Wilmington College	LonLat: -75.54909 39.74039	Feature code: SCH	Country code: US	Admin1: DE	
      
If you want to programatically use the "pretty print" strings

    Break on newline
    Break on tab
    Break on colon 
    
#Notes on Neon Black  
We have run over 3000 text files through Neon Black and found that it's performance is comparable to some commercial products.  The Stanford NER tends to throw more "false alarm" detections
than the commercial product, but these can be reduced by adding to the Stopwords list.  Locations that you encounter frequently that are not resolved correctly by Neon Black can be resolved
correctly by using the Synonyms list.  We found that a short iterative sequence of running text through Neon Black and adding annoyances and "klinkers" to the Stopwords and Synonmyms list quickly
improved our resolution performance.

Location detection in Neon Black is subject to the performance of the Stanford NER.  We found the Stanford NER to be pretty good at finding city and country names, but it often does not find
FIPS or ISO codes.  It's also probably not going to find the name of your favorite trout stream in Colorado.

#Licensing
All of the Neon Black code is licensed under the Apache License, Version 2.0, however the Stanford NER is licensed under the GNU General License, Version 2, and that makes Neon Black
subject to that license agreement.

The default country shapes loaded into the geodata index were obtained from [thematicmapping.org](http://www.thematicmapping.org/downloads/world_borders.php) and are licensed under a Creative Commons Attribution-Share Alike license. 

State shapes were obtained from the [spatial-solr-sandbox](https://github.com/ryantxu/spatial-solr-sandbox/blob/master/spatial-demo/data/) and are not attributed.
    
#Acknowledgements
We would like to thank the CLAVIN team at [Berico Technologies](http://www.bericotechnologies.com/). CLAVIN served as the inspiration for this project, and we borrowed more than a few ideas 
from them.

Also, a big thanks to David Smiley for all the work he's done on Spatial4J and Solr Spatial, as well as his team that developed the [OpenSextant](http://www.opensextant.org) project.
    