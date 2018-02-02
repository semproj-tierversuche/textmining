package de.grunddan;
import opennlp.tools.sentdetect.*;
import java.io.*;
import java.nio.charset.MalformedInputException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.*;

//zurückgeben: offset wo der Satz im Abstract beginnt, und die Länge

//IDEE für überwachtes lernverfahren: zufällig ne pubmed-id zu einem major subject heading wählen und ähnliche dokumente dazu bestimmen, dann nutzer bewerten lassen
//Here, we / In this study(report, work,  paper, review, article, manuscript) kmbinieren mit dem jeweiligen verb; stemming; zudem erkennen was "this"/"it" bedeutet
//unterschied versuchszweck, -ergebnis?
public class Main2 {
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
		System.err.println(result.size());
		
		return result.toArray(new String[0]);
	}
	
	//es gibt: <AbstractText Label="METHODS" NlmCategory="METHODS"
	public static String[] readTestDir() throws IOException {
		List<String> result = new LinkedList<String>();  
		for (File f : testDir.listFiles()) {
			StringBuilder abstractText = new StringBuilder();
			try {
				for(String l : Files.readAllLines(f.toPath())) {
					
					Pattern p = Pattern.compile("^\\p{Space}*<AbstractText.*>(?:.+)</AbstractText>");
					Matcher m = p.matcher(l);
					if(m.matches()) {
						//nicht die innere Gruppe wg. <AbstracText OBJECTIVE>
						abstractText.append(m.group().replace("<AbstractText>", "").replace("</AbstractText>", ""));
						//kein break: weil es mehrere geben kann
					}
				}
			} catch (MalformedInputException e) {
				System.err.println("CHARSET ERROR " + f.getAbsolutePath());
				continue;
			}
			result.add(abstractText.toString());
		}
		return result.toArray(new String[0]);
	}
	
		
	public static void main(String[] args) throws IOException {
		
		String[] inputLines = readTestDir();
		inputLines = readPubmedFile(new File("C:\\Users\\X250\\Documents\\uni\\tierversuche\\pubmed18n0928.xml"));

		Collection<String> stopwords = new HashSet<String>(); 
		List<String> patternLines = Files.readAllLines(dictionaryFile.toPath());
		List<Pattern> patterns = new LinkedList<Pattern>();
		int countFound = 0;
		int countNotFound = 0;
		
		for(String s : patternLines) {
			patterns.add(Pattern.compile(".*" + s, Pattern.CASE_INSENSITIVE));
		}
		
		try (Scanner scanner = new Scanner(stopwordFile)) {
			while(scanner.hasNext()) {
				stopwords.add(scanner.next());
			}
		}
		
		BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\X250\\Documents\\uni\\tierversuche\\output.log"));
		
		
		try (InputStream modelIn = new FileInputStream("C:\\Users\\X250\\Documents\\uni\\tierversuche\\en-sent.bin")) {
			  SentenceModel model = new SentenceModel(modelIn);
			  SentenceDetectorME detector = new SentenceDetectorME(model);
			  
			int n=1;

			for (int i = 1; i<inputLines.length && i<lineCountLimit; i++) {
				
				String[] sentences = detector.sentDetect(inputLines[i]);
				System.out.print(n++ + "(" + sentences.length + "): ");
				
				boolean found_hereWe = false;
				
				
				for(int j=0; j<sentences.length  && !found_hereWe; j++) {
					for(Pattern p : patterns) {
						Matcher m = p.matcher(sentences[j]);
						if(m.lookingAt()) {
							//TODO: call stopWords.contains()
							System.out.print("OK [" + sentences[j] + "]");
							found_hereWe = true;
							countFound++;
							break;
						}
					}
					
				}
				
				if(!found_hereWe) {
					countNotFound++;
					writer.newLine();
					
					for(int j=0; j<sentences.length; j++) {
						writer.write('\t');
						writer.write(j + " " + sentences[j]);
						writer.newLine();
					}
				}
				
				
				System.out.println();
				
				
				
				
			}
			writer.close();
			System.out.println("Count: " + countFound + " of " + (countFound + countNotFound) + " = " + (double) countFound / (countFound + countNotFound));
			  
		}
	
		
		
		
		
		
	}

}
