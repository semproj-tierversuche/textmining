package de.grunddan;
import opennlp.tools.sentdetect.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.*;

//zurückgeben: offset wo der Satz im Abstract beginnt, und die Länge

//den sentence splitter trainieren auf den Dokumenten von Daniel Butzke und den Metamap benchmarkdaten MEDLINE_892
//dictionary noch verkleinern per stemming
public class Main {
	static final File speedBenchmarkFile = new File("C:\\Users\\X250\\Documents\\Tierversuche\\testdaten\\MEDLINE_892.txt");
	static final File dictionaryFile = new File("C:\\Users\\X250\\eclipse-workspace\\HereWeSatz\\dictionary.txt");
	static final File stopwordFile = new File("C:\\Users\\X250\\eclipse-workspace\\HereWeSatz\\stopwords.txt");
	//static final int lineCountLimit = 100;
	static final int lineCountLimit = Integer.MAX_VALUE;
	
	
	public static String[] readMetamapSpeedBenchmarkfile() throws IOException {
		return Files.readAllLines(speedBenchmarkFile.toPath()).toArray(new String[0]);
	}
	
		
	public static void main(String[] args) throws IOException {
		String[] inputLines = readMetamapSpeedBenchmarkfile();
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
		
		
		try (InputStream modelIn = new FileInputStream("C:\\Users\\X250\\Documents\\Tierversuche\\en-sent.bin")) {
			  SentenceModel model = new SentenceModel(modelIn);
			  SentenceDetectorME detector = new SentenceDetectorME(model);
			  
			int n=1;

			for (int i = 1; i<inputLines.length && i<lineCountLimit; i += 3) {
				
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
					System.out.println();
					for(int j=0; j<sentences.length; j++) {
						System.out.print('\t');
						System.out.println(j + " " + sentences[j]);
					}
				}
				
				
				System.out.println();
				
				
				
				
			}
			System.out.println("Count: " + countFound + " of " + (countFound + countNotFound) + " = " + (double) countFound / (countFound + countNotFound));
			  
		}
	
		
		
		
		
		
	}

}
