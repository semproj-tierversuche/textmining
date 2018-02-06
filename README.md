# textmining  
---

### What do we do?:  
take BioC as input via the stdIn  
processes it through our Pipeline  
returns BioC as output to the stdOut  

In the output we have annotated the Title and abstract with UMLS tags and IDs.
We also marked the sentence which most likly describes the experimental purpose.  
  
  
### Dependencies:  
- jdom-2.0.6
- mmapi-2.0 --- Metamap API  
  
store both dependencies under the assets folder  

- a running Metamap Server

### how to build:  
cd into the src folder  
javac -classpath "assets/*" *.java  
jar cvfm ../tm.jar MANIFEST.MF *.class *.properties assets/BioC.dtd assets/stopwords.txt assets/dictionary.txt  

### how to run: 
Starting Metamap:  
- start the skrmed Metamap Server  ( ./bin/skrmedpostctl start )  
- start the Metamap Server (./bin/mmserver16 )  
  
In a new Terminal window start our program:  
- java -jar tm.jar -tm ( >> output file)  
  
write the BioC xml into the stdIn. 
End it with <endAll/> in a new line.  
