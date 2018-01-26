
import opennlp.tools.sentdetect.*;
import java.io.*;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.*;




public class Main {
	static final File speedBenchmarkFile = new File("C:\\Users\\X250\\Documents\\Tierversuche\\MEDLINE_892.txt");
	static final File pubmedFile = new File("C:\\Users\\X250\\Documents\\Tierversuche\\pubmed18n0928.xml");
	
	public static String[] readMetamapSpeedBenchmarkfile() throws IOException {
		return Files.readAllLines(speedBenchmarkFile.toPath()).toArray(new String[0]);
	}
	
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
	
	public static void main(String[] args) throws IOException {
		String[] lines = readMetamapSpeedBenchmarkfile(); 
		lines = readPubmedFile();
		Pattern pattern = Pattern.compile(".*(In this (study|review)|To test the|We (developed a|have demonstrated that)|We report the)", Pattern.CASE_INSENSITIVE);
		int limit = Integer.MAX_VALUE;
		
		
		try (InputStream modelIn = new FileInputStream("C:\\Users\\X250\\Documents\\Tierversuche\\en-sent.bin")) {
			  SentenceModel model = new SentenceModel(modelIn);
			  
			  SentenceDetectorME detector = new SentenceDetectorME(model);
			  
			int n=1;
			int count=0;
			for (int i = 1; i<lines.length && i<limit; i += 3) {
				
				String[] sentences = detector.sentDetect(lines[i]);
				System.out.print(n++ + "(" + sentences.length + "): ");
				
				boolean found_hereWe = false;
				
				
				for(int j=0; j<sentences.length  && !found_hereWe; j++) {
					Matcher m = pattern.matcher(sentences[j]);
					if(m.lookingAt()) {
						System.out.print("OK");
						found_hereWe = true;
						count++;
					}
					
				}
				
				if(!found_hereWe) {
					System.out.println();
					for(int j=0; j<sentences.length; j++) {
						System.out.print('\t');
						System.out.println(j + " " + sentences[j]);
					}
				}
				
				
				System.out.println();
				
				
				
				
			}
			System.out.println("Count: " + count);
			  
		}
	
		
		
		
		
		
	}

}
