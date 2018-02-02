package de.grunddan;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


//zur Vorhersage von Mesh-Terms gibt es bereits ne Challenge http://bioasq.org/ (gab es auch vergangene challenges mit papers dazu); Suchbegriff in pubmed: meshable
//zur manuellen Benutzung gibts https://meshb.nlm.nih.gov/MeSHonDemand
//anderer Suchbegriff in Pubmed: Meshlabeler sowie Deepmash; oder auch mal nach BioASQ suchen
public class Stuff {
	static final File pubmedFile = new File("C:\\Users\\X250\\Documents\\Tierversuche\\pubmed18n0928.xml");
	public static String[] readPubmedFile() throws IOException {
		ArrayList<String> res = new ArrayList<String>();
		try(BufferedReader reader = new BufferedReader(new FileReader(pubmedFile))) {
			String line;
			int n=0;
			while((line = reader.readLine()) != null) {
				n++;
				if(line.startsWith("<AbstractText>", 10)) {
					while(!line.endsWith("</AbstractText>")) {
						line += reader.readLine();
					}
					line = line.substring(24, line.length()-15);
					res.add(line);
				}
			}
		}
		return res.toArray(new String[0]);
	}
}


//Pubmed nach Datei: 27Mio-Dokumente -> 300*300 Verzeichnisse mit je 300 Dateien
//darauf auf der obersten Ebene 4 Threads, die dann je 25% der obersten Ebene rekursiv bearbeiten
//schlauer wäre wohl lokal sqllite


//braucht nen XML-Parser in Java
