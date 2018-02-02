import opennlp.tools.sentdetect.*;

import java.io.*;
import java.util.*;


//IDEE für überwachtes lernverfahren: zufällig ne pubmed-id zu einem major subject heading wählen und ähnliche dokumente dazu bestimmen, dann nutzer bewerten lassen
//Here, we / In this study(report, work,  paper, review, article, manuscript) kmbinieren mit dem jeweiligen verb; stemming; zudem erkennen was "this"/"it" bedeutet
//unterschied versuchszweck, -ergebnis?
public class Main3 {
	static final File testDir = new File("C:\\Users\\X250\\Documents\\uni\\tierversuche\\pubmed_downloads");
	static final File dictionaryFile = new File("C:\\Users\\X250\\eclipse-workspace\\HereWeSatz\\dictionary.txt");
	static final File stopwordFile = new File("C:\\Users\\X250\\eclipse-workspace\\HereWeSatz\\stopwords.txt");
	//static final int lineCountLimit = 100;
	static final int lineCountLimit = Integer.MAX_VALUE;
	
	public static String[] readPubmedFile(File pubmed) throws IOException {
		List<String> result = new LinkedList<String>();
		
		try ( BufferedReader reader = new BufferedReader(new FileReader(pubmed))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				
				if(line.trim().startsWith("<Abstract>")) {
					StringBuilder  s = new StringBuilder();
					line = reader.readLine().trim();
					do {
						s.append(line.replace("<AbstractText>", "").replace("</AbstractText>", ""));
						line = reader.readLine().trim();
						
					} while (!line.startsWith("</Abstract>"));
					result.add(s.toString());
				}
				
			}
			
		}
		System.out.println("*** " + result.size() + " documents from " + pubmed.getAbsolutePath() + " ****");
		
		return result.toArray(new String[0]);
	}
		
	public static void main(String[] args) throws IOException {
		int countFound = 0;
		int countNotFound = 0;
		long startTime = System.currentTimeMillis();
		VersuchszweckChecker checker = new VersuchszweckChecker(dictionaryFile, stopwordFile);
		SentenceDetectorME detector;
		
		try (FileInputStream modelInput = new FileInputStream("C:\\Users\\X250\\Documents\\uni\\tierversuche\\en-sent.bin")) {
			SentenceModel model = new SentenceModel(modelInput);
			detector = new SentenceDetectorME(model);
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\X250\\Documents\\uni\\tierversuche\\output.log"));		
		for(int documentCount=28; documentCount>=16; documentCount--) {
			String[] inputLines = readPubmedFile(new File("C:\\Users\\X250\\Documents\\uni\\tierversuche\\pubmed18n09" + documentCount + ".xml"));
			for (int i = 1; i<inputLines.length && i<lineCountLimit; i++) {
				String[] sentences = detector.sentDetect(inputLines[i]);
				
				String hereWe = checker.getFirstVersuchszweck(sentences);
				if(hereWe != null) {
					countFound++;
				} else {
					countNotFound++;
					writer.newLine();
					for(int j=0; j<sentences.length; j++) {
						writer.write('\t');
						writer.write(j + " " + sentences[j]);
						writer.newLine();
					}
				}
			}
		}
		writer.close();
		
		long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
		int totalDocumentCount = countFound + countNotFound;
		System.out.println();
		System.out.println("Count: " + countFound + " of " + totalDocumentCount + " = " + 100.0 * countFound / totalDocumentCount + " percent of documents have a detected HereWeSentence");
		System.out.println("Current speed: " + 1000.0 * elapsedTime / totalDocumentCount + " seconds per 1k documents");
		System.out.println("Estimated runningtime for 27Mio documents: " + (27.0E6 * elapsedTime / totalDocumentCount) / 3600 + " hours");
		System.out.println("Estimation is based on sample size relative to total corpus size: " + 1000.0 * totalDocumentCount / 27.0E6 + " promille");
	}
}