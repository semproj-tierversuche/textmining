# textmining  
---

### What do we do?:  
take BioC as input via the stdIn  
processes it through our Pipeline  
returns BioC as output to the stdOut  

In the output we have annotated the Title and abstract with UMLS tags and IDs.
We also marked the sentence which most likly describes the experimental purpose.  
  
  
### Dependencies:  
Code dependencies:  
- jdom-2.0.6  
- mmapi-2.0 --- Metamap API  
- apache commons-text  
- apache commons-lang  
  
Store the Metamap API unter the 'repo' folder which should be located in the project foler.  
The other ones will be loaded automataclly.  

- a running Metamap Server

### How to build:  
- **cd** into the project foler  
  
- **Before the first build run:**  
                         mvn install:install-file -Dfile=repo/MetamapAPI.jar \  
                         -DgroupId=nlm.nih.gov -DartifactId=metamap.nlm.nih.gov -Dversion=2016v2 \   
                         -Dpackaging=jar  
- **run:** mvn clean compile assembly:single  
  
  
### How to run: 
Starting Metamap:  
- start the skrmed Metamap Server  ( ./bin/skrmedpostctl start )  
- start the Metamap Server (./bin/mmserver16 )  
  
In a new Terminal window start our program:  
- java -jar TextMining.jar -tm ( >> output file)  
  
write the BioC xml into the stdIn.  
End it with \<endAll/> in a new line.  

Make sure the BioC.dtd is in the execution directory. 
