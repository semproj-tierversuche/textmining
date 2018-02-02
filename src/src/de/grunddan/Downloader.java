package de.grunddan;
import java.io.*;
import java.util.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;

public class Downloader {
	public static File idLIst = new File("C:\\Users\\X250\\Documents\\Tierversuche\\testdaten\\daniel butzke\\pubmed_id_list.csv");
	public static String template = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?db=pubmed&id=<HOHO>&retmode=xml";

	public static void main(String[] args) throws MalformedURLException, IOException {
		List<String> ids = Files.readAllLines(idLIst.toPath());
		
		
		for(String id : ids ) {
			System.out.println(id);
			URL url = new URL(template.replaceFirst("<HOHO>", id));

		
			
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
			
				String line;
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(id + ".raw"))) {
					while((line = reader.readLine()) != null) {
												
							writer.write(line);
							writer.newLine();
					}
				}
			}
		}

	}

}
